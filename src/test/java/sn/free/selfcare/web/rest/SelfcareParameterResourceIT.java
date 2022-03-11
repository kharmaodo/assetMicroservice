package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.repository.SelfcareParameterRepository;
import sn.free.selfcare.repository.search.SelfcareParameterSearchRepository;
import sn.free.selfcare.service.SelfcareParameterService;
import sn.free.selfcare.service.dto.SelfcareParameterDTO;
import sn.free.selfcare.service.mapper.SelfcareParameterMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import sn.free.selfcare.domain.enumeration.ParameterCategory;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.domain.enumeration.TypeValeur;
/**
 * Integration tests for the {@link SelfcareParameterResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class SelfcareParameterResourceIT {

    private static final ParameterCategory DEFAULT_CATEGORY = ParameterCategory.FREE_GLOBAL;
    private static final ParameterCategory UPDATED_CATEGORY = ParameterCategory.CLIENT;

    private static final ParameterOption DEFAULT_NOM = ParameterOption.FREE_RAISON_SOCIALE;
    private static final ParameterOption UPDATED_NOM = ParameterOption.FREE_EMAIL;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TypeValeur DEFAULT_TYPE = TypeValeur.PERIODE;
    private static final TypeValeur UPDATED_TYPE = TypeValeur.DATE;

    private static final String DEFAULT_VALEUR = "AAAAAAAAAA";
    private static final String UPDATED_VALEUR = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SEVERAL_VALUES = false;
    private static final Boolean UPDATED_SEVERAL_VALUES = true;

    @Autowired
    private SelfcareParameterRepository selfcareParameterRepository;

    @Autowired
    private SelfcareParameterMapper selfcareParameterMapper;

    @Autowired
    private SelfcareParameterService selfcareParameterService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.SelfcareParameterSearchRepositoryMockConfiguration
     */
    @Autowired
    private SelfcareParameterSearchRepository mockSelfcareParameterSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSelfcareParameterMockMvc;

    private SelfcareParameter selfcareParameter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SelfcareParameter createEntity(EntityManager em) {
        SelfcareParameter selfcareParameter = new SelfcareParameter()
            .category(DEFAULT_CATEGORY)
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .valeur(DEFAULT_VALEUR)
            .severalValues(DEFAULT_SEVERAL_VALUES);
        return selfcareParameter;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SelfcareParameter createUpdatedEntity(EntityManager em) {
        SelfcareParameter selfcareParameter = new SelfcareParameter()
            .category(UPDATED_CATEGORY)
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .valeur(UPDATED_VALEUR)
            .severalValues(UPDATED_SEVERAL_VALUES);
        return selfcareParameter;
    }

    @BeforeEach
    public void initTest() {
        selfcareParameter = createEntity(em);
    }

    @Test
    @Transactional
    public void createSelfcareParameter() throws Exception {
        int databaseSizeBeforeCreate = selfcareParameterRepository.findAll().size();
        // Create the SelfcareParameter
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(selfcareParameter);
        restSelfcareParameterMockMvc.perform(post("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isCreated());

        // Validate the SelfcareParameter in the database
        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeCreate + 1);
        SelfcareParameter testSelfcareParameter = selfcareParameterList.get(selfcareParameterList.size() - 1);
        assertThat(testSelfcareParameter.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testSelfcareParameter.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testSelfcareParameter.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSelfcareParameter.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSelfcareParameter.getValeur()).isEqualTo(DEFAULT_VALEUR);
        assertThat(testSelfcareParameter.isSeveralValues()).isEqualTo(DEFAULT_SEVERAL_VALUES);

        // Validate the SelfcareParameter in Elasticsearch
        verify(mockSelfcareParameterSearchRepository, times(1)).save(testSelfcareParameter);
    }

    @Test
    @Transactional
    public void createSelfcareParameterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = selfcareParameterRepository.findAll().size();

        // Create the SelfcareParameter with an existing ID
        selfcareParameter.setId(1L);
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(selfcareParameter);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSelfcareParameterMockMvc.perform(post("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SelfcareParameter in the database
        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeCreate);

        // Validate the SelfcareParameter in Elasticsearch
        verify(mockSelfcareParameterSearchRepository, times(0)).save(selfcareParameter);
    }


    @Test
    @Transactional
    public void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = selfcareParameterRepository.findAll().size();
        // set the field null
        selfcareParameter.setCategory(null);

        // Create the SelfcareParameter, which fails.
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(selfcareParameter);


        restSelfcareParameterMockMvc.perform(post("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isBadRequest());

        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = selfcareParameterRepository.findAll().size();
        // set the field null
        selfcareParameter.setNom(null);

        // Create the SelfcareParameter, which fails.
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(selfcareParameter);


        restSelfcareParameterMockMvc.perform(post("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isBadRequest());

        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSelfcareParameters() throws Exception {
        // Initialize the database
        selfcareParameterRepository.saveAndFlush(selfcareParameter);

        // Get all the selfcareParameterList
        restSelfcareParameterMockMvc.perform(get("/api/selfcare-parameters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(selfcareParameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].severalValues").value(hasItem(DEFAULT_SEVERAL_VALUES.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getSelfcareParameter() throws Exception {
        // Initialize the database
        selfcareParameterRepository.saveAndFlush(selfcareParameter);

        // Get the selfcareParameter
        restSelfcareParameterMockMvc.perform(get("/api/selfcare-parameters/{id}", selfcareParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(selfcareParameter.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR))
            .andExpect(jsonPath("$.severalValues").value(DEFAULT_SEVERAL_VALUES.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingSelfcareParameter() throws Exception {
        // Get the selfcareParameter
        restSelfcareParameterMockMvc.perform(get("/api/selfcare-parameters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSelfcareParameter() throws Exception {
        // Initialize the database
        selfcareParameterRepository.saveAndFlush(selfcareParameter);

        int databaseSizeBeforeUpdate = selfcareParameterRepository.findAll().size();

        // Update the selfcareParameter
        SelfcareParameter updatedSelfcareParameter = selfcareParameterRepository.findById(selfcareParameter.getId()).get();
        // Disconnect from session so that the updates on updatedSelfcareParameter are not directly saved in db
        em.detach(updatedSelfcareParameter);
        updatedSelfcareParameter
            .category(UPDATED_CATEGORY)
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .valeur(UPDATED_VALEUR)
            .severalValues(UPDATED_SEVERAL_VALUES);
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(updatedSelfcareParameter);

        restSelfcareParameterMockMvc.perform(put("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isOk());

        // Validate the SelfcareParameter in the database
        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeUpdate);
        SelfcareParameter testSelfcareParameter = selfcareParameterList.get(selfcareParameterList.size() - 1);
        assertThat(testSelfcareParameter.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testSelfcareParameter.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testSelfcareParameter.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSelfcareParameter.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSelfcareParameter.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testSelfcareParameter.isSeveralValues()).isEqualTo(UPDATED_SEVERAL_VALUES);

        // Validate the SelfcareParameter in Elasticsearch
        verify(mockSelfcareParameterSearchRepository, times(1)).save(testSelfcareParameter);
    }

    @Test
    @Transactional
    public void updateNonExistingSelfcareParameter() throws Exception {
        int databaseSizeBeforeUpdate = selfcareParameterRepository.findAll().size();

        // Create the SelfcareParameter
        SelfcareParameterDTO selfcareParameterDTO = selfcareParameterMapper.toDto(selfcareParameter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSelfcareParameterMockMvc.perform(put("/api/selfcare-parameters").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(selfcareParameterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SelfcareParameter in the database
        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SelfcareParameter in Elasticsearch
        verify(mockSelfcareParameterSearchRepository, times(0)).save(selfcareParameter);
    }

    @Test
    @Transactional
    public void deleteSelfcareParameter() throws Exception {
        // Initialize the database
        selfcareParameterRepository.saveAndFlush(selfcareParameter);

        int databaseSizeBeforeDelete = selfcareParameterRepository.findAll().size();

        // Delete the selfcareParameter
        restSelfcareParameterMockMvc.perform(delete("/api/selfcare-parameters/{id}", selfcareParameter.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SelfcareParameter> selfcareParameterList = selfcareParameterRepository.findAll();
        assertThat(selfcareParameterList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SelfcareParameter in Elasticsearch
        verify(mockSelfcareParameterSearchRepository, times(1)).deleteById(selfcareParameter.getId());
    }

    @Test
    @Transactional
    public void searchSelfcareParameter() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        selfcareParameterRepository.saveAndFlush(selfcareParameter);
        when(mockSelfcareParameterSearchRepository.search(queryStringQuery("id:" + selfcareParameter.getId())))
            .thenReturn(Collections.singletonList(selfcareParameter));

        // Search the selfcareParameter
        restSelfcareParameterMockMvc.perform(get("/api/_search/selfcare-parameters?query=id:" + selfcareParameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(selfcareParameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].severalValues").value(hasItem(DEFAULT_SEVERAL_VALUES.booleanValue())));
    }
}
