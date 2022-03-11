package sn.free.selfcare.service;

import java.util.List;
import java.util.Optional;

import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.service.dto.NumberProductKpiDTO;
import sn.free.selfcare.service.dto.ProduitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Produit}.
 */
public interface ProduitService {

	/**
	 * Save a produit.
	 *
	 * @param produitDTO
	 *            the entity to save.
	 * @return the persisted entity.
	 */
	ProduitDTO save(ProduitDTO produitDTO);

	/**
	 * Get all the produits.
	 *
	 * @return the list of entities.
	 */
	List<ProduitDTO> findAll();

	List<ProduitDTO> findByTypeOffre(TypeOffre typeProduit, Long clientId);

	/**
	 * Get the "id" produit.
	 *
	 * @param id
	 *            the id of the entity.
	 * @return the entity.
	 */
	Optional<ProduitDTO> findOne(Long id);

	/**
	 * Delete the "id" produit.
	 *
	 * @param id
	 *            the id of the entity.
	 */
	void delete(Long id);

	/**
	 * Search for the produit corresponding to the query.
	 *
	 * @param query
	 *            the query of the search.
	 *
	 * @return the list of entities.
	 */
	List<ProduitDTO> search(String query);

	Page<ProduitDTO> findProduitsByClientAndQuery(Long clientId, String query, Pageable pageable);

	/**
     * Get the "nom" produit.
     *
     * @param nom the nom of the entity.
     * @param active if true, return only active entities.
     * @return the entity.
     */
    List<ProduitDTO> findAllByNom(String nom, boolean onlyActive);

    /**
     * Get the produit matching query params.
     *
     * @param credit the credit of the entity.
     * @param sms the sms of the entity.
     * @param minAppel the minAppel of the entity.
     * @param goData the goData of the entity.
     * @param active if true, return only active entities.
     * @return the entity.
     */
    List<ProduitDTO> findAllByCreditAndSmsAndMinAppelAndGoData(Double credit, Double sms, Double minAppel, Double goData, boolean onlyActive);

    List<NumberProductKpiDTO> getNumberOfProductsPerMonth(int year);
}
