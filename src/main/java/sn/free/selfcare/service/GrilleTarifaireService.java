package sn.free.selfcare.service;

import sn.free.selfcare.service.dto.GrilleTarifaireDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.GrilleTarifaire}.
 */
public interface GrilleTarifaireService {

    /**
     * Save a grilleTarifaire.
     *
     * @param grilleTarifaireDTO the entity to save.
     * @return the persisted entity.
     */
    GrilleTarifaireDTO save(GrilleTarifaireDTO grilleTarifaireDTO);

    /**
     * Get all the grilleTarifaires.
     *
     * @return the list of entities.
     */
    List<GrilleTarifaireDTO> findAll();


    /**
     * Get the "id" grilleTarifaire.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GrilleTarifaireDTO> findOne(Long id);

    /**
     * Delete the "id" grilleTarifaire.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the grilleTarifaire corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<GrilleTarifaireDTO> search(String query);
}
