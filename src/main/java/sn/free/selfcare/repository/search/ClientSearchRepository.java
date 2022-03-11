package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Client;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Client} entity.
 */
public interface ClientSearchRepository extends ElasticsearchRepository<Client, Long> {
}
