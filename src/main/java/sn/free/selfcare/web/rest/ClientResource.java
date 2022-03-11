package sn.free.selfcare.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.service.ClientService;
import sn.free.selfcare.service.dto.AdminClientDTO;
import sn.free.selfcare.service.dto.ClientDTO;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Client}.
 */
@RestController
@RequestMapping("/api")
public class ClientResource {

    private static final String ENTITY_NAME = "assetMicroserviceClient";
    private final Logger log = LoggerFactory.getLogger(ClientResource.class);
    private final ClientService clientService;
    private final RestTemplate restTemplate;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    @Value("${services.infosClient.url}")
    private String infosClientServiceURL;

    public ClientResource(ClientService clientService, @Qualifier("restTemplate") RestTemplate restTemplate) {
        this.clientService = clientService;
        this.restTemplate = restTemplate;
    }

    /**
     * {@code POST  /clients} : Create a new client.
     *
     * @param clientDTO the clientDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientDTO, or with status {@code 400 (Bad Request)} if the client has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clients")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
        log.debug("REST request to save Client : {}", clientDTO);

        if (clientDTO.getId() != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (clientService.findOneByCode(clientDTO.getCode()).isPresent()) {
            throw new BadRequestAlertException("Code " + clientDTO.getCode() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".codeexists");
        }
        if (clientService.findOneByRaisonSociale(clientDTO.getRaisonSociale()).isPresent()) {
            throw new BadRequestAlertException("Social reason " + clientDTO.getRaisonSociale() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".socialreasonexists");
        }
        if (clientService.findOneByNinea(clientDTO.getNinea()).isPresent()) {
            throw new BadRequestAlertException("Ninea " + clientDTO.getRaisonSociale() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".nineaexists");
        }

        ClientDTO result = clientService.save(clientDTO);

        return ResponseEntity.created(new URI("/api/clients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clients} : Updates an existing client.
     *
     * @param clientDTO the clientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientDTO,
     * or with status {@code 400 (Bad Request)} if the clientDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clients")
    public ResponseEntity<ClientDTO> updateClient(@Valid @RequestBody ClientDTO clientDTO) throws URISyntaxException {
        log.debug("REST request to update Client : {}", clientDTO);

        if (clientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (clientService.findOneByIdNotAndCode(clientDTO.getId(), clientDTO.getCode()).isPresent()) {
            throw new BadRequestAlertException("Code " + clientDTO.getCode() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".codeexists");
        }
        if (clientService.findOneByIdNotAndRaisonSociale(clientDTO.getId(), clientDTO.getRaisonSociale()).isPresent()) {
            throw new BadRequestAlertException("Social reason " + clientDTO.getRaisonSociale() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".socialreasonexists");
        }
        if (clientService.findOneByIdNotAndNinea(clientDTO.getId(), clientDTO.getNinea()).isPresent()) {
            throw new BadRequestAlertException("Ninea " + clientDTO.getRaisonSociale() + " is already in use!", ENTITY_NAME, ENTITY_NAME + ".nineaexists");
        }

        ClientDTO result = clientService.save(clientDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /clients} : get all the clients.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clients in body.
     */
    @GetMapping("/clients")
    public ResponseEntity<List<ClientDTO>> getAllClients(Pageable pageable) {
        log.debug("REST request to get a page of Clients");
        Page<ClientDTO> page = clientService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /compteClientIntegration} : get infos client from CRM.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the client's infos in body.
     */
    @SuppressWarnings("rawtypes")
    @GetMapping("/compteClientCrm")
  //TODO : MD : Audit : Plus de 40 lignes de codes 
    public ResponseEntity<ClientDTO> getInfosClientFromCrm(@RequestParam(value = "accountCode", required = true) final String accountCode) {
        log.debug("REST request to getinfos of Client : {}", accountCode);
        log.debug("infosClientServiceURL avec code : {}", infosClientServiceURL.concat("?accountCode=").concat(accountCode));
        ClientDTO clientDTO = new ClientDTO();
        Map result = restTemplate.getForEntity(infosClientServiceURL.concat("?accountCode=").concat(accountCode), Map.class).getBody();
        log.debug("result = {}", result.toString());
        if (result != null) {
            Map response = (Map) result.get("CompteClientIntegrationResponse");
            Map header = (Map) response.get("header");
            Integer succesCode = (Integer) header.get("succesCode");
            if (succesCode != null && succesCode.equals(200)) {
                Map body = (Map) response.get("body");
                Map compteClient = (Map) body.get("compteClient");

                if (compteClient != null) {

                    clientDTO.setStatus(ObjectStatus.ACTIVE);

                    String raisonSociale = compteClient.get("customerName") != null ? ((Object) compteClient.get("customerName")).toString() : null;
                    clientDTO.setRaisonSociale(raisonSociale);

                    String code = compteClient.get("accountCode") != null ? ((Object) compteClient.get("accountCode")).toString() : null;
                    clientDTO.setCode(code);

                    String ninea = compteClient.get("customerNinea") != null ? ((Object) compteClient.get("customerNinea")).toString() : null;
                    clientDTO.setNinea(ninea);

                    String regCom = compteClient.get("customerRegNumber") != null ? ((Object) compteClient.get("customerRegNumber")).toString() : null;
                    clientDTO.setRegistreComm(regCom);

                    // Infos admin client
                    ////////////////////////////
                    AdminClientDTO adminClientDTO = new AdminClientDTO();

                    String contactFirstName = compteClient.get("contactFirstName") != null ? ((Object) compteClient.get("contactFirstName")).toString() : null;
                    adminClientDTO.setFirstName(contactFirstName);

                    String contactLastName = compteClient.get("contactLastName") != null ? ((Object) compteClient.get("contactLastName")).toString() : null;
                    adminClientDTO.setLastName(contactLastName);

                    String mobilePhoneNumber = compteClient.get("mobilePhoneNumber") != null ? ((Object) compteClient.get("mobilePhoneNumber")).toString() : null;
                    adminClientDTO.setTelephone(mobilePhoneNumber);

                    clientDTO.setAdmin(adminClientDTO);

                    ///////////////////////////

                    String telephonePrincipale = compteClient.get("workPhoneNumber") != null ? ((Object) compteClient.get("workPhoneNumber")).toString() : null;
                    clientDTO.setTelephone(telephonePrincipale);

                    String adresseSiegeSocial = compteClient.get("address") != null ? ((Object) compteClient.get("address")).toString() : null;
                    clientDTO.setAdresse(adresseSiegeSocial);

                    Map listeNumeros = compteClient.get("listeNumeros") != null ? (Map) compteClient.get("listeNumeros") : null;
                    if (listeNumeros != null) {
                        if(listeNumeros.get("numero") != null) {
                            List numeros = new ArrayList();
                            if(listeNumeros.get("numero") instanceof List) {
                                numeros.addAll((List) listeNumeros.get("numero"));
                            } else {
                                if(listeNumeros.get("numero") instanceof Map) {
                                    Map numero = (Map) listeNumeros.get("numero");
                                    numeros.add(numero);
                                }
                            }

                            if (numeros != null && numeros.size() > 0) {
                                Set<LigneDTO> lignes = new HashSet<LigneDTO>();
                                for (int i = 0; i < numeros.size(); i++) {
                                    Map numero = (Map) numeros.get(i);
                                    if(numero.get("subscriberType") != null) {
                                        String subscriberType = (String) numero.get("subscriberType");
                                        if ("mobile".equals(subscriberType)) {
                                            if (numero.get("msisdn") != null && numero.get("imsi") != null) {
                                                Object msisdn = (Object) numero.get("msisdn");
                                                Object imsi = (Object) numero.get("imsi");
                                                if (!msisdn.toString().isEmpty() && !imsi.toString().isEmpty() && StringUtils.isNumeric(msisdn.toString()) && StringUtils.isNumeric(imsi.toString())) {
                                                    LigneDTO ligne = new LigneDTO();
                                                    ligne.setNumero(msisdn.toString());
                                                    ligne.setImsi(imsi.toString());
                                                    ligne.setStatus(ObjectStatus.ACTIVE);
                                                    lignes.add(ligne);
                                                }
                                            }
                                        }
                                    }
                                }

                                lignes = lignes
                                    .stream()
                                    .sorted(Comparator.comparing(LigneDTO::getNumero))
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                                clientDTO.setLignes(lignes);

                                clientDTO.setLignes(lignes);
                            }
                        }
                    }
                }
            }
        }

        return ResponseEntity.ok().body(clientDTO);
    }

