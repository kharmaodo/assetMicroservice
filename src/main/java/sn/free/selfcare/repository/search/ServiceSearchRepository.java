package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Service;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Service} entity.
 */
public interface ServiceSearchRepository extends ElasticsearchRepository<Service, Long> {
}
