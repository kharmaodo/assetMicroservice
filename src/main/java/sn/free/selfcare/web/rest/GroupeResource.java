package sn.free.selfcare.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.github.jhipster.web.util.PaginationUtil;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.http.HttpHeaders;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import sn.free.selfcare.service.GroupeService;
import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.dto.operation.GroupeAdjustmentDTO;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Groupe}.
 */
@RestController
@RequestMapping("/api")
public class GroupeResource {

	private final Logger log = LoggerFactory.getLogger(GroupeResource.class);

	private static final String ENTITY_NAME = "assetMicroserviceGroupe";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private final GroupeService groupeService;

	public GroupeResource(GroupeService groupeService) {
		this.groupeService = groupeService;
	}

	/**
	 * {@code POST  /groupes} : Create a new groupe.
	 *
	 * @param groupeDTO
	 *            the groupeDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new groupeDTO, or with status {@code 400 (Bad Request)} if
	 *         the groupe has already an ID.
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect.
	 */
	@PostMapping("/groupes")
	public ResponseEntity<GroupeDTO> createGroupe(@Valid @RequestBody GroupeDTO groupeDTO) throws URISyntaxException {
		log.debug("REST request to save Groupe : {}", groupeDTO);
		if (groupeDTO.getId() != null) {
			throw new BadRequestAlertException("A new groupe cannot already have an ID", ENTITY_NAME, "idexists");
		}

		checkNomUnicity(groupeDTO);

		GroupeDTO result = groupeService.save(groupeDTO);
		return ResponseEntity
				.created(new URI("/api/groupes/" + result.getId())).headers(HeaderUtil
						.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
				.body(result);
	}

	/**
	 * {@code PUT  /groupes} : Updates an existing groupe.
	 *
	 * @param groupeDTO
	 *            the groupeDTO to update.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the updated groupeDTO, or with status {@code 400 (Bad Request)} if
	 *         the groupeDTO is not valid, or with status
	 *         {@code 500 (Internal Server Error)} if the groupeDTO couldn't be
	 *         updated.
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect.
	 */
	@PutMapping("/groupes")
	public ResponseEntity<GroupeDTO> updateGroupe(@Valid @RequestBody GroupeDTO groupeDTO) throws URISyntaxException {
		log.debug("REST request to update Groupe : {}", groupeDTO);
		if (groupeDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}

		checkNomUnicity(groupeDTO);

		GroupeDTO result = groupeService.save(groupeDTO);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groupeDTO.getId().toString()))
				.body(result);
	}

	/**
	 * {@code GET  /groupes} : get all the groupes.
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of groupes in body.
	 */
	@GetMapping("/groupes")
	public List<GroupeDTO> getAllGroupes() {
		log.debug("REST request to get all Groupes");
		return groupeService.findAll();
	}

	/**
	 * {@code GET  /groupes/:id} : get the "id" groupe.
	 *
	 * @param id
	 *            the id of the groupeDTO to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the groupeDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/groupes/{id}")
	public ResponseEntity<GroupeDTO> getGroupe(@PathVariable Long id) {
		log.debug("REST request to get Groupe : {}", id);
		Optional<GroupeDTO> groupeDTO = groupeService.findOne(id);
		return ResponseUtil.wrapOrNotFound(groupeDTO);
	}

	@GetMapping(value = {"/groupes/adjustment", "/groupes/adjustment/{id}"})
	public ResponseEntity<GroupeAdjustmentDTO> getGroupeForAdjustment(@PathVariable(required = false) Long id) {
		if (id == null) {
			id = -1L;
		}
		log.debug("REST request to get Groupe for adjustment: {}", id);
		Optional<GroupeAdjustmentDTO> groupeAdjustmentDTO = groupeService.findOneForAdjustment(id);
		return ResponseUtil.wrapOrNotFound(groupeAdjustmentDTO);
	}

	/**
	 * {@code DELETE  /groupes/:id} : delete the "id" groupe.
	 *
	 * @param id
	 *            the id of the groupeDTO to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/groupes/{id}")
	public ResponseEntity<Void> deleteGroupe(@PathVariable Long id) {
		log.debug("REST request to delete Groupe : {}", id);
		groupeService.delete(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
				.build();
	}

	/**
	 * {@code SEARCH  /_search/groupes?query=:query} : search for the groupe
	 * corresponding to the query.
	 *
	 * @param query
	 *            the query of the groupe search.
	 * @return the result of the search.
	 */
	@GetMapping("/_search/groupes")
	public List<GroupeDTO> searchGroupes(@RequestParam String query) {
		log.debug("REST request to search Groupes for query {}", query);
		return groupeService.search(query);
	}

	@GetMapping("/client/{clientId}/groupes")
    public ResponseEntity<List<GroupeDTO>> findAllByClientAndQuery(@PathVariable Long clientId, @RequestParam(required = false) String query, Pageable pageable) {
        log.debug("REST request to findAllByClient Groupes for clientId {} and query {}", clientId.toString(), query);
        Page<GroupeDTO> page = groupeService.findGroupesByClientAndQuery(clientId, query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/groupes/client/{clientId}")
    public List<GroupeDTO> findGroupesByClient(@PathVariable Long clientId) {
        log.debug("REST request to find Groupes for client: {}", clientId);
        return groupeService.findGroupesByClient(clientId);
    }

	private void checkNomUnicity(GroupeDTO groupeDTO) {
		List<GroupeDTO> groupes = groupeService.findAllByNom(groupeDTO.getNom(), true);
		if (groupes != null) {
			if (groupeDTO.getId() != null) {
				groupes = groupes.stream()
						.filter(groupe -> groupe.getId() != groupeDTO.getId())
						.collect(Collectors.toCollection(LinkedList::new));
			}
			if (groupes.size() > 0) {
				final Long clientId = groupeDTO.getClientId();
				if (clientId == null || (clientId != null && groupes.stream().anyMatch(groupe -> groupe.getClientId() == clientId))) {
					throw new BadRequestAlertException("A group with name \"" + groupeDTO.getNom() + "\" already exists", ENTITY_NAME, "group.nomexists");
				}
			}
		}
	}
}
