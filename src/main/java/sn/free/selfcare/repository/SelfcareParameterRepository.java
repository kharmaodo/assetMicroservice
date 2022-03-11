package sn.free.selfcare.repository;

import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.domain.enumeration.ParameterOption;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import feign.Param;

/**
 * Spring Data repository for the SelfcareParameter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SelfcareParameterRepository extends JpaRepository<SelfcareParameter, Long> {
	// recupere les options raison sociale, contact et call center par MOF
	@Query("SELECT s FROM SelfcareParameter s WHERE s.nom IN (:options)")
	List<SelfcareParameter> findParameterOptions(@Param("options")List<ParameterOption> options);
	// Fin par MOF

}
