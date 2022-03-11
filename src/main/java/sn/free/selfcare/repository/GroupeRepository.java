package sn.free.selfcare.repository;

import sn.free.selfcare.domain.Groupe;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Spring Data  repository for the Groupe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Groupe e WHERE e.client.id = :clientId AND e.status <> :status AND (lower(e.nom) LIKE lower(:query) )")
    Page<Groupe> findGroupesByClientAndQuery(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, @Param("query") String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Groupe e WHERE e.client.id = :clientId AND e.status <> :status")
    Page<Groupe> findGroupesByClient(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, Pageable pageable);

    List<Groupe> findByNomIgnoreCase(String nom);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Groupe e WHERE e.status <> 'ARCHIVED'")
    List<Groupe> findAll();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Groupe e WHERE e.client.id = :clientId AND e.status = :status")
    List<Groupe> findGroupesByClientAndStatus(@Param("clientId") Long clientId, @Param("status") ObjectStatus status);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Groupe e WHERE e.client.id = :clientId AND e.nom = :nom AND e.status = 'ACTIVE'")
    Optional<Groupe> findByClientAndNom(@Param("clientId") Long clientId, @Param("nom") String nom);
}
