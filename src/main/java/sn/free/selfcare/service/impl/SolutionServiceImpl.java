package sn.free.selfcare.service.impl;

import sn.free.selfcare.service.SolutionService;
import sn.free.selfcare.domain.Solution;
import sn.free.selfcare.repository.SolutionRepository;
import sn.free.selfcare.repository.search.SolutionSearchRepository;
import sn.free.selfcare.service.dto.SolutionDTO;
import sn.free.selfcare.service.mapper.SolutionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Solution}.
 */
@Service
@Transactional
public class SolutionServiceImpl implements SolutionService {

    private final Logger log = LoggerFactory.getLogger(SolutionServiceImpl.class);

    private final SolutionRepository solutionRepository;

    private final SolutionMapper solutionMapper;

    private final SolutionSearchRepository solutionSearchRepository;

    public SolutionServiceImpl(SolutionRepository solutionRepository, SolutionMapper solutionMapper, SolutionSearchRepository solutionSearchRepository) {
        this.solutionRepository = solutionRepository;
        this.solutionMapper = solutionMapper;
        this.solutionSearchRepository = solutionSearchRepository;
    }

    @Override
    public SolutionDTO save(SolutionDTO solutionDTO) {
        log.debug("Request to save Solution : {}", solutionDTO);
        Solution solution = solutionMapper.toEntity(solutionDTO);
        solution = solutionRepository.save(solution);
        SolutionDTO result = solutionMapper.toDto(solution);
        solutionSearchRepository.save(solution);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionDTO> findAll() {
        log.debug("Request to get all Solutions");
        return solutionRepository.findAll().stream()
            .map(solutionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<SolutionDTO> findOne(Long id) {
        log.debug("Request to get Solution : {}", id);
        return solutionRepository.findById(id)
            .map(solutionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Solution : {}", id);
        solutionRepository.deleteById(id);
        solutionSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionDTO> search(String query) {
        log.debug("Request to search Solutions for query {}", query);
        return StreamSupport
            .stream(solutionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(solutionMapper::toDto)
        .collect(Collectors.toList());
    }
}
