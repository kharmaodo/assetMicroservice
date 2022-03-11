package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Ligne;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Ligne} entity.
 */
public interface LigneSearchRepository extends ElasticsearchRepository<Ligne, Long> {
}
