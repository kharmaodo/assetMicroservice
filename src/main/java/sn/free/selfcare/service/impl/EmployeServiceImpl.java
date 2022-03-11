package sn.free.selfcare.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sn.free.selfcare.domain.Employe;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.helper.CSVImportFileHelper;
import sn.free.selfcare.helper.header.EmployeHeaderEnum;
import sn.free.selfcare.helper.header.LigneHeaderEnum;
import sn.free.selfcare.repository.EmployeRepository;
import sn.free.selfcare.repository.search.EmployeSearchRepository;
import sn.free.selfcare.service.EmployeService;
import sn.free.selfcare.service.GroupeService;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.ServiceService;
import sn.free.selfcare.service.dto.EmployeDTO;
import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.service.dto.ServiceDTO;
import sn.free.selfcare.service.mapper.EmployeMapper;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service Implementation for managing {@link Employe}.
 */
@Service
@Transactional
public class EmployeServiceImpl implements EmployeService {

    private final Logger log = LoggerFactory.getLogger(EmployeServiceImpl.class);

    private final EmployeRepository employeRepository;

    private final EmployeMapper employeMapper;

    private final EmployeSearchRepository employeSearchRepository;

    @Autowired
    private LigneService ligneService;

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private ServiceService serviceService;

    public EmployeServiceImpl(EmployeRepository employeRepository, EmployeMapper employeMapper, EmployeSearchRepository employeSearchRepository) {
        this.employeRepository = employeRepository;
        this.employeMapper = employeMapper;
        this.employeSearchRepository = employeSearchRepository;
    }

