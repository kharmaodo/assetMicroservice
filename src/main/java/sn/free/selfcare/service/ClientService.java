package sn.free.selfcare.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.free.selfcare.service.dto.ClientDTO;
import sn.free.selfcare.service.dto.NumberClientActiveKpiDTO;
import sn.free.selfcare.service.dto.NumberClientKpiDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Client}.
 */
public interface ClientService {

    /**
     * Save a client.
     *
     * @param clientDTO the entity to save.
     * @return the persisted entity.
     */
    ClientDTO save(ClientDTO clientDTO);

    /**
     * Get all the clients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClientDTO> findAll(Pageable pageable);

    /**
     * Get all the clients.
     *
     * @return the list of entities.
     */
    List<ClientDTO> findAll();

    /**
     * Get all the clients.
     *
     * @return the list of entities.
     */
    List<ClientDTO> findAllByGestionnaire(String gestionnaire);

    /**
     * Get the "id" client.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClientDTO> findOne(Long id);

    Page<ClientDTO> findByGestionnaire(String emailGestionnaire, Pageable pageable);

    /**
     * Delete the "id" client.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the client corresponding to the query.
     *
     * @param query    the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClientDTO> search(String query, Pageable pageable);

    byte[] export(String gestionnaire) throws IOException;

    Optional<ClientDTO> findOneByCode(String code);

    Optional<ClientDTO> findOneByIdNotAndCode(Long id, String code);

    Optional<ClientDTO> findOneByRaisonSociale(String raisonSociale);

    Optional<ClientDTO> findOneByIdNotAndRaisonSociale(Long id, String raisonSociale);

    Optional<ClientDTO> findOneByNinea(String ninea);

    Optional<ClientDTO> findOneByIdNotAndNinea(Long id, String ninea);

    /**
     * Search clients by gestionnaire and query
     *
     * @param gestionnaire
     * @param query
     * @param pageable
     * @return
     */
    Page<ClientDTO> searchClientsByGestionnaire(String gestionnaire, String query, Pageable pageable);

    List<NumberClientKpiDTO> getNumberOfClientsPerMonth(int year);

    List<NumberClientActiveKpiDTO> getNumberOfActiveClientsPerMonth(int year);

    List<Long> getActiveClientsIdList();
}
