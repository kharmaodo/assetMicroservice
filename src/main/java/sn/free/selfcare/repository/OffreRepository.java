package sn.free.selfcare.repository;

import sn.free.selfcare.domain.Offre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Offre entity.
 */
@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
    @Query(value = "select distinct offre from Offre offre left join fetch offre.clients left join fetch offre.produits",
        countQuery = "select count(distinct offre) from Offre offre")
    Page<Offre> findAllWithEagerRelationships(Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("select distinct offre from Offre offre left join fetch offre.clients left join fetch offre.produits")
    List<Offre> findAllWithEagerRelationships();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("select offre from Offre offre left join fetch offre.clients left join fetch offre.produits where offre.id =:id")
    Optional<Offre> findOneWithEagerRelationships(@Param("id") Long id);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT offre FROM Offre offre WHERE offre.status <> 'ARCHIVED'")
    List<Offre> findAll();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT offre FROM Offre offre join offre.clients client WHERE client.id = :clientId and offre.status <> 'ARCHIVED'")
    List<Offre> findAllByClientId(@Param("clientId") Long clientId);
}
