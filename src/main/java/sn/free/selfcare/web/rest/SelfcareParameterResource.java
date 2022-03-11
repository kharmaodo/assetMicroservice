package sn.free.selfcare.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.repository.SelfcareParameterRepository;
import sn.free.selfcare.service.SelfcareParameterService;
import sn.free.selfcare.service.dto.SelfcareParameterDTO;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing
 * {@link sn.free.selfcare.domain.SelfcareParameter}.
 */
@RestController
@RequestMapping("/api")
public class SelfcareParameterResource {

	private final Logger log = LoggerFactory.getLogger(SelfcareParameterResource.class);

	private static final String ENTITY_NAME = "assetMicroserviceSelfcareParameter";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private final SelfcareParameterService selfcareParameterService;

	public SelfcareParameterResource(SelfcareParameterService selfcareParameterService,
			SelfcareParameterRepository selfcareParameterRepository) {
		this.selfcareParameterService = selfcareParameterService;
	}

	/**
	 * {@code POST  /selfcare-parameters} : Create a new selfcareParameter.
	 *
	 * @param selfcareParameterDTO
	 *            the selfcareParameterDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new selfcareParameterDTO, or with status
	 *         {@code 400 (Bad Request)} if the selfcareParameter has already an ID.
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect.
	 */
	@PostMapping("/selfcare-parameters")
	public ResponseEntity<SelfcareParameterDTO> createSelfcareParameter(
			@Valid @RequestBody SelfcareParameterDTO selfcareParameterDTO) throws URISyntaxException {
		log.debug("REST request to save SelfcareParameter : {}", selfcareParameterDTO);
		if (selfcareParameterDTO.getId() != null) {
			throw new BadRequestAlertException("A new selfcareParameter cannot already have an ID", ENTITY_NAME,
					"idexists");
		}
		SelfcareParameterDTO result = selfcareParameterService.save(selfcareParameterDTO);
		return ResponseEntity
				.created(new URI("/api/selfcare-parameters/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}
	// Methode qui renvoie liste des options raison sociale, adresse et call center

	@SuppressWarnings("unchecked")
	@GetMapping("/selfcare-parameters/options")
	public List<SelfcareParameter> findParameterOptions(@RequestParam("options") List<ParameterOption> options) {
		log.debug("REST request to get SelfcareParameters : {}", options);
		List<SelfcareParameter> listOptions = null;
		if (options != null) {
			listOptions = selfcareParameterService.findParameterOptions(options);
		}

		return listOptions;
	}
	// Fin implementation par MOF

	/**
	 * {@code PUT  /selfcare-parameters} : Updates an existing selfcareParameter.
	 *
	 * @param selfcareParameterDTO
	 *            the selfcareParameterDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated selfcareParameterDTO, or with status
	 *         {@code 400 (Bad Request)} if the selfcareParameterDTO is not valid,
	 *         or with status {@code 500 (Internal Server Error)} if the
	 *         selfcareParameterDTO couldn't be updated.
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect.
	 */
	@PutMapping("/selfcare-parameters")
	public ResponseEntity<SelfcareParameterDTO> updateSelfcareParameter(
			@Valid @RequestBody SelfcareParameterDTO selfcareParameterDTO) throws URISyntaxException {
		log.debug("REST request to update SelfcareParameter : {}", selfcareParameterDTO);
		if (selfcareParameterDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		SelfcareParameterDTO result = selfcareParameterService.save(selfcareParameterDTO);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
				selfcareParameterDTO.getId().toString())).body(result);
	}

	/**
	 * {@code GET  /selfcare-parameters} : get all the selfcareParameters.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of selfcareParameters in body.
	 */
	@GetMapping("/selfcare-parameters")
	public List<SelfcareParameterDTO> getAllSelfcareParameters() {
		log.debug("REST request to get all SelfcareParameters");
		return selfcareParameterService.findAll();
	}

	/**
	 * {@code GET  /selfcare-parameters/:id} : get the "id" selfcareParameter.
	 *
	 * @param id
	 *            the id of the selfcareParameterDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the selfcareParameterDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/selfcare-parameters/{id}")
	public ResponseEntity<SelfcareParameterDTO> getSelfcareParameter(@PathVariable Long id) {
		log.debug("REST request to get SelfcareParameter : {}", id);
		Optional<SelfcareParameterDTO> selfcareParameterDTO = selfcareParameterService.findOne(id);
		return ResponseUtil.wrapOrNotFound(selfcareParameterDTO);
	}

	/**
	 * {@code DELETE  /selfcare-parameters/:id} : delete the "id" selfcareParameter.
	 *
	 * @param id
	 *            the id of the selfcareParameterDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/selfcare-parameters/{id}")
	public ResponseEntity<Void> deleteSelfcareParameter(@PathVariable Long id) {
		log.debug("REST request to delete SelfcareParameter : {}", id);
		selfcareParameterService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	/**
	 * {@code SEARCH  /_search/selfcare-parameters?query=:query} : search for the
	 * selfcareParameter corresponding to the query.
	 *
	 * @param query
	 *            the query of the selfcareParameter search.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/selfcare-parameters")
	public List<SelfcareParameterDTO> searchSelfcareParameters(@RequestParam String query) {
		log.debug("REST request to search SelfcareParameters for query {}", query);
		return selfcareParameterService.search(query);
	}
}