    /**
     * {@code GET  /clients/export} : export all the clients.
     *
     * @param gestionnaire the gestionnaire  email.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of clients in body.
     */
    @GetMapping(value = "/clients/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAsXlsx(@RequestParam(value = "gestionnaire", required = false) final String gestionnaire) {

        try {
            log.debug("Entering clients download");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateTime = dateFormatter.format(new Date());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-disposition", "attachment; filename=clients_" + currentDateTime + ".xlsx");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok().headers(headers).body(clientService.export(gestionnaire));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
        }
    }

    /**
     * {@code GET  /clients/:id} : get the "id" client.
     *
     * @param id the id of the clientDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        log.debug("REST request to get Client : {}", id);
        Optional<ClientDTO> clientDTO = clientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientDTO);
    }

    /**
     * {@code DELETE  /clients/:id} : delete the "id" client.
     *
     * @param id the id of the clientDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.debug("REST request to delete Client : {}", id);
        try {
            Optional<ClientDTO> clientDTO = clientService.findOne(id);
            if (!clientDTO.isPresent()) {
                throw new BadRequestAlertException("Client with ID " + id + " deletion failed!", ENTITY_NAME, ENTITY_NAME + ".clientnotexists");
            }

            clientService.delete(id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, clientDTO.get().getRaisonSociale()))
                .build();

        } catch (Exception e) {
            throw new BadRequestAlertException("Client with ID " + id + " deletion failed!", ENTITY_NAME, ENTITY_NAME + ".usersDeletionFailed");
        }
    }

    /**
     * {@code SEARCH  /_search/clients?query=:query} : search for the client corresponding
     * to the query.
     *
     * @param query    the query of the client search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/clients")
    public ResponseEntity<List<ClientDTO>> searchClients(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Clients for query {}", query);
        Page<ClientDTO> page = clientService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Search clients by gestionnaire and query
     *
     * @param gestionnaire
     * @param query
     * @param pageable
     * @return
     */
    @GetMapping("/my-clients/{gestionnaire}")
    public ResponseEntity<List<ClientDTO>> searchClientsByGestionnaire(@PathVariable String gestionnaire, @RequestParam String query, Pageable pageable) {
        log.debug("REST request to search Clients for gestionnaire = {} and query = {}", gestionnaire, query);
        Page<ClientDTO> page = clientService.searchClientsByGestionnaire(gestionnaire, query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
