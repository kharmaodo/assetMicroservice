package sn.free.selfcare.service;

import sn.free.selfcare.service.dto.ServiceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Service}.
 */
public interface ServiceService {

    /**
     * Save a service.
     *
     * @param serviceDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceDTO save(ServiceDTO serviceDTO);

    /**
     * Get all the services.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceDTO> findAll(Pageable pageable);


    /**
     * Get the "id" service.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceDTO> findOne(Long id);

    /**
     * Delete the "id" service.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the service corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceDTO> search(String query, Pageable pageable);

    List<ServiceDTO> findAll();

    List<ServiceDTO> findByServiceDefault(Boolean servDefault);
}
