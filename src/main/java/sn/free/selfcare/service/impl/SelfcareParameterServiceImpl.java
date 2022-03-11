package sn.free.selfcare.service.impl;

import sn.free.selfcare.service.SelfcareParameterService;
import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.repository.SelfcareParameterRepository;
import sn.free.selfcare.repository.search.SelfcareParameterSearchRepository;
import sn.free.selfcare.service.dto.SelfcareParameterDTO;
import sn.free.selfcare.service.mapper.SelfcareParameterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link SelfcareParameter}.
 */
@Service
@Transactional
public class SelfcareParameterServiceImpl implements SelfcareParameterService {

	private final Logger log = LoggerFactory.getLogger(SelfcareParameterServiceImpl.class);
	// Ajout par MOF pour recuperation des options
	@Autowired
	private final SelfcareParameterRepository selfcareParameterRepository;
	// Fin Ajout

	private final SelfcareParameterMapper selfcareParameterMapper;

	private final SelfcareParameterSearchRepository selfcareParameterSearchRepository;

	public SelfcareParameterServiceImpl(SelfcareParameterRepository selfcareParameterRepository,
			SelfcareParameterMapper selfcareParameterMapper,
			SelfcareParameterSearchRepository selfcareParameterSearchRepository) {
		this.selfcareParameterRepository = selfcareParameterRepository;
		this.selfcareParameterMapper = selfcareParameterMapper;
		this.selfcareParameterSearchRepository = selfcareParameterSearchRepository;
	}

	@Override
	public SelfcareParameterDTO save(SelfcareParameterDTO selfcareParameterDTO) {
		log.debug("Request to save SelfcareParameter : {}", selfcareParameterDTO);
		SelfcareParameter selfcareParameter = selfcareParameterMapper.toEntity(selfcareParameterDTO);
		selfcareParameter = selfcareParameterRepository.save(selfcareParameter);
		SelfcareParameterDTO result = selfcareParameterMapper.toDto(selfcareParameter);
		selfcareParameterSearchRepository.save(selfcareParameter);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SelfcareParameterDTO> findAll() {
		log.debug("Request to get all SelfcareParameters");
		return selfcareParameterRepository.findAll().stream().map(selfcareParameterMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SelfcareParameterDTO> findOne(Long id) {
		log.debug("Request to get SelfcareParameter : {}", id);
		return selfcareParameterRepository.findById(id).map(selfcareParameterMapper::toDto);
	}

	// Methode pour récupérer les informations telles que raison sociale, adress
	// email et call center par MOF
	@SuppressWarnings("static-access")
	public List<SelfcareParameter> findParameterOptions(List<ParameterOption> options) {
		return selfcareParameterRepository.findParameterOptions(options);
	}

	// Fin Methode par MOF
	@Override
	public void delete(Long id) {
		log.debug("Request to delete SelfcareParameter : {}", id);
		selfcareParameterRepository.deleteById(id);
		selfcareParameterSearchRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SelfcareParameterDTO> search(String query) {
		log.debug("Request to search SelfcareParameters for query {}", query);
		return StreamSupport
				.stream(selfcareParameterSearchRepository.search(queryStringQuery(query)).spliterator(), false)
				.map(selfcareParameterMapper::toDto).collect(Collectors.toList());
	}
}
