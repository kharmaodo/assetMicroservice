package sn.free.selfcare.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.helper.CSVImportFileHelper;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.dto.ActivateDeactivateServiceDTO;
import sn.free.selfcare.service.dto.ActivateDeactivateServiceResponseDTO;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Ligne}.
 */
@RestController
@RequestMapping("/api")
public class LigneResource {

    private static final String ENTITY_NAME = "assetMicroserviceLigne";
    private final Logger log = LoggerFactory.getLogger(LigneResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LigneService ligneService;

    public LigneResource(LigneService ligneService) {
        this.ligneService = ligneService;
    }

    /**
     * {@code POST  /lignes} : Create a new ligne.
     *
     * @param ligneDTO the ligneDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     * body the new ligneDTO, or with status {@code 400 (Bad Request)} if
     * the ligne has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lignes")
    public ResponseEntity<LigneDTO> createLigne(@Valid @RequestBody LigneDTO ligneDTO) throws URISyntaxException {
        log.debug("REST request to save Ligne : {}", ligneDTO);
        if (ligneDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligne cannot already have an ID", ENTITY_NAME, "idexists");
        }

        checkNumeroAndImsiUnicity(ligneDTO);

        LigneDTO result = ligneService.save(ligneDTO);
        return ResponseEntity
            .created(new URI("/api/lignes/" + result.getId())).headers(HeaderUtil
                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getNumero()))
            .body(result);
    }

    /**
     * {@code PUT  /lignes} : Updates an existing ligne.
     *
     * @param ligneDTO the ligneDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     * the updated ligneDTO, or with status {@code 400 (Bad Request)} if the
     * ligneDTO is not valid, or with status
     * {@code 500 (Internal Server Error)} if the ligneDTO couldn't be
     * updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lignes")
    public ResponseEntity<LigneDTO> updateLigne(@Valid @RequestBody LigneDTO ligneDTO) throws URISyntaxException {
        log.debug("REST request to update Ligne : {}", ligneDTO);
        if (ligneDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        checkNumeroAndImsiUnicity(ligneDTO);

        Optional<LigneDTO> ligne = ligneService.findOneWithServicesById(ligneDTO.getId());
        if(ligne.isPresent() && ligne.get().getNumero().equals(ligneDTO.getNumero())) {
            if(ligne.get().getServices() != null && ligne.get().getServices().size() > 0) {
                ligneDTO.setServices(ligne.get().getServices());
            }
        }

        LigneDTO result = ligneService.save(ligneDTO);
        return ResponseEntity.ok().headers(
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneDTO.getNumero()))
            .body(result);
    }

    private void checkNumeroAndImsiUnicity(LigneDTO ligneDTO) {

        List<LigneDTO> lignes = ligneService.findDistinctByNumeroOrImsi(ligneDTO.getNumero(), ligneDTO.getImsi());

        if (lignes != null) {

            if (ligneDTO.getId() != null) {
                lignes = lignes.stream()
                    .filter(ligne -> ligne.getId() != ligneDTO.getId())
                    .collect(Collectors.toCollection(LinkedList::new));
            }

            long nbActivatedLignesWithSameNumAndImsi = lignes.stream()
                .filter(l -> l.getNumero().equals(ligneDTO.getNumero()))
                .filter(l -> l.getImsi().equals(ligneDTO.getImsi()))
                .filter(l -> l.getStatus().equals(ObjectStatus.ACTIVE))
                .count();

            if (nbActivatedLignesWithSameNumAndImsi > 0) {
                throw new BadRequestAlertException("A ligne with numero " + ligneDTO.getNumero() + " and imsi " + ligneDTO.getImsi() + " already exists", ENTITY_NAME, ENTITY_NAME + ".numeroImsiExists");
            }

            long nbArchivedLignesWithSameNumAndImsi = lignes.stream()
                .filter(l -> l.getNumero().equals(ligneDTO.getNumero()))
                .filter(l -> l.getImsi().equals(ligneDTO.getImsi()))
                .filter(l -> l.getStatus().equals(ObjectStatus.ARCHIVED))
                .count();

            if (nbArchivedLignesWithSameNumAndImsi > 0) {
                throw new BadRequestAlertException("A ligne with numero " + ligneDTO.getNumero() + " and imsi " + ligneDTO.getImsi() + " already archived", ENTITY_NAME, ENTITY_NAME + ".numeroImsiArchived");
            }

            long nbActivatedLignesWithSameNumber = lignes.stream()
                .filter(l -> l.getNumero().equals(ligneDTO.getNumero()))
                .filter(l -> l.getStatus().equals(ObjectStatus.ACTIVE))
                .count();

            if (nbActivatedLignesWithSameNumber > 0) {
                throw new BadRequestAlertException("A ligne with numero " + ligneDTO.getNumero() + " already exists", ENTITY_NAME, ENTITY_NAME + ".numeroexists");
            }

            long nbActivatedLignesWithSameImsi = lignes.stream()
                .filter(l -> l.getImsi().equals(ligneDTO.getImsi()))
                .filter(l -> l.getStatus().equals(ObjectStatus.ACTIVE))
                .count();

            if(nbActivatedLignesWithSameImsi > 0) {
                throw new BadRequestAlertException("A ligne with ismi " + ligneDTO.getImsi() + " already exists", ENTITY_NAME, ENTITY_NAME + ".imsiexists");
            }
        }
    }

    /**
     * {@code GET  /lignes} : get all the lignes.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of lignes in body.
     */
    @GetMapping("/lignes")
    public List<LigneDTO> getAllLignes(@RequestParam(required = false) String filter) {
        if ("employe-is-null".equals(filter)) {
            log.debug("REST request to get all Lignes where employe is null");
            return ligneService.findAllWhereEmployeIsNull();
        }
        log.debug("REST request to get all Lignes");
        return ligneService.findAll();
    }

    /**
     * {@code GET  /lignes/client} : get all the lignes of one client.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of lignes in body.
     */
    @GetMapping("/client/{clientId}/offre/lignes")
    public ResponseEntity<List<LigneDTO>> getAllLigneOfOffre(@PathVariable Long clientId,
                                                             @RequestParam(required = false) Long offreId,
                                                             @RequestParam(required = false) String filter) {
        List<LigneDTO> lignes;
        if ("offre-is-null".equals(filter)) {
            log.debug("REST request to get all Lignes of clientId: {} where offre is null", clientId);
            lignes = ligneService.findAllByClientIdAndOffreIsNull(clientId, offreId);
        } else {
            log.debug("REST request to get all Lignes of clientId: {}", clientId);
            lignes = ligneService.findAllByClientId(clientId);
        }
        return ResponseEntity.ok().body(lignes);
    }

    /**
     * {@code GET  /lignes/:id} : get the "id" ligne.
     *
     * @param id the id of the ligneDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     * the ligneDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lignes/{id}")
    public ResponseEntity<LigneDTO> getLigne(@PathVariable Long id) {
        log.debug("REST request to get Ligne : {}", id);
        Optional<LigneDTO> ligneDTO = ligneService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneDTO);
    }

    /**
     * {@code DELETE  /lignes/:id} : delete the "id" ligne.
     *
     * @param id the id of the ligneDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lignes/{id}")
    public ResponseEntity<Void> deleteLigne(@PathVariable Long id) {
        log.debug("REST request to delete Ligne : {}", id);
        Optional<LigneDTO> ligne = ligneService.findOne(id);
        String numero = ligne.isPresent() ? ligne.get().getNumero() : "";
        ligneService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, numero))
            .build();
    }

    /**
     * {@code SEARCH  /_search/lignes?query=:query} : search for the ligne
     * corresponding to the query.
     *
     * @param query the query of the ligne search.
     * @return the result of the search.
     */
    @GetMapping("/_search/lignes")
    public List<LigneDTO> searchLignes(@RequestParam String query) {
        log.debug("REST request to search Lignes for query {}", query);
        return ligneService.search(query);
    }

    @GetMapping("/client/{clientId}/lignes")
    public ResponseEntity<List<LigneDTO>> findAllByClientAndQuery(@PathVariable Long clientId, @RequestParam(required = false) String query, Pageable pageable) {
        log.debug("REST request to findAllByClient Lignes for clientId {} and query {}", clientId.toString(), query);
        Page<LigneDTO> page = ligneService.findLignesByClientAndQuery(clientId, query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lignes/export} : get all the clients.
     *
     * @param clientId the client id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     * of clients in body.
     */
    @GetMapping(value = "/lignes/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAsXlsx(@RequestParam(value = "clientId", required = true) final Long clientId) {

        try {
            log.debug("Entering lignes download");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateTime = dateFormatter.format(new Date());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-disposition", "attachment; filename=lignes_" + currentDateTime + ".xlsx");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok().headers(headers).body(ligneService.export(clientId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
        }
    }

    @PostMapping("/{clientId}/lignes/import")
    public ResponseEntity<Void> importLignes(@PathVariable("clientId") Long clientId, @RequestParam("file") MultipartFile file) {
        log.debug("REST request to import Lignes from CSV file : {} with clientId = {}", file.getOriginalFilename(), clientId);
        if (CSVImportFileHelper.hasCSVFormat(file)) {
            try {
                ligneService.saveLignesFromCSV(clientId, file);
                return ResponseEntity.noContent()
                    .headers(HeaderUtil.createAlert(applicationName, applicationName + "." + ENTITY_NAME + ".fileImported", file.getOriginalFilename()))
                    .build();
            } catch (IOException e) {
                throw new BadRequestAlertException("Fail to parse CSV file: " + e.getMessage(), "fileImport", "fileImport.failure.parse");
            }
        } else {
            throw new BadRequestAlertException("Please upload a csv file: " + file.getOriginalFilename() + "!", "fileImport", "fileImport.failure.badFormat");
        }
    }
}
