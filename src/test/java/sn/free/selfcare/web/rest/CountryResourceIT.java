package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Country;
import sn.free.selfcare.repository.CountryRepository;
import sn.free.selfcare.repository.search.CountrySearchRepository;
import sn.free.selfcare.service.CountryService;
import sn.free.selfcare.service.dto.CountryDTO;
import sn.free.selfcare.service.mapper.CountryMapper;

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
 * Integration tests for the {@link CountryResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CountryResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_INDICATIF = "AAAAAAAAAA";
    private static final String UPDATED_INDICATIF = "BBBBBBBBBB";

    private static final String DEFAULT_DEVISE = "AAAAAAAAAA";
    private static final String UPDATED_DEVISE = "BBBBBBBBBB";

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private CountryService countryService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.CountrySearchRepositoryMockConfiguration
     */
    @Autowired
    private CountrySearchRepository mockCountrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCountryMockMvc;

    private Country country;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createEntity(EntityManager em) {
        Country country = new Country()
            .nom(DEFAULT_NOM)
            .code(DEFAULT_CODE)
            .indicatif(DEFAULT_INDICATIF)
            .devise(DEFAULT_DEVISE);
        return country;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createUpdatedEntity(EntityManager em) {
        Country country = new Country()
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .indicatif(UPDATED_INDICATIF)
            .devise(UPDATED_DEVISE);
        return country;
    }

    @BeforeEach
    public void initTest() {
        country = createEntity(em);
    }

    @Test
    @Transactional
    public void createCountry() throws Exception {
        int databaseSizeBeforeCreate = countryRepository.findAll().size();
        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);
        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isCreated());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeCreate + 1);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testCountry.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCountry.getIndicatif()).isEqualTo(DEFAULT_INDICATIF);
        assertThat(testCountry.getDevise()).isEqualTo(DEFAULT_DEVISE);

        // Validate the Country in Elasticsearch
        verify(mockCountrySearchRepository, times(1)).save(testCountry);
    }

    @Test
    @Transactional
    public void createCountryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = countryRepository.findAll().size();

        // Create the Country with an existing ID
        country.setId(1L);
        CountryDTO countryDTO = countryMapper.toDto(country);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeCreate);

        // Validate the Country in Elasticsearch
        verify(mockCountrySearchRepository, times(0)).save(country);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = countryRepository.findAll().size();
        // set the field null
        country.setNom(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);


        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = countryRepository.findAll().size();
        // set the field null
        country.setCode(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);


        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIndicatifIsRequired() throws Exception {
        int databaseSizeBeforeTest = countryRepository.findAll().size();
        // set the field null
        country.setIndicatif(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);


        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDeviseIsRequired() throws Exception {
        int databaseSizeBeforeTest = countryRepository.findAll().size();
        // set the field null
        country.setDevise(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);


        restCountryMockMvc.perform(post("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCountries() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get all the countryList
        restCountryMockMvc.perform(get("/api/countries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].indicatif").value(hasItem(DEFAULT_INDICATIF)))
            .andExpect(jsonPath("$.[*].devise").value(hasItem(DEFAULT_DEVISE)));
    }
    
    @Test
    @Transactional
    public void getCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        // Get the country
        restCountryMockMvc.perform(get("/api/countries/{id}", country.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(country.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.indicatif").value(DEFAULT_INDICATIF))
            .andExpect(jsonPath("$.devise").value(DEFAULT_DEVISE));
    }
    @Test
    @Transactional
    public void getNonExistingCountry() throws Exception {
        // Get the country
        restCountryMockMvc.perform(get("/api/countries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeUpdate = countryRepository.findAll().size();

        // Update the country
        Country updatedCountry = countryRepository.findById(country.getId()).get();
        // Disconnect from session so that the updates on updatedCountry are not directly saved in db
        em.detach(updatedCountry);
        updatedCountry
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .indicatif(UPDATED_INDICATIF)
            .devise(UPDATED_DEVISE);
        CountryDTO countryDTO = countryMapper.toDto(updatedCountry);

        restCountryMockMvc.perform(put("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isOk());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);
        Country testCountry = countryList.get(countryList.size() - 1);
        assertThat(testCountry.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testCountry.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCountry.getIndicatif()).isEqualTo(UPDATED_INDICATIF);
        assertThat(testCountry.getDevise()).isEqualTo(UPDATED_DEVISE);

        // Validate the Country in Elasticsearch
        verify(mockCountrySearchRepository, times(1)).save(testCountry);
    }

    @Test
    @Transactional
    public void updateNonExistingCountry() throws Exception {
        int databaseSizeBeforeUpdate = countryRepository.findAll().size();

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCountryMockMvc.perform(put("/api/countries").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(countryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Country in the database
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Country in Elasticsearch
        verify(mockCountrySearchRepository, times(0)).save(country);
    }

    @Test
    @Transactional
    public void deleteCountry() throws Exception {
        // Initialize the database
        countryRepository.saveAndFlush(country);

        int databaseSizeBeforeDelete = countryRepository.findAll().size();

        // Delete the country
        restCountryMockMvc.perform(delete("/api/countries/{id}", country.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Country in Elasticsearch
        verify(mockCountrySearchRepository, times(1)).deleteById(country.getId());
    }

    @Test
    @Transactional
    public void searchCountry() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        countryRepository.saveAndFlush(country);
        when(mockCountrySearchRepository.search(queryStringQuery("id:" + country.getId())))
            .thenReturn(Collections.singletonList(country));

        // Search the country
        restCountryMockMvc.perform(get("/api/_search/countries?query=id:" + country.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(country.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].indicatif").value(hasItem(DEFAULT_INDICATIF)))
            .andExpect(jsonPath("$.[*].devise").value(hasItem(DEFAULT_DEVISE)));
    }
}
