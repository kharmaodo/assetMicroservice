package sn.free.selfcare.service;

import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.service.dto.SelfcareParameterDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing
 * {@link sn.free.selfcare.domain.SelfcareParameter}.
 */
public interface SelfcareParameterService {

	/**
	 * Save a selfcareParameter.
	 *
	 * @param selfcareParameterDTO
	 *            the entity to save.
	 * @return the persisted entity.
	 */
	SelfcareParameterDTO save(SelfcareParameterDTO selfcareParameterDTO);

	/**
	 * Get all the selfcareParameters.
	 *
	 * @return the list of entities.
	 */
	List<SelfcareParameterDTO> findAll();

	/**
	 * Get the "id" selfcareParameter.
	 *
	 * @param id
	 *            the id of the entity.
	 * @return the entity.
	 */
	Optional<SelfcareParameterDTO> findOne(Long id);

	/**
	 * Delete the "id" selfcareParameter.
	 *
	 * @param id
	 *            the id of the entity.
	 */
	void delete(Long id);

	// Par MOF
	List<SelfcareParameter> findParameterOptions(List<ParameterOption> options);

	// Fin par MOF
	/**
	 * Search for the selfcareParameter corresponding to the query.
	 *
	 * @param query
	 *            the query of the search.
	 *
	 * @return the list of entities.
	 */
	List<SelfcareParameterDTO> search(String query);
}
