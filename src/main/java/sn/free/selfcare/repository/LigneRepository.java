package sn.free.selfcare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.free.selfcare.domain.Ligne;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Ligne entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LigneRepository extends JpaRepository<Ligne, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE e.client.id = :clientId AND e.status <> :status AND (lower(e.numero) LIKE lower(:query) OR lower(e.imsi) LIKE lower(:query))")
    Page<Ligne> findLignesByClientAndQuery(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, @Param("query") String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE e.client.id = :clientId AND e.status <> :status")
    Page<Ligne> findLignesByClient(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE e.status <> 'ARCHIVED'")
    List<Ligne> findAll();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE e.client.id = :clientId AND e.status <> 'ARCHIVED'")
    List<Ligne> findAllByClientId(@Param("clientId") Long clientId);

    List<Ligne> findByNumero(String numero);

    List<Ligne> findByImsi(String imsi);

    List<Ligne> findDistinctByNumeroOrImsi(String numero, String imsi);

    @EntityGraph(attributePaths = "services")
    Optional<Ligne> findOneWithServicesById(Long id);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE" +
        " (e.numero = :numero AND e.imsi = :imsi)" +
        " OR (e.imsi = :imsi AND e.status = 'ARCHIVED')")
    List<Ligne> getLignesToFilter(String numero, String imsi);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Ligne e WHERE e.client.id = :clientId AND e.numero = :numero AND e.status = 'ACTIVE'")
    Optional<Ligne> findByClientAndNumero(@Param("clientId") Long clientId, @Param("numero") String numero);
}
