package sn.free.selfcare.repository;

import sn.free.selfcare.domain.Solution;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Solution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
