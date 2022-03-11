package sn.free.selfcare.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.dto.operation.GroupeAdjustmentDTO;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Groupe}.
 */
public interface GroupeService {

	/**
	 * Save a groupe.
	 *
	 * @param groupeDTO
	 *            the entity to save.
	 * @return the persisted entity.
	 */
	GroupeDTO save(GroupeDTO groupeDTO);

	/**
	 * Get all the groupes.
	 *
	 * @return the list of entities.
	 */
	List<GroupeDTO> findAll();

	/**
	 * Get the "id" groupe.
	 *
	 * @param id
	 *            the id of the entity.
	 * @return the entity.
	 */
	Optional<GroupeDTO> findOne(Long id);

	/**
	 * Delete the "id" groupe.
	 *
	 * @param id
	 *            the id of the entity.
	 */
	void delete(Long id);

	/**
	 * Search for the groupe corresponding to the query.
	 *
	 * @param query
	 *            the query of the search.
	 *
	 * @return the list of entities.
	 */
	List<GroupeDTO> search(String query);

	Optional<GroupeAdjustmentDTO> findOneForAdjustment(Long id);

	Page<GroupeDTO> findGroupesByClientAndQuery(Long clientId, String query, Pageable pageable);

	 /**
     * Get the "nom" groupe.
     *
     * @param nom the nom of the entity.
     * @param active if true, return only active entities.
     * @return the entity.
     */
    List<GroupeDTO> findAllByNom(String nom, boolean onlyActive);

    List<GroupeDTO> findGroupesByClient(Long clientId);

    Optional<GroupeDTO> findByClientAndNom(Long clientId, String nom);
}
