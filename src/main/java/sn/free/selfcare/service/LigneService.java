package sn.free.selfcare.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.service.dto.LigneDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Ligne}.
 */
public interface LigneService {

    /**
     * Save a ligne.
     *
     * @param ligneDTO the entity to save.
     * @return the persisted entity.
     */
    LigneDTO save(LigneDTO ligneDTO);

    /**
     * Get all the lignes.
     *
     * @return the list of entities.
     */
    List<LigneDTO> findAll();

    /**
     * Get all the lignes.
     *
     * @return the list of entities.
     */
    List<LigneDTO> findAllByOffreId(Long offreId);

    /**
     * Get all the LigneDTO where Employe is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<LigneDTO> findAllWhereEmployeIsNull();

    /**
     * Get all the LigneDTO of client where Offre is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<LigneDTO> findAllByClientIdAndOffreIsNull(Long clientId, Long offreId);

    /**
     * Get all the LigneDTO of client.
     *
     * @return the {@link List} of entities.
     */
    List<LigneDTO> findAllByClientId(Long clientId);


    /**
     * Get the "id" ligne.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LigneDTO> findOne(Long id);

    /**
     * Get Ligne with attached Services
     *
     * @param id
     * @return
     */
    Optional<LigneDTO> findOneWithServicesById(Long id);

    /**
     * Get the "numero" ligne.
     *
     * @param numero     the numero of the entity.
     * @param onlyActive if true, return only active entities.
     * @return the entity.
     */
    List<LigneDTO> findAllByNumero(String numero, boolean onlyActive);

    /**
     * Get the "imsi" ligne.
     *
     * @param imsi       the imsi of the entity.
     * @param onlyActive if true, return only active entities.
     * @return the entity.
     */
    List<LigneDTO> findAllByImsi(String imsi, boolean onlyActive);

    /**
     * Get all "lignes" objects with same "numero" or "imsi"
     *
     * @param numero
     * @param imsi
     * @return list of "ligne"
     */
    List<LigneDTO> findDistinctByNumeroOrImsi(String numero, String imsi);

    /**
     * Delete the "id" ligne.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the ligne corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<LigneDTO> search(String query);

    Page<LigneDTO> findLignesByClientAndQuery(Long clientId, String query, Pageable pageable);

    byte[] export(Long clientId) throws IOException;

    /**
     * Save lignes imported from CSV file for given client
     *
     * @param clientId given client
     * @param file     csv file containing lignes data
     */
    void saveLignesFromCSV(Long clientId, MultipartFile file) throws IOException;

    List<LigneDTO> getLignesToFilter(LigneDTO ligne);

    Optional<LigneDTO> findByClientAndNumero(Long clientId, String numero);
}
