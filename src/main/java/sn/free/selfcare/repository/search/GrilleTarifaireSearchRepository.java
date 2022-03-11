package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.GrilleTarifaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link GrilleTarifaire} entity.
 */
public interface GrilleTarifaireSearchRepository extends ElasticsearchRepository<GrilleTarifaire, Long> {
}
