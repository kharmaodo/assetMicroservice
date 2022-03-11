package sn.free.selfcare.repository;

import sn.free.selfcare.domain.GrilleTarifaire;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the GrilleTarifaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GrilleTarifaireRepository extends JpaRepository<GrilleTarifaire, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
	@Query("SELECT e FROM GrilleTarifaire e WHERE e.status <> 'ARCHIVED'")
    List<GrilleTarifaire> findAll();
}
