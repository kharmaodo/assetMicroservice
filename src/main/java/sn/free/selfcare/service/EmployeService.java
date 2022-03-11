package sn.free.selfcare.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sn.free.selfcare.service.dto.EmployeDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link sn.free.selfcare.domain.Employe}.
 */
public interface EmployeService {

    /**
     * Save a employe.
     *
     * @param employeDTO the entity to save.
     * @return the persisted entity.
     */
    EmployeDTO save(EmployeDTO employeDTO);

    /**
     * Get all the employes.
     *
     * @return the list of entities.
     */
    List<EmployeDTO> findAll();


    /**
     * Get the "id" employe.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EmployeDTO> findOne(Long id);

    /**
     * Delete the "id" employe.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search by filtering active employees by client
     *
     * @param clientId
     * @param query
     * @param pageable
     * @return the result the Paging of EmployeDTO
     */
    Page<EmployeDTO> searchEmployes(Long clientId, String query, Pageable pageable);

    /**
     * Get the "email" employe.
     *
     * @param email the email of the entity.
     * @param active if true, return only active entities.
     * @return the entity.
     */
    List<EmployeDTO> findAllByEmail(String email, boolean onlyActive);

    /**
     * Get the "clientId" employe.
     *
     * @param clientId the clientId of the entity.
     * @return the entity.
     */
    List<EmployeDTO> findAllByClientId(Long clientId);

    List<EmployeDTO> findEmployesWithOffreByGroupe(Long groupeId);

    /**
     * Save Employes imported from CSV file for given client
     *
     * @param clientId given client
     * @param file     csv file containing lignes data
     */
    void saveEmployesFromCSV(Long clientId, MultipartFile file) throws IOException;
}
