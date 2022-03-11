package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Groupe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Groupe} entity.
 */
public interface GroupeSearchRepository extends ElasticsearchRepository<Groupe, Long> {
}
