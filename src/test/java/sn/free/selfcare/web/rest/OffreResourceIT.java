package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Offre;
import sn.free.selfcare.repository.OffreRepository;
import sn.free.selfcare.repository.search.OffreSearchRepository;
import sn.free.selfcare.service.OffreService;
import sn.free.selfcare.service.dto.OffreDTO;
import sn.free.selfcare.service.mapper.OffreMapper;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.domain.enumeration.ModePaiement;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link OffreResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class OffreResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final TypeOffre DEFAULT_TYPE_OFFRE = TypeOffre.STANDARD;
    private static final TypeOffre UPDATED_TYPE_OFFRE = TypeOffre.CUSTOM;

    private static final ModePaiement DEFAULT_MODE_PAIEMENT = ModePaiement.PREPAID;
    private static final ModePaiement UPDATED_MODE_PAIEMENT = ModePaiement.POSTPAID;

    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    private static final Integer DEFAULT_DUREE_ENGAGEMENT = 1;
    private static final Integer UPDATED_DUREE_ENGAGEMENT = 2;

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private OffreRepository offreRepository;

    @Mock
    private OffreRepository offreRepositoryMock;

    @Autowired
    private OffreMapper offreMapper;

    @Mock
    private OffreService offreServiceMock;

    @Autowired
    private OffreService offreService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.OffreSearchRepositoryMockConfiguration
     */
    @Autowired
    private OffreSearchRepository mockOffreSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOffreMockMvc;

    private Offre offre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Offre createEntity(EntityManager em) {
        Offre offre = new Offre()
            .code(DEFAULT_CODE)
            .typeOffre(DEFAULT_TYPE_OFFRE)
            .modePaiement(DEFAULT_MODE_PAIEMENT)
            .montant(DEFAULT_MONTANT)
            .dureeEngagement(DEFAULT_DUREE_ENGAGEMENT)
            .status(DEFAULT_STATUS);
        return offre;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Offre createUpdatedEntity(EntityManager em) {
        Offre offre = new Offre()
            .code(UPDATED_CODE)
            .typeOffre(UPDATED_TYPE_OFFRE)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .montant(UPDATED_MONTANT)
            .dureeEngagement(UPDATED_DUREE_ENGAGEMENT)
            .status(UPDATED_STATUS);
        return offre;
    }

    @BeforeEach
    public void initTest() {
        offre = createEntity(em);
    }

    @Test
    @Transactional
    public void createOffre() throws Exception {
        int databaseSizeBeforeCreate = offreRepository.findAll().size();
        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);
        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isCreated());

        // Validate the Offre in the database
        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeCreate + 1);
        Offre testOffre = offreList.get(offreList.size() - 1);
        assertThat(testOffre.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOffre.getTypeOffre()).isEqualTo(DEFAULT_TYPE_OFFRE);
        assertThat(testOffre.getModePaiement()).isEqualTo(DEFAULT_MODE_PAIEMENT);
        assertThat(testOffre.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testOffre.getDureeEngagement()).isEqualTo(DEFAULT_DUREE_ENGAGEMENT);
        assertThat(testOffre.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Offre in Elasticsearch
        verify(mockOffreSearchRepository, times(1)).save(testOffre);
    }

    @Test
    @Transactional
    public void createOffreWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = offreRepository.findAll().size();

        // Create the Offre with an existing ID
        offre.setId(1L);
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeCreate);

        // Validate the Offre in Elasticsearch
        verify(mockOffreSearchRepository, times(0)).save(offre);
    }


    @Test
    @Transactional
    public void checkTypeOffreIsRequired() throws Exception {
        int databaseSizeBeforeTest = offreRepository.findAll().size();
        // set the field null
        offre.setTypeOffre(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);


        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkModePaiementIsRequired() throws Exception {
        int databaseSizeBeforeTest = offreRepository.findAll().size();
        // set the field null
        offre.setModePaiement(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);


        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMontantIsRequired() throws Exception {
        int databaseSizeBeforeTest = offreRepository.findAll().size();
        // set the field null
        offre.setMontant(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);


        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = offreRepository.findAll().size();
        // set the field null
        offre.setStatus(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);


        restOffreMockMvc.perform(post("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOffres() throws Exception {
        // Initialize the database
        offreRepository.saveAndFlush(offre);

        // Get all the offreList
        restOffreMockMvc.perform(get("/api/offres?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offre.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].typeOffre").value(hasItem(DEFAULT_TYPE_OFFRE.toString())))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].dureeEngagement").value(hasItem(DEFAULT_DUREE_ENGAGEMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllOffresWithEagerRelationshipsIsEnabled() throws Exception {
        when(offreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreMockMvc.perform(get("/api/offres?eagerload=true"))
            .andExpect(status().isOk());

        verify(offreServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllOffresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(offreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreMockMvc.perform(get("/api/offres?eagerload=true"))
            .andExpect(status().isOk());

        verify(offreServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getOffre() throws Exception {
        // Initialize the database
        offreRepository.saveAndFlush(offre);

        // Get the offre
        restOffreMockMvc.perform(get("/api/offres/{id}", offre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(offre.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.typeOffre").value(DEFAULT_TYPE_OFFRE.toString()))
            .andExpect(jsonPath("$.modePaiement").value(DEFAULT_MODE_PAIEMENT.toString()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()))
            .andExpect(jsonPath("$.dureeEngagement").value(DEFAULT_DUREE_ENGAGEMENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingOffre() throws Exception {
        // Get the offre
        restOffreMockMvc.perform(get("/api/offres/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOffre() throws Exception {
        // Initialize the database
        offreRepository.saveAndFlush(offre);

        int databaseSizeBeforeUpdate = offreRepository.findAll().size();

        // Update the offre
        Offre updatedOffre = offreRepository.findById(offre.getId()).get();
        // Disconnect from session so that the updates on updatedOffre are not directly saved in db
        em.detach(updatedOffre);
        updatedOffre
            .code(UPDATED_CODE)
            .typeOffre(UPDATED_TYPE_OFFRE)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .montant(UPDATED_MONTANT)
            .dureeEngagement(UPDATED_DUREE_ENGAGEMENT)
            .status(UPDATED_STATUS);
        OffreDTO offreDTO = offreMapper.toDto(updatedOffre);

        restOffreMockMvc.perform(put("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isOk());

        // Validate the Offre in the database
        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeUpdate);
        Offre testOffre = offreList.get(offreList.size() - 1);
        assertThat(testOffre.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOffre.getTypeOffre()).isEqualTo(UPDATED_TYPE_OFFRE);
        assertThat(testOffre.getModePaiement()).isEqualTo(UPDATED_MODE_PAIEMENT);
        assertThat(testOffre.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testOffre.getDureeEngagement()).isEqualTo(UPDATED_DUREE_ENGAGEMENT);
        assertThat(testOffre.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Offre in Elasticsearch
        verify(mockOffreSearchRepository, times(1)).save(testOffre);
    }

    @Test
    @Transactional
    public void updateNonExistingOffre() throws Exception {
        int databaseSizeBeforeUpdate = offreRepository.findAll().size();

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOffreMockMvc.perform(put("/api/offres").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Offre in Elasticsearch
        verify(mockOffreSearchRepository, times(0)).save(offre);
    }

    @Test
    @Transactional
    public void deleteOffre() throws Exception {
        // Initialize the database
        offreRepository.saveAndFlush(offre);

        int databaseSizeBeforeDelete = offreRepository.findAll().size();

        // Delete the offre
        restOffreMockMvc.perform(delete("/api/offres/{id}", offre.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Offre> offreList = offreRepository.findAll();
        assertThat(offreList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Offre in Elasticsearch
        verify(mockOffreSearchRepository, times(1)).deleteById(offre.getId());
    }

    @Test
    @Transactional
    public void searchOffre() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        offreRepository.saveAndFlush(offre);
        when(mockOffreSearchRepository.search(queryStringQuery("id:" + offre.getId())))
            .thenReturn(Collections.singletonList(offre));

        // Search the offre
        restOffreMockMvc.perform(get("/api/_search/offres?query=id:" + offre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offre.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].typeOffre").value(hasItem(DEFAULT_TYPE_OFFRE.toString())))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].dureeEngagement").value(hasItem(DEFAULT_DUREE_ENGAGEMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
