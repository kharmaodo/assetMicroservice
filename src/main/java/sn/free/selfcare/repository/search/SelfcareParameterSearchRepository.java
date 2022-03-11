package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.SelfcareParameter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link SelfcareParameter} entity.
 */
public interface SelfcareParameterSearchRepository extends ElasticsearchRepository<SelfcareParameter, Long> {
}
