package sn.free.selfcare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.free.selfcare.domain.Produit;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.TypeOffre;

import java.util.List;

/**
 * Spring Data repository for the Produit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByTypeProduit(TypeOffre typeProduit);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Produit e WHERE e.client.id = :clientId AND e.status <> :status AND (lower(e.nom) LIKE lower(:query))")
    Page<Produit> findProduitsByClientAndQuery(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, @Param("query") String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Produit e WHERE e.client.id = :clientId AND e.status <> :status")
    Page<Produit> findProduitsByClient(@Param("clientId") Long clientId, @Param("status") ObjectStatus status, Pageable pageable);

    List<Produit> findByNomIgnoreCase(String nom);

    List<Produit> findByCreditAndSmsAndMinAppelAndGoData(Double credit, Double sms, Double minAppel, Double goData);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Produit e WHERE e.status <> 'ARCHIVED'")
    List<Produit> findAll();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query(value = "SELECT COUNT(p.id) FROM Produit p WHERE YEAR(p.createdDate) = :year AND MONTH(p.createdDate) = :month")
    long getNumberOfProductsPerMonth(@Param("year") int year, @Param("month") int month);
}
