package sn.free.selfcare.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.free.selfcare.domain.Produit;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.helper.DateHelper;
import sn.free.selfcare.repository.ProduitRepository;
import sn.free.selfcare.repository.search.ProduitSearchRepository;
import sn.free.selfcare.service.ProduitService;
import sn.free.selfcare.service.dto.NumberProductKpiDTO;
import sn.free.selfcare.service.dto.ProduitDTO;
import sn.free.selfcare.service.mapper.ProduitMapper;
import sn.free.selfcare.web.rest.errors.ProduitAlreadyAssignedException;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Produit}.
 */
@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final Logger log = LoggerFactory.getLogger(ProduitServiceImpl.class);

    private final ProduitRepository produitRepository;

    private final ProduitMapper produitMapper;

    private final ProduitSearchRepository produitSearchRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository, ProduitMapper produitMapper, ProduitSearchRepository produitSearchRepository) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
        this.produitSearchRepository = produitSearchRepository;
    }

    @Override
    public ProduitDTO save(ProduitDTO produitDTO) {
        log.debug("Request to save Produit : {}", produitDTO);
        Produit produit = produitMapper.toEntity(produitDTO);
        produit = produitRepository.save(produit);
        ProduitDTO result = produitMapper.toDto(produit);
        produitSearchRepository.save(produit);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitDTO> findAll() {
        log.debug("Request to get all Produits");
        return produitRepository.findAll().stream()
            .map(produitMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitDTO> findByTypeOffre(TypeOffre typeProduit, Long clientId) {
        log.debug("Request to get Produits by TypeOffre");
        return produitRepository.findByTypeProduit(typeProduit).stream().map(produitMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProduitDTO> findOne(Long id) {
        log.debug("Request to get Produit : {}", id);
        return produitRepository.findById(id)
            .map(produitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Produit : {}", id);
        Optional<Produit> optionalProduit = produitRepository.findById(id);
        if (optionalProduit.isPresent()) {
            Produit produit = optionalProduit.get();

            // Vérifier si le produit n'est pas assigné à un groupe.
            if (produit.getGroupes().size() > 0) {
                throw new ProduitAlreadyAssignedException();
            }

            // Concaténer l'instant actuel au nom de produit
            produit.setNom(DateHelper.formatWithCurrentDateTime(produit.getNom()));

            // Procéder à la suppression logique le cas échéant.
            produit.setStatus(ObjectStatus.ARCHIVED);
            produit = produitRepository.save(produit);
            produitSearchRepository.save(produit);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitDTO> search(String query) {
        log.debug("Request to search Produits for query {}", query);
        return StreamSupport
            .stream(produitSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(produitMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ProduitDTO> findProduitsByClientAndQuery(Long clientId, String query, Pageable pageable) {

        Page<Produit> page;
        if (query != null && !query.isEmpty()) {
            page = produitRepository.findProduitsByClientAndQuery(clientId, ObjectStatus.ARCHIVED, "%" + query + "%", pageable);
        } else {
            page = produitRepository.findProduitsByClient(clientId, ObjectStatus.ARCHIVED, pageable);
        }
        return page.map(produitMapper::toDto);
    }

    @Override
    public List<ProduitDTO> findAllByNom(String nom, boolean onlyActive) {
        log.debug("Request to get all products : nom = {} ", nom);
        if (onlyActive) {
            return StreamSupport
                .stream(produitRepository.findByNomIgnoreCase(nom).spliterator(), false)
                .filter(produit -> produit.getStatus() != null && produit.getStatus().equals(ObjectStatus.ACTIVE))
                .map(produitMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        }

        return StreamSupport
            .stream(produitRepository.findByNomIgnoreCase(nom).spliterator(), false)
            .map(produitMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<ProduitDTO> findAllByCreditAndSmsAndMinAppelAndGoData(Double credit, Double sms, Double minAppel,
                                                                      Double goData, boolean onlyActive) {
        log.debug("Request to get all products : credit = {}, sms = {}, minAppel = {}, goData = {} ", credit, sms, minAppel, goData);
        if (onlyActive) {
            return StreamSupport
                .stream(produitRepository.findByCreditAndSmsAndMinAppelAndGoData(credit, sms, minAppel, goData).spliterator(), false)
                .filter(produit -> produit.getStatus() != null && produit.getStatus().equals(ObjectStatus.ACTIVE))
                .map(produitMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        }

        return StreamSupport
            .stream(produitRepository.findByCreditAndSmsAndMinAppelAndGoData(credit, sms, minAppel, goData).spliterator(), false)
            .map(produitMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<NumberProductKpiDTO> getNumberOfProductsPerMonth(int year) {
        List<NumberProductKpiDTO> kpiDTOs = new ArrayList<NumberProductKpiDTO>();
        for (Month month : Month.values()) {
            NumberProductKpiDTO kpiDTO = new NumberProductKpiDTO();
            kpiDTO.setMonthNumber(month.getValue());
            kpiDTO.setMonthName(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            kpiDTO.setNumberOfProducts(produitRepository.getNumberOfProductsPerMonth(year, month.getValue()));
            kpiDTOs.add(kpiDTO);
        }
        return kpiDTOs.stream().sorted().collect(Collectors.toList());
    }
}
