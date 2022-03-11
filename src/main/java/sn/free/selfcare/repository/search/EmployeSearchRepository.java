package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Employe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Employe} entity.
 */
public interface EmployeSearchRepository extends ElasticsearchRepository<Employe, Long> {
}
