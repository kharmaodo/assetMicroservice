package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Groupe;
import sn.free.selfcare.repository.GroupeRepository;
import sn.free.selfcare.repository.search.GroupeSearchRepository;
import sn.free.selfcare.service.GroupeService;
import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.mapper.GroupeMapper;

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

import sn.free.selfcare.domain.enumeration.Periodicite;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
/**
 * Integration tests for the {@link GroupeResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class GroupeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Periodicite DEFAULT_PERIODICITE = Periodicite.MENSUELLE;
    private static final Periodicite UPDATED_PERIODICITE = Periodicite.HEBDOMADAIRE;

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private GroupeMapper groupeMapper;

    @Autowired
    private GroupeService groupeService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.GroupeSearchRepositoryMockConfiguration
     */
    @Autowired
    private GroupeSearchRepository mockGroupeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupeMockMvc;

    private Groupe groupe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Groupe createEntity(EntityManager em) {
        Groupe groupe = new Groupe()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .periodicite(DEFAULT_PERIODICITE)
            .status(DEFAULT_STATUS);
        return groupe;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Groupe createUpdatedEntity(EntityManager em) {
        Groupe groupe = new Groupe()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .periodicite(UPDATED_PERIODICITE)
            .status(UPDATED_STATUS);
        return groupe;
    }

    @BeforeEach
    public void initTest() {
        groupe = createEntity(em);
    }

    @Test
    @Transactional
    public void createGroupe() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();
        // Create the Groupe
        GroupeDTO groupeDTO = groupeMapper.toDto(groupe);
        restGroupeMockMvc.perform(post("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isCreated());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate + 1);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testGroupe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testGroupe.getPeriodicite()).isEqualTo(DEFAULT_PERIODICITE);
        assertThat(testGroupe.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Groupe in Elasticsearch
        verify(mockGroupeSearchRepository, times(1)).save(testGroupe);
    }

    @Test
    @Transactional
    public void createGroupeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();

        // Create the Groupe with an existing ID
        groupe.setId(1L);
        GroupeDTO groupeDTO = groupeMapper.toDto(groupe);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupeMockMvc.perform(post("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Groupe in Elasticsearch
        verify(mockGroupeSearchRepository, times(0)).save(groupe);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupeRepository.findAll().size();
        // set the field null
        groupe.setNom(null);

        // Create the Groupe, which fails.
        GroupeDTO groupeDTO = groupeMapper.toDto(groupe);


        restGroupeMockMvc.perform(post("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isBadRequest());

        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupeRepository.findAll().size();
        // set the field null
        groupe.setStatus(null);

        // Create the Groupe, which fails.
        GroupeDTO groupeDTO = groupeMapper.toDto(groupe);


        restGroupeMockMvc.perform(post("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isBadRequest());

        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGroupes() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get all the groupeList
        restGroupeMockMvc.perform(get("/api/groupes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].periodicite").value(hasItem(DEFAULT_PERIODICITE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", groupe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groupe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.periodicite").value(DEFAULT_PERIODICITE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingGroupe() throws Exception {
        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // Update the groupe
        Groupe updatedGroupe = groupeRepository.findById(groupe.getId()).get();
        // Disconnect from session so that the updates on updatedGroupe are not directly saved in db
        em.detach(updatedGroupe);
        updatedGroupe
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .periodicite(UPDATED_PERIODICITE)
            .status(UPDATED_STATUS);
        GroupeDTO groupeDTO = groupeMapper.toDto(updatedGroupe);

        restGroupeMockMvc.perform(put("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isOk());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testGroupe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGroupe.getPeriodicite()).isEqualTo(UPDATED_PERIODICITE);
        assertThat(testGroupe.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Groupe in Elasticsearch
        verify(mockGroupeSearchRepository, times(1)).save(testGroupe);
    }

    @Test
    @Transactional
    public void updateNonExistingGroupe() throws Exception {
        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // Create the Groupe
        GroupeDTO groupeDTO = groupeMapper.toDto(groupe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupeMockMvc.perform(put("/api/groupes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Groupe in Elasticsearch
        verify(mockGroupeSearchRepository, times(0)).save(groupe);
    }

    @Test
    @Transactional
    public void deleteGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        int databaseSizeBeforeDelete = groupeRepository.findAll().size();

        // Delete the groupe
        restGroupeMockMvc.perform(delete("/api/groupes/{id}", groupe.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Groupe in Elasticsearch
        verify(mockGroupeSearchRepository, times(1)).deleteById(groupe.getId());
    }

    @Test
    @Transactional
    public void searchGroupe() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);
        when(mockGroupeSearchRepository.search(queryStringQuery("id:" + groupe.getId())))
            .thenReturn(Collections.singletonList(groupe));

        // Search the groupe
        restGroupeMockMvc.perform(get("/api/_search/groupes?query=id:" + groupe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].periodicite").value(hasItem(DEFAULT_PERIODICITE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
