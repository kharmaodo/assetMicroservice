package sn.free.selfcare.repository.search;

import sn.free.selfcare.domain.Produit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Produit} entity.
 */
public interface ProduitSearchRepository extends ElasticsearchRepository<Produit, Long> {
}
