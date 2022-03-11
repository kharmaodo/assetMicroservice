package sn.free.selfcare.service;

import sn.free.selfcare.service.dto.OffreDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Offre}.
 */
public interface OffreService {

    /**
     * Save a offre.
     *
     * @param offreDTO the entity to save.
     * @return the persisted entity.
     */
    OffreDTO save(OffreDTO offreDTO);

    /**
     * Get all the offres.
     *
     * @return the list of entities.
     */
    List<OffreDTO> findAll();

    /**
     * Get all the offres with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<OffreDTO> findAllWithEagerRelationships(Pageable pageable);


    /**
     * Get the "id" offre.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OffreDTO> findOne(Long id);

    /**
     * Delete the "id" offre.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the offre corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    List<OffreDTO> search(String query);

    byte[] export(Long clientId) throws IOException;

}
