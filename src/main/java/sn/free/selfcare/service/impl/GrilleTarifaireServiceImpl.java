package sn.free.selfcare.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.free.selfcare.domain.GrilleTarifaire;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.repository.GrilleTarifaireRepository;
import sn.free.selfcare.repository.search.GrilleTarifaireSearchRepository;
import sn.free.selfcare.service.GrilleTarifaireService;
import sn.free.selfcare.service.dto.GrilleTarifaireDTO;
import sn.free.selfcare.service.mapper.GrilleTarifaireMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link GrilleTarifaire}.
 */
@Service
@Transactional
public class GrilleTarifaireServiceImpl implements GrilleTarifaireService {

    private final Logger log = LoggerFactory.getLogger(GrilleTarifaireServiceImpl.class);

    private final GrilleTarifaireRepository grilleTarifaireRepository;

    private final GrilleTarifaireMapper grilleTarifaireMapper;

    private final GrilleTarifaireSearchRepository grilleTarifaireSearchRepository;

    public GrilleTarifaireServiceImpl(GrilleTarifaireRepository grilleTarifaireRepository, GrilleTarifaireMapper grilleTarifaireMapper, GrilleTarifaireSearchRepository grilleTarifaireSearchRepository) {
        this.grilleTarifaireRepository = grilleTarifaireRepository;
        this.grilleTarifaireMapper = grilleTarifaireMapper;
        this.grilleTarifaireSearchRepository = grilleTarifaireSearchRepository;
    }

    @Override
    public GrilleTarifaireDTO save(GrilleTarifaireDTO grilleTarifaireDTO) {
        log.debug("Request to save GrilleTarifaire : {}", grilleTarifaireDTO);
        GrilleTarifaire grilleTarifaire = grilleTarifaireMapper.toEntity(grilleTarifaireDTO);
        grilleTarifaire = grilleTarifaireRepository.save(grilleTarifaire);
        GrilleTarifaireDTO result = grilleTarifaireMapper.toDto(grilleTarifaire);
        grilleTarifaireSearchRepository.save(grilleTarifaire);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrilleTarifaireDTO> findAll() {
        log.debug("Request to get all GrilleTarifaires");
        return grilleTarifaireRepository.findAll().stream()
            .map(grilleTarifaireMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<GrilleTarifaireDTO> findOne(Long id) {
        log.debug("Request to get GrilleTarifaire : {}", id);
        return grilleTarifaireRepository.findById(id)
            .map(grilleTarifaireMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete GrilleTarifaire : {}", id);
        Optional<GrilleTarifaire> optionalGrilleTarifaire = grilleTarifaireRepository.findById(id);
        if (optionalGrilleTarifaire.isPresent()) {
            GrilleTarifaire grilleTarifaire = optionalGrilleTarifaire.get();
            grilleTarifaire.setStatus(ObjectStatus.ARCHIVED);
            grilleTarifaire = grilleTarifaireRepository.save(grilleTarifaire);
            grilleTarifaireSearchRepository.save(grilleTarifaire);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrilleTarifaireDTO> search(String query) {
        log.debug("Request to search GrilleTarifaires for query {}", query);
        return StreamSupport
            .stream(grilleTarifaireSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(grilleTarifaireMapper::toDto)
        .collect(Collectors.toList());
    }
}
