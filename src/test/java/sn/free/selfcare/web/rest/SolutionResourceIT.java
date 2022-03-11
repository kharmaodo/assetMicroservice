package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Solution;
import sn.free.selfcare.repository.SolutionRepository;
import sn.free.selfcare.repository.search.SolutionSearchRepository;
import sn.free.selfcare.service.SolutionService;
import sn.free.selfcare.service.dto.SolutionDTO;
import sn.free.selfcare.service.mapper.SolutionMapper;

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

/**
 * Integration tests for the {@link SolutionResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class SolutionResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private SolutionMapper solutionMapper;

    @Autowired
    private SolutionService solutionService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.SolutionSearchRepositoryMockConfiguration
     */
    @Autowired
    private SolutionSearchRepository mockSolutionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSolutionMockMvc;

    private Solution solution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Solution createEntity(EntityManager em) {
        Solution solution = new Solution()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION);
        return solution;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Solution createUpdatedEntity(EntityManager em) {
        Solution solution = new Solution()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION);
        return solution;
    }

    @BeforeEach
    public void initTest() {
        solution = createEntity(em);
    }

    @Test
    @Transactional
    public void createSolution() throws Exception {
        int databaseSizeBeforeCreate = solutionRepository.findAll().size();
        // Create the Solution
        SolutionDTO solutionDTO = solutionMapper.toDto(solution);
        restSolutionMockMvc.perform(post("/api/solutions").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(solutionDTO)))
            .andExpect(status().isCreated());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeCreate + 1);
        Solution testSolution = solutionList.get(solutionList.size() - 1);
        assertThat(testSolution.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testSolution.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Solution in Elasticsearch
        verify(mockSolutionSearchRepository, times(1)).save(testSolution);
    }

    @Test
    @Transactional
    public void createSolutionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solutionRepository.findAll().size();

        // Create the Solution with an existing ID
        solution.setId(1L);
        SolutionDTO solutionDTO = solutionMapper.toDto(solution);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolutionMockMvc.perform(post("/api/solutions").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(solutionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Solution in Elasticsearch
        verify(mockSolutionSearchRepository, times(0)).save(solution);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionRepository.findAll().size();
        // set the field null
        solution.setNom(null);

        // Create the Solution, which fails.
        SolutionDTO solutionDTO = solutionMapper.toDto(solution);


        restSolutionMockMvc.perform(post("/api/solutions").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(solutionDTO)))
            .andExpect(status().isBadRequest());

        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSolutions() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        // Get all the solutionList
        restSolutionMockMvc.perform(get("/api/solutions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solution.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        // Get the solution
        restSolutionMockMvc.perform(get("/api/solutions/{id}", solution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(solution.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }
    @Test
    @Transactional
    public void getNonExistingSolution() throws Exception {
        // Get the solution
        restSolutionMockMvc.perform(get("/api/solutions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        int databaseSizeBeforeUpdate = solutionRepository.findAll().size();

        // Update the solution
        Solution updatedSolution = solutionRepository.findById(solution.getId()).get();
        // Disconnect from session so that the updates on updatedSolution are not directly saved in db
        em.detach(updatedSolution);
        updatedSolution
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION);
        SolutionDTO solutionDTO = solutionMapper.toDto(updatedSolution);

        restSolutionMockMvc.perform(put("/api/solutions").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(solutionDTO)))
            .andExpect(status().isOk());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeUpdate);
        Solution testSolution = solutionList.get(solutionList.size() - 1);
        assertThat(testSolution.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testSolution.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Solution in Elasticsearch
        verify(mockSolutionSearchRepository, times(1)).save(testSolution);
    }

    @Test
    @Transactional
    public void updateNonExistingSolution() throws Exception {
        int databaseSizeBeforeUpdate = solutionRepository.findAll().size();

        // Create the Solution
        SolutionDTO solutionDTO = solutionMapper.toDto(solution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolutionMockMvc.perform(put("/api/solutions").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(solutionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Solution in Elasticsearch
        verify(mockSolutionSearchRepository, times(0)).save(solution);
    }

    @Test
    @Transactional
    public void deleteSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        int databaseSizeBeforeDelete = solutionRepository.findAll().size();

        // Delete the solution
        restSolutionMockMvc.perform(delete("/api/solutions/{id}", solution.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Solution in Elasticsearch
        verify(mockSolutionSearchRepository, times(1)).deleteById(solution.getId());
    }

    @Test
    @Transactional
    public void searchSolution() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        solutionRepository.saveAndFlush(solution);
        when(mockSolutionSearchRepository.search(queryStringQuery("id:" + solution.getId())))
            .thenReturn(Collections.singletonList(solution));

        // Search the solution
        restSolutionMockMvc.perform(get("/api/_search/solutions?query=id:" + solution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solution.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
