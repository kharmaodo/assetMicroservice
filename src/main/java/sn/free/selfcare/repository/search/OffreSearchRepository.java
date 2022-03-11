package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Offre;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Offre} entity.
 */
public interface OffreSearchRepository extends ElasticsearchRepository<Offre, Long> {
}
