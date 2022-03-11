package sn.free.selfcare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.free.selfcare.domain.Client;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	//TODO : MD : Audit : Database instead of ORM powerful
    @Query(value = "select distinct client from Client client left join fetch client.offres", countQuery = "select count(distinct client) from Client client")
    Page<Client> findAllWithEagerRelationships(Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("select distinct client from Client client left join fetch client.offres")
    List<Client> findAllWithEagerRelationships();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("select client from Client client left join fetch client.offres where client.id =:id")
    Optional<Client> findOneWithEagerRelationships(@Param("id") Long id);

    Page<Client> findByGestionnaire(String gestionnaire, Pageable pageable);

    Page<Client> findByStatusNot(ObjectStatus status, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Client e WHERE e.status <> 'ARCHIVED'")
    List<Client> findAll();
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Client e WHERE e.status <> :status AND (lower(e.raisonSociale) LIKE lower(:query) or lower(e.ninea) like lower(:query) or lower(e.telephone) like lower(:query) or lower(e.email) like lower(:query) )")
    Page<Client> searchClients(@Param("status") ObjectStatus status, @Param("query") String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Client e WHERE  e.status <> :status")
    Page<Client> searchClients(@Param("status") ObjectStatus status, Pageable pageable);

    List<Client> findAllByGestionnaireAndStatusNot(String gestionnaire, ObjectStatus status);

    /**
     * Permet de retrouver par id un client avec tous les objets en relation.
     * Cette methode est utile pour la suppression logique.
     *
     * @param id
     * @return list of clients
     */
  //TODO : MD : Audit : Database instead of ORM powerful
    @EntityGraph(attributePaths = {"lignes", "groupes", "employes", "produitsPersonnalises", "offres"})
    @Query("SELECT e FROM Client e WHERE e.id =:id")
    Optional<Client> findClientByIdFetchAll(@Param("id") Long id);

    Optional<Client> findOneByCode(String code);

    Optional<Client> findOneByIdNotAndCode(Long id, String code);

    Optional<Client> findOneByRaisonSociale(String raisonSociale);

    Optional<Client> findOneByIdNotAndRaisonSociale(Long id, String raisonSociale);

    Optional<Client> findOneByNinea(String ninea);

    Optional<Client> findOneByIdNotAndNinea(Long id, String ninea);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Client e WHERE lower(e.gestionnaire) = lower(:gestionnaire) AND e.status <> :status AND (lower(e.raisonSociale) LIKE lower(:query) or lower(e.ninea) like lower(:query) or lower(e.telephone) like lower(:query) or lower(e.email) like lower(:query) )")
    Page<Client> searchClientsByGestionnaire(ObjectStatus status, String gestionnaire, String query, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT e FROM Client e WHERE lower(e.gestionnaire) = lower(:gestionnaire) AND e.status <> :status")
    Page<Client> searchClientsByGestionnaire(ObjectStatus status, String gestionnaire, Pageable pageable);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query(value = "SELECT COUNT(c.id) FROM Client c WHERE YEAR(c.createdDate) = :year AND MONTH(c.createdDate) = :month")
    long getNumberClientsPerMonth(@Param("year") int year, @Param("month") int month);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query(value = "SELECT COUNT(c.id) FROM Client c WHERE YEAR(c.createdDate) = :year AND MONTH(c.createdDate) = :month AND c.status = 'ACTIVE'")
    long getNumberOfActiveClientsPerMonth(@Param("year") int year, @Param("month") int month);
  //TODO : MD : Audit : Database instead of ORM powerful
    @Query("SELECT c.id FROM Client c WHERE  c.status = 'ACTIVE'")
    List<Long> getActiveClientsIdList();
}
