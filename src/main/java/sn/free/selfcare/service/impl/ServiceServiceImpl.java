package sn.free.selfcare.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import sn.free.selfcare.domain.Service;
import sn.free.selfcare.repository.ServiceRepository;
import sn.free.selfcare.repository.search.ServiceSearchRepository;
import sn.free.selfcare.service.ServiceService;
import sn.free.selfcare.service.dto.ServiceDTO;
import sn.free.selfcare.service.mapper.ServiceMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Service}.
 */
@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final Logger log = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;

    private final ServiceMapper serviceMapper;

    private final ServiceSearchRepository serviceSearchRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceMapper serviceMapper, ServiceSearchRepository serviceSearchRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.serviceSearchRepository = serviceSearchRepository;
    }

    @Override
    public ServiceDTO save(ServiceDTO serviceDTO) {
        log.debug("Request to save Service : {}", serviceDTO);
        Service service = serviceMapper.toEntity(serviceDTO);
        service = serviceRepository.save(service);
        ServiceDTO result = serviceMapper.toDto(service);
        serviceSearchRepository.save(service);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Services");
        return serviceRepository.findAll(pageable)
            .map(serviceMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceDTO> findOne(Long id) {
        log.debug("Request to get Service : {}", id);
        return serviceRepository.findById(id)
            .map(serviceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Service : {}", id);
        serviceRepository.deleteById(id);
        serviceSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Services for query {}", query);
        return serviceSearchRepository.search(queryStringQuery(query), pageable)
            .map(serviceMapper::toDto);
    }

    @Override
    public List<ServiceDTO> findAll() {
        log.debug("Request to get all Services");
        return serviceRepository.findAll().stream().map(serviceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<ServiceDTO> findByServiceDefault(Boolean servDefault) {
        log.debug("Request to get all default Services");
        return serviceRepository.findByServiceDefault(servDefault).stream().map(serviceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
