package sn.free.selfcare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sn.free.selfcare.domain.Employe;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.service.dto.EmployeDTO;

/**
 * Spring Data  repository for the Employe entity.
 */
@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
    @Query("select e" +
          " from Employe e" +
            " left join e.ligne l" +
            " left join e.groupe g" +
           " where " +
            " e.client.id = :clientId" +
            " and e.status <> :status" +
            " and (" +
            " (lower(e.nom) like lower(:query)" +
            " or lower(e.prenom) like lower(:query)" +
            " or lower(e.fonction) like lower(:query)" +
            " or lower(e.email) like lower(:query))" +
            " or (l.id is not null and lower(l.numero) like lower(:query))" +
            " or (g.id is not null and lower(g.nom) like lower(:query)))")
    Page<Employe> searchEmployes(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, @Param("query") String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Employe e WHERE e.client.id = :clientId AND e.status <> :status")
    Page<Employe> searchEmployes(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, Pageable pageable);

    List<Employe> findByEmail(String email);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Employe e WHERE e.status <> 'ARCHIVED'")
    List<Employe> findAll();

    @Query("SELECT e FROM Employe e WHERE e.client.id = :clientId AND e.status <> 'ARCHIVED'")
    List<Employe> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT e FROM Employe e" +
        " LEFT JOIN e.ligne l" +
        " LEFT JOIN l.offre o" +
        " WHERE e.groupe.id = :groupeId" +
        " AND e.status = :status" +
        " AND o.id IS NOT NULL")
    List<Employe> findEmployesWithOffreByGroupe(@Param("groupeId") Long groupeId, @Param("status") ObjectStatus status);

    @Query("SELECT e FROM Employe e WHERE e.ligne.id = :ligneId AND e.status = 'ACTIVE'")
    Optional<Employe> findByLigne(@Param("ligneId") Long ligneId);
}