    @Override
    public EmployeDTO save(EmployeDTO employeDTO) {
        log.debug("Request to save Employe : {}", employeDTO);
        Employe employe = employeMapper.toEntity(employeDTO);
        employe = employeRepository.save(employe);
        EmployeDTO result = employeMapper.toDto(employe);
        employeSearchRepository.save(employe);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeDTO> findAll() {
        log.debug("Request to get all Employes");
        return employeRepository.findAll().stream()
            .map(employeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeDTO> findOne(Long id) {
        log.debug("Request to get Employe : {}", id);
        return employeRepository.findById(id)
            .map(employeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employe : {}", id);
        Optional<Employe> optionalEmploye = employeRepository.findById(id);
        if (optionalEmploye.isPresent()) {
            Employe employe = optionalEmploye.get();

            // Enlever l'employé de son groupe s'il en dispose déjà.
            if (employe.getGroupe() != null)
                employe.setGroupe(null);

            // Rendre sa ligne à nouveau disponible s'il en posséde une.
            if (employe.getLigne() != null)
                employe.setLigne(null);

            // Procéder à la suppression logique.
            employe.setStatus(ObjectStatus.ARCHIVED);
            employe = employeRepository.save(employe);
            employeSearchRepository.save(employe);
        }
    }

    @Override
    public Page<EmployeDTO> searchEmployes(Long clientId, String query, Pageable pageable) {

        Page<Employe> page;
        if (query != null && !query.isEmpty()) {
            page = employeRepository.searchEmployes(clientId, ObjectStatus.ARCHIVED, "%" + query + "%", pageable);
        } else {
            page = employeRepository.searchEmployes(clientId, ObjectStatus.ARCHIVED, pageable);
        }
        return page.map(employeMapper::toDto);
    }

    @Override
    public List<EmployeDTO> findAllByEmail(String email, boolean onlyActive) {
        log.debug("Request to get all employees : {} ", email);
        if (onlyActive) {
            return StreamSupport
                .stream(employeRepository.findByEmail(email).spliterator(), false)
                .filter(employe -> employe.getStatus() != null && employe.getStatus().equals(ObjectStatus.ACTIVE))
                .map(employeMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        }

        return StreamSupport
            .stream(employeRepository.findByEmail(email).spliterator(), false)
            .map(employeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<EmployeDTO> findAllByClientId(Long clientId) {
        log.debug("Request to get all employees : {} ", clientId);
        return StreamSupport
            .stream(employeRepository.findByClientId(clientId).spliterator(), false)
            .map(employeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeDTO> findEmployesWithOffreByGroupe(Long groupeId) {
        log.debug("Request to find Employes with offres by groupe: {}", groupeId);
        return employeRepository.findEmployesWithOffreByGroupe(groupeId, ObjectStatus.ACTIVE).stream()
            .map(employeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    //TODO : MD : Audit : Plus de 40 lignes de codes 
    //TODO : MD : Traitement des fichier d'import : Perf
    //TODO : MD : Traitement des fichier d'import : Perf
    @Override
    public void saveEmployesFromCSV(Long clientId, MultipartFile file) throws IOException {

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), CSVImportFileHelper.CHARSET_NAME));
        CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
            .withDelimiter(CSVImportFileHelper.SEPARATOR)
            .withFirstRecordAsHeader()
            .withIgnoreHeaderCase()
            .withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();

        log.debug("csvRecords.size() = {}", csvRecords.size());
        if (csvRecords.size() <= 0) {
            throw new BadRequestAlertException("Empty file or incorrectly formatted data: " + file.getOriginalFilename() + "!", "fileImport", "fileImport.failure.empty");
        }

        csvRecords.stream().forEach(csvRecord -> {
            if (CSVImportFileHelper.checkRecordForEmploye(csvRecord)) {

                String ligneNumero = csvRecord.get(EmployeHeaderEnum.LIGNE.name());
                boolean numeroNotValid = !StringUtils.isNumeric(ligneNumero) || StringUtils.length(ligneNumero) != 9
                    || (!ligneNumero.startsWith("76") && !ligneNumero.startsWith("77") && !ligneNumero.startsWith("70")
                    && !ligneNumero.startsWith("78") && !ligneNumero.startsWith("75") && !ligneNumero.startsWith("32"));
                if(numeroNotValid) {
                    throw new BadRequestAlertException("Le numéro de la ligne " + ligneNumero.trim() + " doit contenir 9 chiffres et commence par 76, 77, 70, 75, 78 ou 32!", "fileImport", "fileImport.failure.numeroLigneNotValid");
                }

                Optional<LigneDTO> optionalLigneDTO = ligneService.findByClientAndNumero(clientId, ligneNumero.trim());
                if(!optionalLigneDTO.isPresent()) {
                    throw new BadRequestAlertException("La ligne " + ligneNumero.trim() + " de l'employe à importer n'existe pas!", "fileImport", "fileImport.failure.EmployeLigne");
                }

                if(isLigneAffectedToAnEmploye(optionalLigneDTO.get().getId())) {
                    throw new BadRequestAlertException("La ligne " + ligneNumero.trim() + " est déjà affecté à un employé existant!", "fileImport", "fileImport.failure.EmployeLigneAffecte");
                }

                String groupeName = csvRecord.get(EmployeHeaderEnum.GROUPE.name());
                Optional<GroupeDTO> optionalGroupeDTO = groupeService.findByClientAndNom(clientId, groupeName.trim());
                if(!optionalGroupeDTO.isPresent()) {
                    throw new BadRequestAlertException("Le groupe " + groupeName.trim() + " de l'employe à importer n'existe pas!", "fileImport", "fileImport.failure.EmployeGroupe");
                }

                EmployeDTO employeDTO = new EmployeDTO();

                employeDTO.setPrenom(csvRecord.get(EmployeHeaderEnum.PRENOM.name()));
                employeDTO.setNom(csvRecord.get(EmployeHeaderEnum.NOM.name()));

                if(csvRecord.isSet(EmployeHeaderEnum.EMAIL.name())) {
                    employeDTO.setEmail(csvRecord.get(EmployeHeaderEnum.EMAIL.name()));
                }

                if(csvRecord.isSet(EmployeHeaderEnum.POSTE.name())) {
                    employeDTO.setFonction(csvRecord.get(EmployeHeaderEnum.POSTE.name()));
                }
                employeDTO.setLigneId(optionalLigneDTO.get().getId());
                employeDTO.setGroupeId(optionalGroupeDTO.get().getId());
                employeDTO.setStatus(ObjectStatus.ACTIVE);
                employeDTO.setClientId(clientId);

                save(employeDTO);

                LigneDTO ligneDTO = optionalLigneDTO.get();
                ligneDTO.setServices(new HashSet<>(serviceService.findByServiceDefault(true)));
                ligneService.save(ligneDTO);

            } else {
                throw new BadRequestAlertException("Fail to parse CSV file: une rangée du fichier est mal formatté", "fileImport", "fileImport.failure.parse");
            }
        });
    }

    private boolean isLigneAffectedToAnEmploye(Long ligneId) {
        Optional<EmployeDTO> optionalEmployeDTO = employeRepository.findByLigne(ligneId).map(employeMapper::toDto);
        if(optionalEmployeDTO.isPresent()) return true;
        else return false;
    }
}
