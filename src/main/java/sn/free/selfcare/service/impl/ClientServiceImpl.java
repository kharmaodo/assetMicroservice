package sn.free.selfcare.service.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.free.selfcare.client.OperationFeignClient;
import sn.free.selfcare.client.UserFeignClient;
import sn.free.selfcare.domain.Client;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.repository.ClientRepository;
import sn.free.selfcare.repository.search.ClientSearchRepository;
import sn.free.selfcare.service.ClientService;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.dto.*;
import sn.free.selfcare.service.mapper.ClientMapper;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Client}.
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final ClientSearchRepository clientSearchRepository;

    @Autowired
    private LigneService ligneService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OperationFeignClient operationFeignClient;

    public ClientServiceImpl(ClientRepository clientRepository, ClientMapper clientMapper, ClientSearchRepository clientSearchRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.clientSearchRepository = clientSearchRepository;
    }

    //TODO : MD : Audit : Plus de 40 lignes de codes 
    @Override
    public ClientDTO save(ClientDTO clientDTO) {
        log.debug("Request to save Client : {}", clientDTO);

        // == Saving client ==
        Client client = clientMapper.toEntity(clientDTO);
        client = clientRepository.save(client);

        if(clientDTO.getId() == null) {

            // == Saving the client admin user ==
            AdminClientDTO admin = clientDTO.getAdmin();
            admin.setLogin("admin" + client.getId());
            admin.setClientId(client.getId());
            Map<String, String> response = userFeignClient.createAdminClientUser(admin);

            // == Checking if client admin user is successfully created
            if(!response.get("status").equals("success")) {
                String errorKey = response.get("errorKey");
                String entityName = response.get("entityName");
                String defaultErrorMessage = response.get("errorKey");
                throw new BadRequestAlertException(defaultErrorMessage, entityName, errorKey);
            }
        }

        // Saving lignes ==
        Set<LigneDTO> lignes = clientDTO.getLignes();
        final Long clientId = client.getId();
        lignes.stream().forEach(ligne -> {
            if (this.ligneService.getLignesToFilter(ligne).isEmpty()) {
                ligne.setClientId(clientId);
                this.ligneService.save(ligne);
            }
        });

        // == Returning result ==
        ClientDTO result = clientMapper.toDto(client);
        clientSearchRepository.save(client);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Clients");

        return clientRepository.findByStatusNot(ObjectStatus.ARCHIVED, pageable)
            .map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> findAll() {
        log.debug("Request to get all Clients");

        return clientRepository.findAll().stream()
            .map(clientMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> findAllByGestionnaire(String gestionnaire) {
        log.debug("Request to get all Clients");

        return clientRepository.findAllByGestionnaireAndStatusNot(gestionnaire, ObjectStatus.ARCHIVED).stream()
            .map(clientMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> findOne(Long id) {
        log.debug("Request to get Client : {}", id);
        return clientRepository.findById(id)
            .map(clientMapper::toDto);
    }

    @Override
    //TODO : MD : Audit : Plus de 40 lignes de codes 
    //TODO : MD : Audit : Tests de valeurs API non exploités
    public void delete(Long id) {
        log.debug("Request to delete Client : {}", id);
        Optional<Client> optionalClient = clientRepository.findClientByIdFetchAll(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            userFeignClient.deleteClientUsers(client.getId());

            // suppression des alertes du client
            operationFeignClient.deleteClientAlerts(client.getId());

            if (client.getLignes() != null && !client.getLignes().isEmpty()) {
                client.getLignes().parallelStream().forEach(ligne -> {
                    ligne.setStatus(ObjectStatus.ARCHIVED);
                });
            }

            if (client.getGroupes() != null && !client.getGroupes().isEmpty()) {
                client.getGroupes().parallelStream().forEach(groupe -> {
                    groupe.setStatus(ObjectStatus.ARCHIVED);
                });
            }

            if (client.getEmployes() != null && !client.getEmployes().isEmpty()) {
                client.getEmployes().parallelStream().forEach(employe -> {
                    employe.setStatus(ObjectStatus.ARCHIVED);
                });
            }

            if (client.getProduitsPersonnalises() != null && !client.getProduitsPersonnalises().isEmpty()) {
                client.getProduitsPersonnalises().parallelStream().forEach(produit -> {
                    produit.setStatus(ObjectStatus.ARCHIVED);
                });
            }

            if (client.getOffres() != null && !client.getOffres().isEmpty()) {
                client.getOffres().parallelStream().forEach(offre -> {
                    offre.setStatus(ObjectStatus.ARCHIVED);
                });
            }

            client.setStatus(ObjectStatus.ARCHIVED);
            client = clientRepository.save(client);
            clientSearchRepository.save(client);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> search(String query, Pageable pageable) {
        Page<Client> page;
        if (query != null && !query.isEmpty()) {
            page = clientRepository.searchClients(ObjectStatus.ARCHIVED, "%" + query + "%", pageable);
        } else {
            page = clientRepository.searchClients(ObjectStatus.ARCHIVED, pageable);
        }
        return page.map(clientMapper::toDto);
    }

    @Override
    public Page<ClientDTO> findByGestionnaire(String emailGestionnaire, Pageable pageable) {
        return clientRepository.findByGestionnaire(emailGestionnaire, pageable).map(clientMapper::toDto);
    }

    @Override
    //TODO : MD : Audit : Plus de 40 lignes de codes 
    //TODO : MD : Audit : Code en dur
    public byte[] export(String gestionnaire) throws IOException {
        byte[] clientsData = null;
        List<ClientDTO> clients;
        if (gestionnaire != null) {
            log.debug("REST request to get all Clients by gestionnaire : {}", gestionnaire);
            clients = findAllByGestionnaire(gestionnaire);
        } else {
            log.debug("REST request to get all Clients");
            clients = findAll();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row rowHeader = sheet.createRow(0);
        Cell cell0 = rowHeader.createCell(0);
        cell0.setCellValue("Raison social");
        Cell cell1 = rowHeader.createCell(1);
        cell1.setCellValue("Numéro de compte");
        Cell cell2 = rowHeader.createCell(2);
        cell2.setCellValue("Numéro d'identification");
        Cell cell3 = rowHeader.createCell(3);
        cell3.setCellValue("Registre de commerce");
        Cell cell4 = rowHeader.createCell(4);
        cell4.setCellValue("Adresse du siège social");
        Cell cell5 = rowHeader.createCell(5);
        cell5.setCellValue("Ville");
        Cell cell6 = rowHeader.createCell(6);
        cell6.setCellValue("Pays");
        Cell cell7 = rowHeader.createCell(7);
        cell7.setCellValue("Adresse e-mail principale");
        Cell cell8 = rowHeader.createCell(8);
        cell8.setCellValue("Téléphone principal");
        Cell cell9 = rowHeader.createCell(9);
        cell9.setCellValue("Adresse e-mail gestionnaire");
        Cell cell10 = rowHeader.createCell(10);
        cell10.setCellValue("Numéro virtuel compte");

        int rowIndex = 1;

        for (ClientDTO client : clients) {
            String regComm = client.getRegistreComm() != null ? client.getRegistreComm() : "";
            String ville = client.getVille() != null ? client.getVille() : "";
            String pays = client.getCountry() != null ? client.getCountry().getNom() : "";
            Row row = sheet.createRow(rowIndex++);
            cell0 = row.createCell(0);
            cell0.setCellValue(client.getRaisonSociale());
            cell1 = row.createCell(1);
            cell1.setCellValue(client.getCode());
            cell2 = row.createCell(2);
            cell2.setCellValue(client.getNinea());
            cell3 = row.createCell(3);
            cell3.setCellValue(regComm);
            cell4 = row.createCell(4);
            cell4.setCellValue(client.getAdresse());
            cell5 = row.createCell(5);
            cell5.setCellValue(ville);
            cell6 = row.createCell(6);
            cell6.setCellValue(pays);
            cell7 = row.createCell(7);
            cell7.setCellValue(client.getEmail());
            cell8 = row.createCell(8);
            cell8.setCellValue(client.getTelephone());
            cell9 = row.createCell(9);
            cell9.setCellValue(client.getGestionnaire());
            cell10 = row.createCell(10);
            cell10.setCellValue(client.getNumeroVirtuel());
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            clientsData = baos.toByteArray();
            baos.flush();
            workbook.close();
        } catch (IOException e) {
            throw e;
        }

        return clientsData;
    }

    @Override
    public Optional<ClientDTO> findOneByCode(String code) {
        log.debug("Request to get Client with code : {}", code);
        return clientRepository.findOneByCode(code)
            .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientDTO> findOneByIdNotAndCode(Long id, String code) {
        log.debug("Request to get Client with code : {} and idClient not : {}", code, id);
        return clientRepository.findOneByIdNotAndCode(id, code)
            .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientDTO> findOneByRaisonSociale(String raisonSociale) {
        log.debug("Request to get Client with raisonSociale : {}", raisonSociale);
        return clientRepository.findOneByRaisonSociale(raisonSociale)
            .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientDTO> findOneByIdNotAndRaisonSociale(Long id, String raisonSociale) {
        log.debug("Request to get Client with raisonSociale : {} and idClient not : {}", raisonSociale, id);
        return clientRepository.findOneByIdNotAndRaisonSociale(id, raisonSociale)
            .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientDTO> findOneByNinea(String ninea) {
        log.debug("Request to get Client with ninea : {}", ninea);
        return clientRepository.findOneByNinea(ninea)
            .map(clientMapper::toDto);
    }

    @Override
    public Optional<ClientDTO> findOneByIdNotAndNinea(Long id, String ninea) {
        log.debug("Request to get Client with ninea : {} and idClient not : {}", ninea, id);
        return clientRepository.findOneByIdNotAndNinea(id, ninea)
            .map(clientMapper::toDto);
    }

    @Override
    public Page<ClientDTO> searchClientsByGestionnaire(String gestionnaire, String query, Pageable pageable) {
        Page<Client> page;
        if (query != null && !query.isEmpty()) {
            page = clientRepository.searchClientsByGestionnaire(ObjectStatus.ARCHIVED, gestionnaire, "%" + query + "%", pageable);
        } else {
            page = clientRepository.searchClientsByGestionnaire(ObjectStatus.ARCHIVED, gestionnaire, pageable);
        }
        return page.map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NumberClientKpiDTO> getNumberOfClientsPerMonth(int year) {
        List<NumberClientKpiDTO> kpiDTOs = new ArrayList<>();
        for (Month month : Month.values()) {
            NumberClientKpiDTO kpiDTO = new NumberClientKpiDTO();
            kpiDTO.setMonthNumber(month.getValue());
            kpiDTO.setMonthName(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            kpiDTO.setNumberOfClients(clientRepository.getNumberClientsPerMonth(year, month.getValue()));
            kpiDTOs.add(kpiDTO);
        }
        return kpiDTOs.stream().sorted().collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NumberClientActiveKpiDTO> getNumberOfActiveClientsPerMonth(int year) {
        List<NumberClientActiveKpiDTO> kpiDTOs = new ArrayList<>();
        for (Month month : Month.values()) {
            NumberClientActiveKpiDTO kpiDTO = new NumberClientActiveKpiDTO();
            kpiDTO.setMonthNumber(month.getValue());
            kpiDTO.setMonthName(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            kpiDTO.setNumberOfActiveClients(clientRepository.getNumberOfActiveClientsPerMonth(year, month.getValue()));
            kpiDTOs.add(kpiDTO);
        }
        return kpiDTOs.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Long> getActiveClientsIdList() {
        return clientRepository.getActiveClientsIdList();
    }
}
