package sn.free.selfcare.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sn.free.selfcare.domain.Ligne;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.helper.CSVImportFileHelper;
import sn.free.selfcare.helper.header.EmployeHeaderEnum;
import sn.free.selfcare.helper.header.LigneHeaderEnum;
import sn.free.selfcare.repository.LigneRepository;
import sn.free.selfcare.repository.search.LigneSearchRepository;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.service.mapper.LigneMapper;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;
import sn.free.selfcare.web.rest.errors.LigneAlreadyAttribuedException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Ligne}.
 */
@Service
@Transactional
public class LigneServiceImpl implements LigneService {

    private final Logger log = LoggerFactory.getLogger(LigneServiceImpl.class);

    private final LigneRepository ligneRepository;

    private final LigneMapper ligneMapper;

    private final LigneSearchRepository ligneSearchRepository;

    public LigneServiceImpl(LigneRepository ligneRepository, LigneMapper ligneMapper, LigneSearchRepository ligneSearchRepository) {
        this.ligneRepository = ligneRepository;
        this.ligneMapper = ligneMapper;
        this.ligneSearchRepository = ligneSearchRepository;
    }

    @Override
    public LigneDTO save(LigneDTO ligneDTO) {
        log.debug("Request to save Ligne : {}", ligneDTO);
        Ligne ligne = ligneMapper.toEntity(ligneDTO);
        ligne = ligneRepository.save(ligne);
        LigneDTO result = ligneMapper.toDto(ligne);
        ligneSearchRepository.save(ligne);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> findAll() {
        log.debug("Request to get all Lignes");
        return ligneRepository.findAll().stream()
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get all the lignes where Employe is {@code null}.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> findAllWhereEmployeIsNull() {
        log.debug("Request to get all lignes where Employe is null");
        return StreamSupport
            .stream(ligneRepository.findAll().spliterator(), false)
            .filter(ligne -> ligne.getEmploye() == null)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the lignes of client where Employe is {@code null}.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> findAllByClientIdAndOffreIsNull(Long clientId, Long offreId) {
        log.debug("Request to get all lignes of client {} by Offre id {} or Offre is null", clientId, offreId);
        return StreamSupport
            .stream(ligneRepository.findAllByClientId(clientId).spliterator(), false)
            .filter(ligne -> ligne.getOffre() == null || ligne.getOffre().getId() == offreId)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the lignes of client.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> findAllByClientId(Long clientId) {
        log.debug("Request to get all lignes of client {}", clientId);
        return StreamSupport
            .stream(ligneRepository.findAllByClientId(clientId).spliterator(), false)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the lignes of offre.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> findAllByOffreId(Long offreId) {
        log.debug("Request to get all lignes of offre {} : ", offreId);
        return StreamSupport
            .stream(ligneRepository.findAll().spliterator(), false)
            .filter(ligne -> ligne.getOffre() != null && ligne.getOffre().getId() == offreId)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LigneDTO> findOne(Long id) {
        log.debug("Request to get Ligne : {}", id);
        return ligneRepository.findById(id)
            .map(ligneMapper::toDto);
    }

    @Override
    public Optional<LigneDTO> findOneWithServicesById(Long id) {
        log.debug("Request to find Ligne with services: {}", id);
        return ligneRepository.findOneWithServicesById(id)
            .map(ligneMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Ligne : {}", id);
        Optional<Ligne> optionalLigne = ligneRepository.findById(id);
        if (optionalLigne.isPresent()) {
            Ligne ligne = optionalLigne.get();

            // Verifier si la ligne est déjà attribuée à un employé
            if (ligne.getEmploye() != null) {
                throw new LigneAlreadyAttribuedException();
            }

            // Procéder à la suppression logique si non attribuée
            ligne.setStatus(ObjectStatus.ARCHIVED);
            ligne = ligneRepository.save(ligne);
            ligneSearchRepository.save(ligne);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneDTO> search(String query) {
        log.debug("Request to search Lignes for query {}", query);
        return StreamSupport
            .stream(ligneSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(ligneMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Page<LigneDTO> findLignesByClientAndQuery(Long clientId, String query, Pageable pageable) {

        Page<Ligne> page;
        if (query != null && !query.isEmpty()) {
            page = ligneRepository.findLignesByClientAndQuery(clientId, ObjectStatus.ARCHIVED, "%" + query + "%", pageable);
        } else {
            page = ligneRepository.findLignesByClient(clientId, ObjectStatus.ARCHIVED, pageable);
        }
        return page.map(ligneMapper::toDto);
    }

    @Override
    public List<LigneDTO> findAllByNumero(String numero, boolean onlyActive) {
        log.debug("Request to get all lignes : {} ", numero);
        if (onlyActive) {
            return StreamSupport
                .stream(ligneRepository.findByNumero(numero).spliterator(), false)
                .filter(ligne -> ligne.getStatus() != null && ligne.getStatus().equals(ObjectStatus.ACTIVE))
                .map(ligneMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        }
        return StreamSupport
            .stream(ligneRepository.findByNumero(numero).spliterator(), false)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<LigneDTO> findAllByImsi(String imsi, boolean onlyActive) {
        log.debug("Request to get all lignes : {} ", imsi);
        if (onlyActive) {
            return StreamSupport
                .stream(ligneRepository.findByImsi(imsi).spliterator(), false)
                .filter(ligne -> ligne.getStatus() != null && ligne.getStatus().equals(ObjectStatus.ACTIVE))
                .map(ligneMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        }

        return StreamSupport
            .stream(ligneRepository.findByImsi(imsi).spliterator(), false)
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<LigneDTO> findDistinctByNumeroOrImsi(String numero, String imsi) {
        return ligneRepository.findDistinctByNumeroOrImsi(numero, imsi)
            .stream()
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    //TODO : MD : Audit : Plus de 40 lignes de codes 
    //TODO : MD : Audit : Tests de valeurs API non exploités
    //TODO : MD : Audit : Rendre abstraite l'export CSV
    public byte[] export(Long clientId) throws IOException {
        byte[] lignesData = null;
        log.debug("REST request to get all Lignes by client : {}", clientId);

        if (clientId == null) {
            log.warn("Client id is required : {}", clientId);
            return lignesData;
        }

        List<LigneDTO> lignes = findAllByClientId(clientId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lignes");
        Row rowHeader = sheet.createRow(0);
        Cell cell0 = rowHeader.createCell(0);
        cell0.setCellValue("Numéro de téléphone");
        Cell cell1 = rowHeader.createCell(1);
        cell1.setCellValue("IMSI");

        int rowIndex = 1;
        for (LigneDTO ligne : lignes) {
            Row row = sheet.createRow(rowIndex++);
            cell0 = row.createCell(0);
            cell0.setCellValue(ligne.getNumero());
            cell1 = row.createCell(1);
            cell1.setCellValue(ligne.getImsi());
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            lignesData = baos.toByteArray();
            baos.flush();
            workbook.close();
        } catch (IOException e) {
            throw e;
        }

        return lignesData;
    }
    
    //TODO : MD : Audit : Plus de 40 lignes de codes 
    //TODO : MD : Traitement des fichier d'import : Perf
    @Override
    public void saveLignesFromCSV(Long clientId, MultipartFile file) throws IOException {

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
            if (CSVImportFileHelper.checkRecordForLigne(csvRecord)) {

                String ligneNumero = csvRecord.get(LigneHeaderEnum.NUMERO.name());
                boolean numeroNotValid = !StringUtils.isNumeric(ligneNumero) || StringUtils.length(ligneNumero) != 9
                    || (!ligneNumero.startsWith("76") && !ligneNumero.startsWith("77") && !ligneNumero.startsWith("70")
                    && !ligneNumero.startsWith("78") && !ligneNumero.startsWith("75") && !ligneNumero.startsWith("32"));
                if(numeroNotValid) {
                    throw new BadRequestAlertException("Le numéro de la ligne " + ligneNumero.trim() + " doit contenir 9 chiffres et commence par 76, 77, 70, 75, 78 ou 32!", "fileImport", "fileImport.failure.numeroLigneNotValid");
                }

                String ligneImsi = csvRecord.get(LigneHeaderEnum.IMSI.name());
                boolean imsiLigneNotValid = !StringUtils.isNumeric(ligneImsi) || StringUtils.length(ligneImsi) != 15 || !ligneImsi.startsWith("60802");
                if(imsiLigneNotValid) {
                    throw new BadRequestAlertException("L'imsi de la ligne " + ligneImsi.trim() + " doit contenir 15 chiffres et commence par 60802!", "fileImport", "fileImport.failure.imsiLigneNotValid");
                }

                LigneDTO ligneDTO = new LigneDTO();
                ligneDTO.setNumero(ligneNumero);
                ligneDTO.setImsi(ligneImsi);
                ligneDTO.setStatus(ObjectStatus.ACTIVE);
                ligneDTO.setClientId(clientId);
                save(ligneDTO);
            } else {
                throw new BadRequestAlertException("Fail to parse CSV file: une rangée du fichier est mal formatté", "fileImport", "fileImport.failure.parse");
            }
        });
    }

    @Override
    public List<LigneDTO> getLignesToFilter(LigneDTO ligne) {
        return ligneRepository.getLignesToFilter(ligne.getNumero(), ligne.getImsi())
            .stream()
            .map(ligneMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LigneDTO> findByClientAndNumero(Long clientId, String numero) {
        log.debug("Request to get Ligne : {} for client : {}", numero, clientId);
        return ligneRepository.findByClientAndNumero(clientId, numero)
            .map(ligneMapper::toDto);
    }
}
