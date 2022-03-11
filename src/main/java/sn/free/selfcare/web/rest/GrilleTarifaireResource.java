package sn.free.selfcare.web.rest;

import sn.free.selfcare.service.GrilleTarifaireService;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;
import sn.free.selfcare.service.dto.GrilleTarifaireDTO;

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
 * REST controller for managing {@link sn.free.selfcare.domain.GrilleTarifaire}.
 */
@RestController
@RequestMapping("/api")
public class GrilleTarifaireResource {

    private final Logger log = LoggerFactory.getLogger(GrilleTarifaireResource.class);

    private static final String ENTITY_NAME = "assetMicroserviceGrilleTarifaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GrilleTarifaireService grilleTarifaireService;

    public GrilleTarifaireResource(GrilleTarifaireService grilleTarifaireService) {
        this.grilleTarifaireService = grilleTarifaireService;
    }

    /**
     * {@code POST  /grille-tarifaires} : Create a new grilleTarifaire.
     *
     * @param grilleTarifaireDTO the grilleTarifaireDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new grilleTarifaireDTO, or with status {@code 400 (Bad Request)} if the grilleTarifaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grille-tarifaires")
    public ResponseEntity<GrilleTarifaireDTO> createGrilleTarifaire(@Valid @RequestBody GrilleTarifaireDTO grilleTarifaireDTO) throws URISyntaxException {
        log.debug("REST request to save GrilleTarifaire : {}", grilleTarifaireDTO);
        if (grilleTarifaireDTO.getId() != null) {
            throw new BadRequestAlertException("A new grilleTarifaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GrilleTarifaireDTO result = grilleTarifaireService.save(grilleTarifaireDTO);
        return ResponseEntity.created(new URI("/api/grille-tarifaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /grille-tarifaires} : Updates an existing grilleTarifaire.
     *
     * @param grilleTarifaireDTO the grilleTarifaireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grilleTarifaireDTO,
     * or with status {@code 400 (Bad Request)} if the grilleTarifaireDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the grilleTarifaireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grille-tarifaires")
    public ResponseEntity<GrilleTarifaireDTO> updateGrilleTarifaire(@Valid @RequestBody GrilleTarifaireDTO grilleTarifaireDTO) throws URISyntaxException {
        log.debug("REST request to update GrilleTarifaire : {}", grilleTarifaireDTO);
        if (grilleTarifaireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GrilleTarifaireDTO result = grilleTarifaireService.save(grilleTarifaireDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, grilleTarifaireDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /grille-tarifaires} : get all the grilleTarifaires.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of grilleTarifaires in body.
     */
    @GetMapping("/grille-tarifaires")
    public List<GrilleTarifaireDTO> getAllGrilleTarifaires() {
        log.debug("REST request to get all GrilleTarifaires");
        return grilleTarifaireService.findAll();
    }

    /**
     * {@code GET  /grille-tarifaires/:id} : get the "id" grilleTarifaire.
     *
     * @param id the id of the grilleTarifaireDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the grilleTarifaireDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grille-tarifaires/{id}")
    public ResponseEntity<GrilleTarifaireDTO> getGrilleTarifaire(@PathVariable Long id) {
        log.debug("REST request to get GrilleTarifaire : {}", id);
        Optional<GrilleTarifaireDTO> grilleTarifaireDTO = grilleTarifaireService.findOne(id);
        return ResponseUtil.wrapOrNotFound(grilleTarifaireDTO);
    }

    /**
     * {@code DELETE  /grille-tarifaires/:id} : delete the "id" grilleTarifaire.
     *
     * @param id the id of the grilleTarifaireDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/grille-tarifaires/{id}")
    public ResponseEntity<Void> deleteGrilleTarifaire(@PathVariable Long id) {
        log.debug("REST request to delete GrilleTarifaire : {}", id);
        grilleTarifaireService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/grille-tarifaires?query=:query} : search for the grilleTarifaire corresponding
     * to the query.
     *
     * @param query the query of the grilleTarifaire search.
     * @return the result of the search.
     */
    @GetMapping("/_search/grille-tarifaires")
    public List<GrilleTarifaireDTO> searchGrilleTarifaires(@RequestParam String query) {
        log.debug("REST request to search GrilleTarifaires for query {}", query);
        return grilleTarifaireService.search(query);
    }
}
