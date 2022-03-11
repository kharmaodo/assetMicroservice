package sn.free.selfcare.web.rest;

import sn.free.selfcare.domain.enumeration.ModePaiement;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.service.OffreService;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;
import sn.free.selfcare.service.dto.OffreDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
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
 * REST controller for managing {@link sn.free.selfcare.domain.Offre}.
 */
@RestController
@RequestMapping("/api")
public class OffreResource {

    private final Logger log = LoggerFactory.getLogger(OffreResource.class);

    private static final String ENTITY_NAME = "assetMicroserviceOffre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OffreService offreService;

    private final RestTemplate restTemplate;

    public OffreResource(OffreService offreService, @Qualifier("restTemplate") RestTemplate restTemplate) {
        this.offreService = offreService;
        this.restTemplate = restTemplate;
    }

    /**
     * {@code POST  /offres} : Create a new offre.
     *
     * @param offreDTO the offreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offreDTO, or with status {@code 400 (Bad Request)} if the offre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/offres")
    public ResponseEntity<OffreDTO> createOffre(@Valid @RequestBody OffreDTO offreDTO) throws URISyntaxException {
        log.debug("REST request to save Offre : {}", offreDTO);
        if (offreDTO.getId() != null) {
            throw new BadRequestAlertException("A new offre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OffreDTO result = offreService.save(offreDTO);
        return ResponseEntity.created(new URI("/api/offres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /offres} : Updates an existing offre.
     *
     * @param offreDTO the offreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offreDTO,
     * or with status {@code 400 (Bad Request)} if the offreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/offres")
    public ResponseEntity<OffreDTO> updateOffre(@Valid @RequestBody OffreDTO offreDTO) throws URISyntaxException {
        log.debug("REST request to update Offre : {}", offreDTO);
        if (offreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OffreDTO result = offreService.save(offreDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, offreDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /offres} : get all the offres.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offres in body.
     */
    @GetMapping("/offres")
    public List<OffreDTO> getAllOffres(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Offres");
        return ListUtils.union(getStandardsOffersCrm(), offreService.findAll());
    }

    /**
     * {@code GET  /offres/:id} : get the "id" offre.
     *
     * @param id the id of the offreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/offres/{id}")
    public ResponseEntity<OffreDTO> getOffre(@PathVariable Long id) {
        log.debug("REST request to get Offre : {}", id);
        Optional<OffreDTO> offreDTO = offreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offreDTO);
    }

    /**
     * {@code DELETE  /offres/:id} : delete the "id" offre.
     *
     * @param id the id of the offreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/offres/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
        log.debug("REST request to delete Offre : {}", id);
        offreService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code GET  /standardsOffersCrm} : get standard offers from CRM.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the standard offers in body.
     */
    @SuppressWarnings("rawtypes")
	@GetMapping("/standardsOffersCrm")
    public List<OffreDTO> getStandardsOffersCrm() {
        log.debug("REST request to get standard offers");
        List<OffreDTO> offres = new ArrayList<OffreDTO>();
        //TODO : MD : Audit code en dur : no best practices
        Map result = restTemplate.getForEntity("http://192.168.41.165:8040/services/freeapp/GetStandardsOffersCrm_1_0", Map.class).getBody();
        log.debug("result : {}", result);
        if (result != null) {
        	Map header = (Map) result.get("header");
        	Integer succesCode = (Integer) header.get("succesCode");
        	if (succesCode != null && succesCode.equals(200)) {
        		Map body = (Map) result.get("body");
        		Map offersList = (Map) body.get("offerslist");
        		if (offersList != null) {
        			List offers = (List) offersList.get("offer");
        			if (offers != null && offers.size() > 0) {
        				for (int i = 0; i < offers.size(); i++) {
        					Map offer = (Map) offers.get(i);
        					OffreDTO offre = new OffreDTO();
        					int id = (int) offer.get("id");
        					int prix = (int) offer.get("prix");
        					offre.setId(Long.valueOf(id));
    						offre.setStatus(ObjectStatus.ACTIVE);
    						offre.setTypeOffre(TypeOffre.STANDARD);
    						offre.setModePaiement(ModePaiement.POSTPAID);
    						offre.setCode((String) offer.get("nom"));
    						offre.setMontant(Double.valueOf(prix));
    						offres.add(offre);
						}
        			}
        		}
        	}
        }

        return offres;
    }

	/**
	 * {@code SEARCH  /_search/offres?query=:query} : search for the offre
	 * corresponding to the query.
	 *
	 * @param query
	 *            the query of the offre search.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/offres")
	public List<OffreDTO> searchOffres(@RequestParam String query) {
		log.debug("REST request to search Offres for query {}", query);
		List<OffreDTO> offresCrm = getStandardsOffersCrm().stream()
				.filter(offre -> StringUtils.containsIgnoreCase(offre.getCode(), query))
				.collect(Collectors.toCollection(LinkedList::new));
		return ListUtils.union(offresCrm, offreService.search(query));
	}

    /**
	 * {@code GET  /offres/export} : export all the offres.
	 *
	 * @param clientId the clientId.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of offres in body.
	 */
	@GetMapping(value = "/offres/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ResponseEntity<byte[]> exportAsXlsx(@RequestParam(value = "clientId", required = false) final Long clientId) {

		try {
			log.debug("Entering offres download");
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		    String currentDateTime = dateFormatter.format(new Date());
			HttpHeaders headers = new HttpHeaders();
		    headers.add("Content-disposition", "attachment; filename=offres_" + currentDateTime + ".xlsx");
		    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		    headers.add("Pragma", "no-cache");
		    headers.add("Expires", "0");
			return ResponseEntity.ok().headers(headers).body(offreService.export(clientId));
		} catch(Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
		}
	}
}
