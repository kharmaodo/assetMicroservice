package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Ligne;
import sn.free.selfcare.repository.LigneRepository;
import sn.free.selfcare.repository.search.LigneSearchRepository;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.service.mapper.LigneMapper;

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

import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link LigneResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LigneResourceIT {

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final String DEFAULT_IMSI = "AAAAAAAAAA";
    private static final String UPDATED_IMSI = "BBBBBBBBBB";

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private LigneRepository ligneRepository;

    @Autowired
    private LigneMapper ligneMapper;

    @Autowired
    private LigneService ligneService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.LigneSearchRepositoryMockConfiguration
     */
    @Autowired
    private LigneSearchRepository mockLigneSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneMockMvc;

    private Ligne ligne;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ligne createEntity(EntityManager em) {
        Ligne ligne = new Ligne()
            .numero(DEFAULT_NUMERO)
            .imsi(DEFAULT_IMSI)
            .status(DEFAULT_STATUS);
        return ligne;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ligne createUpdatedEntity(EntityManager em) {
        Ligne ligne = new Ligne()
            .numero(UPDATED_NUMERO)
            .imsi(UPDATED_IMSI)
            .status(UPDATED_STATUS);
        return ligne;
    }

    @BeforeEach
    public void initTest() {
        ligne = createEntity(em);
    }

    @Test
    @Transactional
    public void createLigne() throws Exception {
        int databaseSizeBeforeCreate = ligneRepository.findAll().size();
        // Create the Ligne
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);
        restLigneMockMvc.perform(post("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isCreated());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeCreate + 1);
        Ligne testLigne = ligneList.get(ligneList.size() - 1);
        assertThat(testLigne.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testLigne.getImsi()).isEqualTo(DEFAULT_IMSI);
        assertThat(testLigne.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Ligne in Elasticsearch
        verify(mockLigneSearchRepository, times(1)).save(testLigne);
    }

    @Test
    @Transactional
    public void createLigneWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ligneRepository.findAll().size();

        // Create the Ligne with an existing ID
        ligne.setId(1L);
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneMockMvc.perform(post("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeCreate);

        // Validate the Ligne in Elasticsearch
        verify(mockLigneSearchRepository, times(0)).save(ligne);
    }


    @Test
    @Transactional
    public void checkNumeroIsRequired() throws Exception {
        int databaseSizeBeforeTest = ligneRepository.findAll().size();
        // set the field null
        ligne.setNumero(null);

        // Create the Ligne, which fails.
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);


        restLigneMockMvc.perform(post("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isBadRequest());

        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImsiIsRequired() throws Exception {
        int databaseSizeBeforeTest = ligneRepository.findAll().size();
        // set the field null
        ligne.setImsi(null);

        // Create the Ligne, which fails.
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);


        restLigneMockMvc.perform(post("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isBadRequest());

        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = ligneRepository.findAll().size();
        // set the field null
        ligne.setStatus(null);

        // Create the Ligne, which fails.
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);


        restLigneMockMvc.perform(post("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isBadRequest());

        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLignes() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        // Get all the ligneList
        restLigneMockMvc.perform(get("/api/lignes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligne.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].imsi").value(hasItem(DEFAULT_IMSI)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        // Get the ligne
        restLigneMockMvc.perform(get("/api/lignes/{id}", ligne.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligne.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.imsi").value(DEFAULT_IMSI))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingLigne() throws Exception {
        // Get the ligne
        restLigneMockMvc.perform(get("/api/lignes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        int databaseSizeBeforeUpdate = ligneRepository.findAll().size();

        // Update the ligne
        Ligne updatedLigne = ligneRepository.findById(ligne.getId()).get();
        // Disconnect from session so that the updates on updatedLigne are not directly saved in db
        em.detach(updatedLigne);
        updatedLigne
            .numero(UPDATED_NUMERO)
            .imsi(UPDATED_IMSI)
            .status(UPDATED_STATUS);
        LigneDTO ligneDTO = ligneMapper.toDto(updatedLigne);

        restLigneMockMvc.perform(put("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isOk());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeUpdate);
        Ligne testLigne = ligneList.get(ligneList.size() - 1);
        assertThat(testLigne.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testLigne.getImsi()).isEqualTo(UPDATED_IMSI);
        assertThat(testLigne.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Ligne in Elasticsearch
        verify(mockLigneSearchRepository, times(1)).save(testLigne);
    }

    @Test
    @Transactional
    public void updateNonExistingLigne() throws Exception {
        int databaseSizeBeforeUpdate = ligneRepository.findAll().size();

        // Create the Ligne
        LigneDTO ligneDTO = ligneMapper.toDto(ligne);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneMockMvc.perform(put("/api/lignes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ligneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ligne in Elasticsearch
        verify(mockLigneSearchRepository, times(0)).save(ligne);
    }

    @Test
    @Transactional
    public void deleteLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        int databaseSizeBeforeDelete = ligneRepository.findAll().size();

        // Delete the ligne
        restLigneMockMvc.perform(delete("/api/lignes/{id}", ligne.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Ligne in Elasticsearch
        verify(mockLigneSearchRepository, times(1)).deleteById(ligne.getId());
    }

    @Test
    @Transactional
    public void searchLigne() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);
        when(mockLigneSearchRepository.search(queryStringQuery("id:" + ligne.getId())))
            .thenReturn(Collections.singletonList(ligne));

        // Search the ligne
        restLigneMockMvc.perform(get("/api/_search/lignes?query=id:" + ligne.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligne.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].imsi").value(hasItem(DEFAULT_IMSI)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
