package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Produit;
import sn.free.selfcare.repository.ProduitRepository;
import sn.free.selfcare.repository.search.ProduitSearchRepository;
import sn.free.selfcare.service.ProduitService;
import sn.free.selfcare.service.dto.ProduitDTO;
import sn.free.selfcare.service.mapper.ProduitMapper;

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

import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ProduitResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TypeOffre DEFAULT_TYPE_PRODUIT = TypeOffre.STANDARD;
    private static final TypeOffre UPDATED_TYPE_PRODUIT = TypeOffre.CUSTOM;

    private static final Double DEFAULT_CREDIT = 1D;
    private static final Double UPDATED_CREDIT = 2D;

    private static final Double DEFAULT_SMS = 1D;
    private static final Double UPDATED_SMS = 2D;

    private static final Double DEFAULT_MIN_APPEL = 1D;
    private static final Double UPDATED_MIN_APPEL = 2D;

    private static final Double DEFAULT_GO_DATA = 1D;
    private static final Double UPDATED_GO_DATA = 2D;

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitMapper produitMapper;

    @Autowired
    private ProduitService produitService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.ProduitSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProduitSearchRepository mockProduitSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProduitMockMvc;

    private Produit produit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity(EntityManager em) {
        Produit produit = new Produit()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .typeProduit(DEFAULT_TYPE_PRODUIT)
            .credit(DEFAULT_CREDIT)
            .sms(DEFAULT_SMS)
            .minAppel(DEFAULT_MIN_APPEL)
            .goData(DEFAULT_GO_DATA)
            .status(DEFAULT_STATUS);
        return produit;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity(EntityManager em) {
        Produit produit = new Produit()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .typeProduit(UPDATED_TYPE_PRODUIT)
            .credit(UPDATED_CREDIT)
            .sms(UPDATED_SMS)
            .minAppel(UPDATED_MIN_APPEL)
            .goData(UPDATED_GO_DATA)
            .status(UPDATED_STATUS);
        return produit;
    }

    @BeforeEach
    public void initTest() {
        produit = createEntity(em);
    }

    @Test
    @Transactional
    public void createProduit() throws Exception {
        int databaseSizeBeforeCreate = produitRepository.findAll().size();
        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);
        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isCreated());

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate + 1);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testProduit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduit.getTypeProduit()).isEqualTo(DEFAULT_TYPE_PRODUIT);
        assertThat(testProduit.getCredit()).isEqualTo(DEFAULT_CREDIT);
        assertThat(testProduit.getSms()).isEqualTo(DEFAULT_SMS);
        assertThat(testProduit.getMinAppel()).isEqualTo(DEFAULT_MIN_APPEL);
        assertThat(testProduit.getGoData()).isEqualTo(DEFAULT_GO_DATA);
        assertThat(testProduit.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Produit in Elasticsearch
        verify(mockProduitSearchRepository, times(1)).save(testProduit);
    }

    @Test
    @Transactional
    public void createProduitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = produitRepository.findAll().size();

        // Create the Produit with an existing ID
        produit.setId(1L);
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate);

        // Validate the Produit in Elasticsearch
        verify(mockProduitSearchRepository, times(0)).save(produit);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setNom(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeProduitIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setTypeProduit(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreditIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setCredit(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSmsIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setSms(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMinAppelIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setMinAppel(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGoDataIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setGoData(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = produitRepository.findAll().size();
        // set the field null
        produit.setStatus(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);


        restProduitMockMvc.perform(post("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProduits() throws Exception {
        // Initialize the database
        produitRepository.saveAndFlush(produit);

        // Get all the produitList
        restProduitMockMvc.perform(get("/api/produits?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].typeProduit").value(hasItem(DEFAULT_TYPE_PRODUIT.toString())))
            .andExpect(jsonPath("$.[*].credit").value(hasItem(DEFAULT_CREDIT.doubleValue())))
            .andExpect(jsonPath("$.[*].sms").value(hasItem(DEFAULT_SMS.doubleValue())))
            .andExpect(jsonPath("$.[*].minAppel").value(hasItem(DEFAULT_MIN_APPEL.doubleValue())))
            .andExpect(jsonPath("$.[*].goData").value(hasItem(DEFAULT_GO_DATA.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getProduit() throws Exception {
        // Initialize the database
        produitRepository.saveAndFlush(produit);

        // Get the produit
        restProduitMockMvc.perform(get("/api/produits/{id}", produit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produit.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.typeProduit").value(DEFAULT_TYPE_PRODUIT.toString()))
            .andExpect(jsonPath("$.credit").value(DEFAULT_CREDIT.doubleValue()))
            .andExpect(jsonPath("$.sms").value(DEFAULT_SMS.doubleValue()))
            .andExpect(jsonPath("$.minAppel").value(DEFAULT_MIN_APPEL.doubleValue()))
            .andExpect(jsonPath("$.goData").value(DEFAULT_GO_DATA.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingProduit() throws Exception {
        // Get the produit
        restProduitMockMvc.perform(get("/api/produits/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProduit() throws Exception {
        // Initialize the database
        produitRepository.saveAndFlush(produit);

        int databaseSizeBeforeUpdate = produitRepository.findAll().size();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).get();
        // Disconnect from session so that the updates on updatedProduit are not directly saved in db
        em.detach(updatedProduit);
        updatedProduit
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .typeProduit(UPDATED_TYPE_PRODUIT)
            .credit(UPDATED_CREDIT)
            .sms(UPDATED_SMS)
            .minAppel(UPDATED_MIN_APPEL)
            .goData(UPDATED_GO_DATA)
            .status(UPDATED_STATUS);
        ProduitDTO produitDTO = produitMapper.toDto(updatedProduit);

        restProduitMockMvc.perform(put("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isOk());

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProduit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduit.getTypeProduit()).isEqualTo(UPDATED_TYPE_PRODUIT);
        assertThat(testProduit.getCredit()).isEqualTo(UPDATED_CREDIT);
        assertThat(testProduit.getSms()).isEqualTo(UPDATED_SMS);
        assertThat(testProduit.getMinAppel()).isEqualTo(UPDATED_MIN_APPEL);
        assertThat(testProduit.getGoData()).isEqualTo(UPDATED_GO_DATA);
        assertThat(testProduit.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Produit in Elasticsearch
        verify(mockProduitSearchRepository, times(1)).save(testProduit);
    }

    @Test
    @Transactional
    public void updateNonExistingProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().size();

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitMockMvc.perform(put("/api/produits").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Produit in Elasticsearch
        verify(mockProduitSearchRepository, times(0)).save(produit);
    }

    @Test
    @Transactional
    public void deleteProduit() throws Exception {
        // Initialize the database
        produitRepository.saveAndFlush(produit);

        int databaseSizeBeforeDelete = produitRepository.findAll().size();

        // Delete the produit
        restProduitMockMvc.perform(delete("/api/produits/{id}", produit.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Produit> produitList = produitRepository.findAll();
        assertThat(produitList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Produit in Elasticsearch
        verify(mockProduitSearchRepository, times(1)).deleteById(produit.getId());
    }

    @Test
    @Transactional
    public void searchProduit() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        produitRepository.saveAndFlush(produit);
        when(mockProduitSearchRepository.search(queryStringQuery("id:" + produit.getId())))
            .thenReturn(Collections.singletonList(produit));

        // Search the produit
        restProduitMockMvc.perform(get("/api/_search/produits?query=id:" + produit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].typeProduit").value(hasItem(DEFAULT_TYPE_PRODUIT.toString())))
            .andExpect(jsonPath("$.[*].credit").value(hasItem(DEFAULT_CREDIT.doubleValue())))
            .andExpect(jsonPath("$.[*].sms").value(hasItem(DEFAULT_SMS.doubleValue())))
            .andExpect(jsonPath("$.[*].minAppel").value(hasItem(DEFAULT_MIN_APPEL.doubleValue())))
            .andExpect(jsonPath("$.[*].goData").value(hasItem(DEFAULT_GO_DATA.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
