package sn.free.selfcare.web.rest;

import sn.free.selfcare.service.ProduitService;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;
import sn.free.selfcare.service.dto.ProduitDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Produit}.
 */
@RestController
@RequestMapping("/api")
public class ProduitResource {

    private final Logger log = LoggerFactory.getLogger(ProduitResource.class);

    private static final String ENTITY_NAME = "assetMicroserviceProduit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProduitService produitService;

    public ProduitResource(ProduitService produitService) {
        this.produitService = produitService;
    }

    /**
     * {@code POST  /produits} : Create a new produit.
     *
     * @param produitDTO the produitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produitDTO, or with status {@code 400 (Bad Request)} if the produit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/produits")
    public ResponseEntity<ProduitDTO> createProduit(@Valid @RequestBody ProduitDTO produitDTO) throws URISyntaxException {
        log.debug("REST request to save Produit : {}", produitDTO);
        if (produitDTO.getId() != null) {
            throw new BadRequestAlertException("A new produit cannot already have an ID", ENTITY_NAME, "idexists");
        }

        checkNomAndResourcesUnicity(produitDTO);

        ProduitDTO result = produitService.save(produitDTO);
        return ResponseEntity.created(new URI("/api/produits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /produits} : Updates an existing produit.
     *
     * @param produitDTO the produitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produitDTO,
     * or with status {@code 400 (Bad Request)} if the produitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/produits")
    public ResponseEntity<ProduitDTO> updateProduit(@Valid @RequestBody ProduitDTO produitDTO) throws URISyntaxException {
        log.debug("REST request to update Produit : {}", produitDTO);
        if (produitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        checkNomAndResourcesUnicity(produitDTO);

        ProduitDTO result = produitService.save(produitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produitDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /produits} : get all the produits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produits in body.
     */
    @GetMapping("/produits")
    public List<ProduitDTO> getAllProduits() {
        log.debug("REST request to get all Produits");
        return produitService.findAll();
    }

    /**
     * {@code GET  /produits/:id} : get the "id" produit.
     *
     * @param id the id of the produitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/produits/{id}")
    public ResponseEntity<ProduitDTO> getProduit(@PathVariable Long id) {
        log.debug("REST request to get Produit : {}", id);
        Optional<ProduitDTO> produitDTO = produitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(produitDTO);
    }

    /**
     * {@code DELETE  /produits/:id} : delete the "id" produit.
     *
     * @param id the id of the produitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/produits/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        log.debug("REST request to delete Produit : {}", id);
        produitService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/produits?query=:query} : search for the produit corresponding
     * to the query.
     *
     * @param query the query of the produit search.
     * @return the result of the search.
     */
    @GetMapping("/_search/produits")
    public List<ProduitDTO> searchProduits(@RequestParam String query) {
        log.debug("REST request to search Produits for query {}", query);
        return produitService.search(query);
    }

    @GetMapping("/client/{clientId}/produits")
    public ResponseEntity<List<ProduitDTO>> findAllByClientAndQuery(@PathVariable Long clientId, @RequestParam(required = false) String query, Pageable pageable) {
        log.debug("REST request to findAllByClient Produits for clientId {} and query {}", clientId.toString(), query);
        Page<ProduitDTO> page = produitService.findProduitsByClientAndQuery(clientId, query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    private void checkNomAndResourcesUnicity(ProduitDTO produitDTO) {
		List<ProduitDTO> produits = produitService.findAllByNom(produitDTO.getNom(), true);
		if (produits != null) {
			if (produitDTO.getId() != null) {
				produits = produits.stream()
						.filter(produit -> produit.getId() != produitDTO.getId())
						.collect(Collectors.toCollection(LinkedList::new));
			}
			if (produits.size() > 0) {
				final Long clientId = produitDTO.getClientId();
				if (clientId == null || (clientId != null && produits.stream().anyMatch(produit -> produit.getClientId() == clientId))) {
					throw new BadRequestAlertException("A produit with name \"" + produitDTO.getNom() + "\" already exists", ENTITY_NAME, "product.nomexists");
				}
			}
		}

		produits = produitService.findAllByCreditAndSmsAndMinAppelAndGoData(produitDTO.getCredit(), produitDTO.getSms(), produitDTO.getMinAppel(), produitDTO.getGoData(), true);
		if (produits != null) {
			if (produitDTO.getId() != null) {
				produits = produits.stream()
						.filter(produit -> produit.getId() != produitDTO.getId())
						.collect(Collectors.toCollection(LinkedList::new));
			}
			if (produits.size() > 0) {
				final Long clientId = produitDTO.getClientId();
				if (clientId == null || (clientId != null && produits.stream().anyMatch(produit -> produit.getClientId() == clientId))) {
					throw new BadRequestAlertException("A produit with name \"" + produitDTO.getNom() + "\" already exists", ENTITY_NAME, "product.resourcesexists");
				}
			}
		}
	}
}
