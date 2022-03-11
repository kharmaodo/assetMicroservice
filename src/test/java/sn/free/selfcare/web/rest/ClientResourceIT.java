package sn.free.selfcare.web.rest;

import sn.free.selfcare.AssetMicroserviceApp;
import sn.free.selfcare.config.SecurityBeanOverrideConfiguration;
import sn.free.selfcare.domain.Client;
import sn.free.selfcare.repository.ClientRepository;
import sn.free.selfcare.repository.search.ClientSearchRepository;
import sn.free.selfcare.service.ClientService;
import sn.free.selfcare.service.dto.ClientDTO;
import sn.free.selfcare.service.mapper.ClientMapper;

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
 * Integration tests for the {@link ClientResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, AssetMicroserviceApp.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ClientResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_RAISON_SOCIALE = "AAAAAAAAAA";
    private static final String UPDATED_RAISON_SOCIALE = "BBBBBBBBBB";

    private static final String DEFAULT_NINEA = "AAAAAAAAAA";
    private static final String UPDATED_NINEA = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTRE_COMM = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRE_COMM = "BBBBBBBBBB";

    private static final String DEFAULT_NUMERO_VIRTUEL = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_VIRTUEL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_GESTIONNAIRE = "AAAAAAAAAA";
    private static final String UPDATED_GESTIONNAIRE = "BBBBBBBBBB";

    private static final ObjectStatus DEFAULT_STATUS = ObjectStatus.ACTIVE;
    private static final ObjectStatus UPDATED_STATUS = ObjectStatus.INACTIVE;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ClientService clientService;

    /**
     * This repository is mocked in the sn.free.selfcare.repository.search test package.
     *
     * @see sn.free.selfcare.repository.search.ClientSearchRepositoryMockConfiguration
     */
    @Autowired
    private ClientSearchRepository mockClientSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity(EntityManager em) {
        Client client = new Client()
            .code(DEFAULT_CODE)
            .raisonSociale(DEFAULT_RAISON_SOCIALE)
            .ninea(DEFAULT_NINEA)
            .registreComm(DEFAULT_REGISTRE_COMM)
            .numeroVirtuel(DEFAULT_NUMERO_VIRTUEL)
            .telephone(DEFAULT_TELEPHONE)
            .email(DEFAULT_EMAIL)
            .adresse(DEFAULT_ADRESSE)
            .ville(DEFAULT_VILLE)
            .gestionnaire(DEFAULT_GESTIONNAIRE)
            .status(DEFAULT_STATUS);
        return client;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity(EntityManager em) {
        Client client = new Client()
            .code(UPDATED_CODE)
            .raisonSociale(UPDATED_RAISON_SOCIALE)
            .ninea(UPDATED_NINEA)
            .registreComm(UPDATED_REGISTRE_COMM)
            .numeroVirtuel(UPDATED_NUMERO_VIRTUEL)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .gestionnaire(UPDATED_GESTIONNAIRE)
            .status(UPDATED_STATUS);
        return client;
    }

    @BeforeEach
    public void initTest() {
        client = createEntity(em);
    }

    @Test
    @Transactional
    public void createClient() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();
        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);
        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isCreated());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate + 1);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testClient.getRaisonSociale()).isEqualTo(DEFAULT_RAISON_SOCIALE);
        assertThat(testClient.getNinea()).isEqualTo(DEFAULT_NINEA);
        assertThat(testClient.getRegistreComm()).isEqualTo(DEFAULT_REGISTRE_COMM);
        assertThat(testClient.getNumeroVirtuel()).isEqualTo(DEFAULT_NUMERO_VIRTUEL);
        assertThat(testClient.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testClient.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testClient.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testClient.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testClient.getGestionnaire()).isEqualTo(DEFAULT_GESTIONNAIRE);
        assertThat(testClient.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Client in Elasticsearch
        verify(mockClientSearchRepository, times(1)).save(testClient);
    }

    @Test
    @Transactional
    public void createClientWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();

        // Create the Client with an existing ID
        client.setId(1L);
        ClientDTO clientDTO = clientMapper.toDto(client);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate);

        // Validate the Client in Elasticsearch
        verify(mockClientSearchRepository, times(0)).save(client);
    }


    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setCode(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);


        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRaisonSocialeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setRaisonSociale(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);


        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNineaIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setNinea(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);


        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNumeroVirtuelIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setNumeroVirtuel(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);


        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setStatus(null);

        // Create the Client, which fails.
        ClientDTO clientDTO = clientMapper.toDto(client);


        restClientMockMvc.perform(post("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClients() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc.perform(get("/api/clients?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].raisonSociale").value(hasItem(DEFAULT_RAISON_SOCIALE)))
            .andExpect(jsonPath("$.[*].ninea").value(hasItem(DEFAULT_NINEA)))
            .andExpect(jsonPath("$.[*].registreComm").value(hasItem(DEFAULT_REGISTRE_COMM)))
            .andExpect(jsonPath("$.[*].numeroVirtuel").value(hasItem(DEFAULT_NUMERO_VIRTUEL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].gestionnaire").value(hasItem(DEFAULT_GESTIONNAIRE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc.perform(get("/api/clients/{id}", client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.raisonSociale").value(DEFAULT_RAISON_SOCIALE))
            .andExpect(jsonPath("$.ninea").value(DEFAULT_NINEA))
            .andExpect(jsonPath("$.registreComm").value(DEFAULT_REGISTRE_COMM))
            .andExpect(jsonPath("$.numeroVirtuel").value(DEFAULT_NUMERO_VIRTUEL))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.gestionnaire").value(DEFAULT_GESTIONNAIRE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get("/api/clients/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).get();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .code(UPDATED_CODE)
            .raisonSociale(UPDATED_RAISON_SOCIALE)
            .ninea(UPDATED_NINEA)
            .registreComm(UPDATED_REGISTRE_COMM)
            .numeroVirtuel(UPDATED_NUMERO_VIRTUEL)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .gestionnaire(UPDATED_GESTIONNAIRE)
            .status(UPDATED_STATUS);
        ClientDTO clientDTO = clientMapper.toDto(updatedClient);

        restClientMockMvc.perform(put("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testClient.getRaisonSociale()).isEqualTo(UPDATED_RAISON_SOCIALE);
        assertThat(testClient.getNinea()).isEqualTo(UPDATED_NINEA);
        assertThat(testClient.getRegistreComm()).isEqualTo(UPDATED_REGISTRE_COMM);
        assertThat(testClient.getNumeroVirtuel()).isEqualTo(UPDATED_NUMERO_VIRTUEL);
        assertThat(testClient.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testClient.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testClient.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testClient.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testClient.getGestionnaire()).isEqualTo(UPDATED_GESTIONNAIRE);
        assertThat(testClient.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Client in Elasticsearch
        verify(mockClientSearchRepository, times(1)).save(testClient);
    }

    @Test
    @Transactional
    public void updateNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc.perform(put("/api/clients").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(clientDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Client in Elasticsearch
        verify(mockClientSearchRepository, times(0)).save(client);
    }

    @Test
    @Transactional
    public void deleteClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeDelete = clientRepository.findAll().size();

        // Delete the client
        restClientMockMvc.perform(delete("/api/clients/{id}", client.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Client in Elasticsearch
        verify(mockClientSearchRepository, times(1)).deleteById(client.getId());
    }

    @Test
    @Transactional
    public void searchClient() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        clientRepository.saveAndFlush(client);
        when(mockClientSearchRepository.search(queryStringQuery("id:" + client.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(client), PageRequest.of(0, 1), 1));

        // Search the client
        restClientMockMvc.perform(get("/api/_search/clients?query=id:" + client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].raisonSociale").value(hasItem(DEFAULT_RAISON_SOCIALE)))
            .andExpect(jsonPath("$.[*].ninea").value(hasItem(DEFAULT_NINEA)))
            .andExpect(jsonPath("$.[*].registreComm").value(hasItem(DEFAULT_REGISTRE_COMM)))
            .andExpect(jsonPath("$.[*].numeroVirtuel").value(hasItem(DEFAULT_NUMERO_VIRTUEL)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].gestionnaire").value(hasItem(DEFAULT_GESTIONNAIRE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
