package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.GrilleTarifaire;
import sn.free.selfcare.repository.GrilleTarifaireRepository;
import sn.free.selfcare.repository.search.GrilleTarifaireSearchRepository;
import sn.free.selfcare.service.GrilleTarifaireService;
import sn.free.selfcare.service.dto.GrilleTarifaireDTO;
import sn.free.selfcare.service.mapper.GrilleTarifaireMapper;

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

import sn.free.selfcare.domain.enumeration.TypeTarification;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link GrilleTarifaireResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class GrilleTarifaireResourceIT {

    private static final TypeTarification DEFAULT_TYPE_TARIFICATION = TypeTarification.STANDARD;
    private static final TypeTarification UPDATED_TYPE_TARIFICATION = TypeTarification.CUSTOM;

    private static final Double DEFAULT_SMS = 1D;
    private static final Double UPDATED_SMS = 2D;

    private static final Double DEFAULT_MIN_APPEL = 1D;
    private static final Double UPDATED_MIN_APPEL = 2D;

    private static final Double DEFAULT_GO_DATA = 1D;
    private static final Double UPDATED_GO_DATA = 2D;

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private GrilleTarifaireRepository grilleTarifaireRepository;

    @Autowired
    private GrilleTarifaireMapper grilleTarifaireMapper;

    @Autowired
    private GrilleTarifaireService grilleTarifaireService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.GrilleTarifaireSearchRepositoryMockConfiguration
     */
    @Autowired
    private GrilleTarifaireSearchRepository mockGrilleTarifaireSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGrilleTarifaireMockMvc;

    private GrilleTarifaire grilleTarifaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GrilleTarifaire createEntity(EntityManager em) {
        GrilleTarifaire grilleTarifaire = new GrilleTarifaire()
            .typeTarification(DEFAULT_TYPE_TARIFICATION)
            .sms(DEFAULT_SMS)
            .minAppel(DEFAULT_MIN_APPEL)
            .goData(DEFAULT_GO_DATA)
            .status(DEFAULT_STATUS);
        return grilleTarifaire;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GrilleTarifaire createUpdatedEntity(EntityManager em) {
        GrilleTarifaire grilleTarifaire = new GrilleTarifaire()
            .typeTarification(UPDATED_TYPE_TARIFICATION)
            .sms(UPDATED_SMS)
            .minAppel(UPDATED_MIN_APPEL)
            .goData(UPDATED_GO_DATA)
            .status(UPDATED_STATUS);
        return grilleTarifaire;
    }

    @BeforeEach
    public void initTest() {
        grilleTarifaire = createEntity(em);
    }

    @Test
    @Transactional
    public void createGrilleTarifaire() throws Exception {
        int databaseSizeBeforeCreate = grilleTarifaireRepository.findAll().size();
        // Create the GrilleTarifaire
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);
        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isCreated());

        // Validate the GrilleTarifaire in the database
        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeCreate + 1);
        GrilleTarifaire testGrilleTarifaire = grilleTarifaireList.get(grilleTarifaireList.size() - 1);
        assertThat(testGrilleTarifaire.getTypeTarification()).isEqualTo(DEFAULT_TYPE_TARIFICATION);
        assertThat(testGrilleTarifaire.getSms()).isEqualTo(DEFAULT_SMS);
        assertThat(testGrilleTarifaire.getMinAppel()).isEqualTo(DEFAULT_MIN_APPEL);
        assertThat(testGrilleTarifaire.getGoData()).isEqualTo(DEFAULT_GO_DATA);
        assertThat(testGrilleTarifaire.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the GrilleTarifaire in Elasticsearch
        verify(mockGrilleTarifaireSearchRepository, times(1)).save(testGrilleTarifaire);
    }

    @Test
    @Transactional
    public void createGrilleTarifaireWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = grilleTarifaireRepository.findAll().size();

        // Create the GrilleTarifaire with an existing ID
        grilleTarifaire.setId(1L);
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GrilleTarifaire in the database
        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeCreate);

        // Validate the GrilleTarifaire in Elasticsearch
        verify(mockGrilleTarifaireSearchRepository, times(0)).save(grilleTarifaire);
    }


    @Test
    @Transactional
    public void checkTypeTarificationIsRequired() throws Exception {
        int databaseSizeBeforeTest = grilleTarifaireRepository.findAll().size();
        // set the field null
        grilleTarifaire.setTypeTarification(null);

        // Create the GrilleTarifaire, which fails.
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);


        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSmsIsRequired() throws Exception {
        int databaseSizeBeforeTest = grilleTarifaireRepository.findAll().size();
        // set the field null
        grilleTarifaire.setSms(null);

        // Create the GrilleTarifaire, which fails.
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);


        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMinAppelIsRequired() throws Exception {
        int databaseSizeBeforeTest = grilleTarifaireRepository.findAll().size();
        // set the field null
        grilleTarifaire.setMinAppel(null);

        // Create the GrilleTarifaire, which fails.
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);


        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGoDataIsRequired() throws Exception {
        int databaseSizeBeforeTest = grilleTarifaireRepository.findAll().size();
        // set the field null
        grilleTarifaire.setGoData(null);

        // Create the GrilleTarifaire, which fails.
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);


        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = grilleTarifaireRepository.findAll().size();
        // set the field null
        grilleTarifaire.setStatus(null);

        // Create the GrilleTarifaire, which fails.
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);


        restGrilleTarifaireMockMvc.perform(post("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGrilleTarifaires() throws Exception {
        // Initialize the database
        grilleTarifaireRepository.saveAndFlush(grilleTarifaire);

        // Get all the grilleTarifaireList
        restGrilleTarifaireMockMvc.perform(get("/api/grille-tarifaires?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grilleTarifaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeTarification").value(hasItem(DEFAULT_TYPE_TARIFICATION.toString())))
            .andExpect(jsonPath("$.[*].sms").value(hasItem(DEFAULT_SMS.doubleValue())))
            .andExpect(jsonPath("$.[*].minAppel").value(hasItem(DEFAULT_MIN_APPEL.doubleValue())))
            .andExpect(jsonPath("$.[*].goData").value(hasItem(DEFAULT_GO_DATA.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getGrilleTarifaire() throws Exception {
        // Initialize the database
        grilleTarifaireRepository.saveAndFlush(grilleTarifaire);

        // Get the grilleTarifaire
        restGrilleTarifaireMockMvc.perform(get("/api/grille-tarifaires/{id}", grilleTarifaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(grilleTarifaire.getId().intValue()))
            .andExpect(jsonPath("$.typeTarification").value(DEFAULT_TYPE_TARIFICATION.toString()))
            .andExpect(jsonPath("$.sms").value(DEFAULT_SMS.doubleValue()))
            .andExpect(jsonPath("$.minAppel").value(DEFAULT_MIN_APPEL.doubleValue()))
            .andExpect(jsonPath("$.goData").value(DEFAULT_GO_DATA.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingGrilleTarifaire() throws Exception {
        // Get the grilleTarifaire
        restGrilleTarifaireMockMvc.perform(get("/api/grille-tarifaires/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGrilleTarifaire() throws Exception {
        // Initialize the database
        grilleTarifaireRepository.saveAndFlush(grilleTarifaire);

        int databaseSizeBeforeUpdate = grilleTarifaireRepository.findAll().size();

        // Update the grilleTarifaire
        GrilleTarifaire updatedGrilleTarifaire = grilleTarifaireRepository.findById(grilleTarifaire.getId()).get();
        // Disconnect from session so that the updates on updatedGrilleTarifaire are not directly saved in db
        em.detach(updatedGrilleTarifaire);
        updatedGrilleTarifaire
            .typeTarification(UPDATED_TYPE_TARIFICATION)
            .sms(UPDATED_SMS)
            .minAppel(UPDATED_MIN_APPEL)
            .goData(UPDATED_GO_DATA)
            .status(UPDATED_STATUS);
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(updatedGrilleTarifaire);

        restGrilleTarifaireMockMvc.perform(put("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isOk());

        // Validate the GrilleTarifaire in the database
        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeUpdate);
        GrilleTarifaire testGrilleTarifaire = grilleTarifaireList.get(grilleTarifaireList.size() - 1);
        assertThat(testGrilleTarifaire.getTypeTarification()).isEqualTo(UPDATED_TYPE_TARIFICATION);
        assertThat(testGrilleTarifaire.getSms()).isEqualTo(UPDATED_SMS);
        assertThat(testGrilleTarifaire.getMinAppel()).isEqualTo(UPDATED_MIN_APPEL);
        assertThat(testGrilleTarifaire.getGoData()).isEqualTo(UPDATED_GO_DATA);
        assertThat(testGrilleTarifaire.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the GrilleTarifaire in Elasticsearch
        verify(mockGrilleTarifaireSearchRepository, times(1)).save(testGrilleTarifaire);
    }

    @Test
    @Transactional
    public void updateNonExistingGrilleTarifaire() throws Exception {
        int databaseSizeBeforeUpdate = grilleTarifaireRepository.findAll().size();

        // Create the GrilleTarifaire
        GrilleTarifaireDTO grilleTarifaireDTO = grilleTarifaireMapper.toDto(grilleTarifaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGrilleTarifaireMockMvc.perform(put("/api/grille-tarifaires").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(grilleTarifaireDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GrilleTarifaire in the database
        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeUpdate);

        // Validate the GrilleTarifaire in Elasticsearch
        verify(mockGrilleTarifaireSearchRepository, times(0)).save(grilleTarifaire);
    }

    @Test
    @Transactional
    public void deleteGrilleTarifaire() throws Exception {
        // Initialize the database
        grilleTarifaireRepository.saveAndFlush(grilleTarifaire);

        int databaseSizeBeforeDelete = grilleTarifaireRepository.findAll().size();

        // Delete the grilleTarifaire
        restGrilleTarifaireMockMvc.perform(delete("/api/grille-tarifaires/{id}", grilleTarifaire.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GrilleTarifaire> grilleTarifaireList = grilleTarifaireRepository.findAll();
        assertThat(grilleTarifaireList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the GrilleTarifaire in Elasticsearch
        verify(mockGrilleTarifaireSearchRepository, times(1)).deleteById(grilleTarifaire.getId());
    }

    @Test
    @Transactional
    public void searchGrilleTarifaire() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        grilleTarifaireRepository.saveAndFlush(grilleTarifaire);
        when(mockGrilleTarifaireSearchRepository.search(queryStringQuery("id:" + grilleTarifaire.getId())))
            .thenReturn(Collections.singletonList(grilleTarifaire));

        // Search the grilleTarifaire
        restGrilleTarifaireMockMvc.perform(get("/api/_search/grille-tarifaires?query=id:" + grilleTarifaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grilleTarifaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeTarification").value(hasItem(DEFAULT_TYPE_TARIFICATION.toString())))
            .andExpect(jsonPath("$.[*].sms").value(hasItem(DEFAULT_SMS.doubleValue())))
            .andExpect(jsonPath("$.[*].minAppel").value(hasItem(DEFAULT_MIN_APPEL.doubleValue())))
            .andExpect(jsonPath("$.[*].goData").value(hasItem(DEFAULT_GO_DATA.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
