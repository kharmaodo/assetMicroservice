package sn.free.selfcare.web.rest;

import sn.free.selfcare.service.SolutionService;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;
import sn.free.selfcare.service.dto.SolutionDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Solution}.
 */
@RestController
@RequestMapping("/api")
public class SolutionResource {

    private final Logger log = LoggerFactory.getLogger(SolutionResource.class);

    private static final String ENTITY_NAME = "assetMicroserviceSolution";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SolutionService solutionService;

    public SolutionResource(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    /**
     * {@code POST  /solutions} : Create a new solution.
     *
     * @param solutionDTO the solutionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solutionDTO, or with status {@code 400 (Bad Request)} if the solution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/solutions")
    public ResponseEntity<SolutionDTO> createSolution(@Valid @RequestBody SolutionDTO solutionDTO) throws URISyntaxException {
        log.debug("REST request to save Solution : {}", solutionDTO);
        if (solutionDTO.getId() != null) {
            throw new BadRequestAlertException("A new solution cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SolutionDTO result = solutionService.save(solutionDTO);
        return ResponseEntity.created(new URI("/api/solutions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /solutions} : Updates an existing solution.
     *
     * @param solutionDTO the solutionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solutionDTO,
     * or with status {@code 400 (Bad Request)} if the solutionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solutionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/solutions")
    public ResponseEntity<SolutionDTO> updateSolution(@Valid @RequestBody SolutionDTO solutionDTO) throws URISyntaxException {
        log.debug("REST request to update Solution : {}", solutionDTO);
        if (solutionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SolutionDTO result = solutionService.save(solutionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solutionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /solutions} : get all the solutions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of solutions in body.
     */
    @GetMapping("/solutions")
    public List<SolutionDTO> getAllSolutions() {
        log.debug("REST request to get all Solutions");
        return solutionService.findAll();
    }

    /**
     * {@code GET  /solutions/:id} : get the "id" solution.
     *
     * @param id the id of the solutionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solutionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/solutions/{id}")
    public ResponseEntity<SolutionDTO> getSolution(@PathVariable Long id) {
        log.debug("REST request to get Solution : {}", id);
        Optional<SolutionDTO> solutionDTO = solutionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solutionDTO);
    }

    /**
     * {@code DELETE  /solutions/:id} : delete the "id" solution.
     *
     * @param id the id of the solutionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/solutions/{id}")
    public ResponseEntity<Void> deleteSolution(@PathVariable Long id) {
        log.debug("REST request to delete Solution : {}", id);
        solutionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/solutions?query=:query} : search for the solution corresponding
     * to the query.
     *
     * @param query the query of the solution search.
     * @return the result of the search.
     */
    @GetMapping("/_search/solutions")
    public List<SolutionDTO> searchSolutions(@RequestParam String query) {
        log.debug("REST request to search Solutions for query {}", query);
        return solutionService.search(query);
    }
}
