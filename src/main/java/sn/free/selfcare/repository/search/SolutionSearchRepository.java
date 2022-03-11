package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Solution;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Solution} entity.
 */
public interface SolutionSearchRepository extends ElasticsearchRepository<Solution, Long> {
}
