package sn.free.selfcare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.free.selfcare.domain.Service;

import java.util.List;

/**
 * Spring Data  repository for the Service entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByServiceDefault(Boolean servDefault);
}
