package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Service;
import sn.free.selfcare.repository.ServiceRepository;
import sn.free.selfcare.repository.search.ServiceSearchRepository;
import sn.free.selfcare.service.ServiceService;
import sn.free.selfcare.service.dto.ServiceDTO;
import sn.free.selfcare.service.mapper.ServiceMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link ServiceResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ServiceResourceIT {

    private static final String DEFAULT_SERVICE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    private static final Boolean DEFAULT_SERVICE_DEFAULT = false;
    private static final Boolean UPDATED_SERVICE_DEFAULT = true;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServiceService serviceService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.ServiceSearchRepositoryMockConfiguration
     */
    @Autowired
    private ServiceSearchRepository mockServiceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceMockMvc;

    private Service service;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createEntity(EntityManager em) {
        Service service = new Service()
            .serviceCode(DEFAULT_SERVICE_CODE)
            .serviceName(DEFAULT_SERVICE_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .serviceDefault(DEFAULT_SERVICE_DEFAULT);
        return service;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createUpdatedEntity(EntityManager em) {
        Service service = new Service()
            .serviceCode(UPDATED_SERVICE_CODE)
            .serviceName(UPDATED_SERVICE_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .serviceDefault(UPDATED_SERVICE_DEFAULT);
        return service;
    }

    @BeforeEach
    public void initTest() {
        service = createEntity(em);
    }

    @Test
    @Transactional
    public void createService() throws Exception {
        int databaseSizeBeforeCreate = serviceRepository.findAll().size();
        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);
        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isCreated());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate + 1);
        Service testService = serviceList.get(serviceList.size() - 1);
        assertThat(testService.getServiceCode()).isEqualTo(DEFAULT_SERVICE_CODE);
        assertThat(testService.getServiceName()).isEqualTo(DEFAULT_SERVICE_NAME);
        assertThat(testService.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testService.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testService.isServiceDefault()).isEqualTo(DEFAULT_SERVICE_DEFAULT);

        // Validate the Service in Elasticsearch
        verify(mockServiceSearchRepository, times(1)).save(testService);
    }

    @Test
    @Transactional
    public void createServiceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serviceRepository.findAll().size();

        // Create the Service with an existing ID
        service.setId(1L);
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate);

        // Validate the Service in Elasticsearch
        verify(mockServiceSearchRepository, times(0)).save(service);
    }


    @Test
    @Transactional
    public void checkServiceCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceRepository.findAll().size();
        // set the field null
        service.setServiceCode(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);


        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceRepository.findAll().size();
        // set the field null
        service.setStatus(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);


        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServices() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList
        restServiceMockMvc.perform(get("/api/services?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$.[*].serviceCode").value(hasItem(DEFAULT_SERVICE_CODE)))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].serviceDefault").value(hasItem(DEFAULT_SERVICE_DEFAULT.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(service.getId().intValue()))
            .andExpect(jsonPath("$.serviceCode").value(DEFAULT_SERVICE_CODE))
            .andExpect(jsonPath("$.serviceName").value(DEFAULT_SERVICE_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.serviceDefault").value(DEFAULT_SERVICE_DEFAULT.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingService() throws Exception {
        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        int databaseSizeBeforeUpdate = serviceRepository.findAll().size();

        // Update the service
        Service updatedService = serviceRepository.findById(service.getId()).get();
        // Disconnect from session so that the updates on updatedService are not directly saved in db
        em.detach(updatedService);
        updatedService
            .serviceCode(UPDATED_SERVICE_CODE)
            .serviceName(UPDATED_SERVICE_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .serviceDefault(UPDATED_SERVICE_DEFAULT);
        ServiceDTO serviceDTO = serviceMapper.toDto(updatedService);

        restServiceMockMvc.perform(put("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isOk());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate);
        Service testService = serviceList.get(serviceList.size() - 1);
        assertThat(testService.getServiceCode()).isEqualTo(UPDATED_SERVICE_CODE);
        assertThat(testService.getServiceName()).isEqualTo(UPDATED_SERVICE_NAME);
        assertThat(testService.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testService.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testService.isServiceDefault()).isEqualTo(UPDATED_SERVICE_DEFAULT);

        // Validate the Service in Elasticsearch
        verify(mockServiceSearchRepository, times(1)).save(testService);
    }

    @Test
    @Transactional
    public void updateNonExistingService() throws Exception {
        int databaseSizeBeforeUpdate = serviceRepository.findAll().size();

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc.perform(put("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Service in Elasticsearch
        verify(mockServiceSearchRepository, times(0)).save(service);
    }

    @Test
    @Transactional
    public void deleteService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        int databaseSizeBeforeDelete = serviceRepository.findAll().size();

        // Delete the service
        restServiceMockMvc.perform(delete("/api/services/{id}", service.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Service in Elasticsearch
        verify(mockServiceSearchRepository, times(1)).deleteById(service.getId());
    }

    @Test
    @Transactional
    public void searchService() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        serviceRepository.saveAndFlush(service);
        when(mockServiceSearchRepository.search(queryStringQuery("id:" + service.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(service), PageRequest.of(0, 1), 1));

        // Search the service
        restServiceMockMvc.perform(get("/api/_search/services?query=id:" + service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$.[*].serviceCode").value(hasItem(DEFAULT_SERVICE_CODE)))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].serviceDefault").value(hasItem(DEFAULT_SERVICE_DEFAULT.booleanValue())));
    }
}
