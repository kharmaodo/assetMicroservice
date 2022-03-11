package sn.free.selfcare.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.free.selfcare.client.OperationFeignClient;
import sn.free.selfcare.domain.Groupe;
import sn.free.selfcare.domain.SelfcareParameter;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.helper.DateHelper;
import sn.free.selfcare.repository.GroupeRepository;
import sn.free.selfcare.repository.search.GroupeSearchRepository;
import sn.free.selfcare.service.GroupeService;
import sn.free.selfcare.service.SelfcareParameterService;
import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.dto.operation.GroupeAdjustmentDTO;
import sn.free.selfcare.service.dto.operation.PeriodeEnvoiDTO;
import sn.free.selfcare.service.mapper.GroupeAdjustmentMapper;
import sn.free.selfcare.service.mapper.GroupeMapper;
import sn.free.selfcare.web.rest.errors.GroupeHasEmployeException;

/**
 * Service Implementation for managing {@link Groupe}.
 */
@Service
@Transactional
public class GroupeServiceImpl implements GroupeService {

	private final Logger log = LoggerFactory.getLogger(GroupeServiceImpl.class);

	private final GroupeRepository groupeRepository;

	private final GroupeMapper groupeMapper;

	private final GroupeAdjustmentMapper groupeAdjustmentMapper;

	private final GroupeSearchRepository groupeSearchRepository;

	@Autowired
	SelfcareParameterService selfcareParameterService;

	@Autowired
	private OperationFeignClient operationClient;

	public GroupeServiceImpl(GroupeRepository groupeRepository, GroupeMapper groupeMapper,
			GroupeAdjustmentMapper groupeAdjustmentMapper, GroupeSearchRepository groupeSearchRepository) {
		this.groupeRepository = groupeRepository;
		this.groupeMapper = groupeMapper;
		this.groupeAdjustmentMapper = groupeAdjustmentMapper;
		this.groupeSearchRepository = groupeSearchRepository;
	}

	@Override
	public GroupeDTO save(GroupeDTO groupeDTO) {
		log.debug("Request to save Groupe : {}", groupeDTO);
		Groupe groupe = groupeMapper.toEntity(groupeDTO);
		groupe = groupeRepository.save(groupe);
		GroupeDTO result = groupeMapper.toDto(groupe);
		this.savePeriodeForGroupe(result);
		groupeSearchRepository.save(groupe);
		return result;
	}

	private void savePeriodeForGroupe(GroupeDTO groupe) {
		log.debug("Request to save Periode for groupe : {}", groupe);
		if (groupe.getPeriodicite() != null) {
			PeriodeEnvoiDTO periodeEnvoi = new PeriodeEnvoiDTO();
			periodeEnvoi.setGroupeId(groupe.getId());
			periodeEnvoi.setStatus(groupe.getStatus());

			String mensuelCron = "0 30 7 1 * ?";
			String hebdoCron = "0 30 7 ? * MON";
			String dailyCron = "0 30 7 ? * *";

			List<ParameterOption> options = new ArrayList<ParameterOption>();
			options.add(ParameterOption.PERIODE_ENVOI_RESSOURCE_MENSUEL);
			options.add(ParameterOption.PERIODE_ENVOI_RESSOURCE_HEBDOMADAIRE);
			options.add(ParameterOption.PERIODE_ENVOI_RESSOURCE_QUOTIDIEN);
			List<SelfcareParameter> selfcareParameters = selfcareParameterService.findParameterOptions(options);

			for (SelfcareParameter selfcareParameter : selfcareParameters) {
				String valeur = selfcareParameter.getValeur();
				ParameterOption option = selfcareParameter.getNom();
				if (valeur != null && !valeur.isEmpty()) {
					if (ParameterOption.PERIODE_ENVOI_RESSOURCE_MENSUEL.equals(option)) {
						mensuelCron = valeur;
					}
					if (ParameterOption.PERIODE_ENVOI_RESSOURCE_HEBDOMADAIRE.equals(option)) {
						hebdoCron = valeur;
					}
					if (ParameterOption.PERIODE_ENVOI_RESSOURCE_QUOTIDIEN.equals(option)) {
						dailyCron = valeur;
					}
				}
			}

			switch (groupe.getPeriodicite()) {
				case MENSUELLE:
					periodeEnvoi.setExpression(mensuelCron);
					break;
				case HEBDOMADAIRE:
					periodeEnvoi.setExpression(hebdoCron);
					break;
				case QUOTIDIEN:
					periodeEnvoi.setExpression(dailyCron);
					break;

			}
			operationClient.createPeriodeEnvoi(periodeEnvoi);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupeDTO> findAll() {
		log.debug("Request to get all Groupes");
		return groupeRepository.findAll().stream().map(groupeMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GroupeDTO> findOne(Long id) {
		log.debug("Request to get Groupe : {}", id);
		return groupeRepository.findById(id).map(groupeMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GroupeAdjustmentDTO> findOneForAdjustment(Long id) {
		log.debug("Request to get Groupe for adjustment: {}", id);
		return groupeRepository.findById(id).map(groupeAdjustmentMapper::toDto);
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete Groupe : {}", id);
        Optional<Groupe> optionalGroupe = groupeRepository.findById(id);
        if (optionalGroupe.isPresent()) {
            Groupe groupe = optionalGroupe.get();

            // Vérifier si le groupe comporte des employés.
            if (groupe.getEmployes().size() > 0) {
                throw new GroupeHasEmployeException();
            }

            // Concaténer l'instant actuel au nom de groupe
            groupe.setNom(DateHelper.formatWithCurrentDateTime(groupe.getNom()));

            // Procéder à la suppression logique du groupe dans le cas échéant.
            GroupeDTO groupeDTO = groupeMapper.toDto(groupe);
            groupeDTO.setStatus(ObjectStatus.ARCHIVED);
            save(groupeDTO);
        }
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupeDTO> search(String query) {
		log.debug("Request to search Groupes for query {}", query);
		return StreamSupport.stream(groupeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
				.map(groupeMapper::toDto).collect(Collectors.toList());
	}

	@Override
    public Page<GroupeDTO> findGroupesByClientAndQuery(Long clientId, String query, Pageable pageable) {

        Page<Groupe> page;
        if (query != null && !query.isEmpty()) {
            page = groupeRepository.findGroupesByClientAndQuery(clientId, ObjectStatus.ARCHIVED, "%" + query + "%", pageable);
        } else {
            page = groupeRepository.findGroupesByClient(clientId, ObjectStatus.ARCHIVED, pageable);
        }
        return page.map(groupeMapper::toDto);
    }

	@Override
	public List<GroupeDTO> findAllByNom(String nom, boolean onlyActive) {
		log.debug("Request to get all groupes : {} ", nom);
		if (onlyActive) {
	        return StreamSupport
	                .stream(groupeRepository.findByNomIgnoreCase(nom).spliterator(), false)
	                .filter(groupe -> groupe.getStatus() != null && groupe.getStatus().equals(ObjectStatus.ACTIVE))
	                .map(groupeMapper::toDto)
	                .collect(Collectors.toCollection(LinkedList::new));
		}

		return StreamSupport
                .stream(groupeRepository.findByNomIgnoreCase(nom).spliterator(), false)
                .map(groupeMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
	}

    @Override
    @Transactional(readOnly = true)
    public List<GroupeDTO> findGroupesByClient(Long clientId) {
        log.debug("Request to find Groupes by client: {}", clientId);
        return groupeRepository.findGroupesByClientAndStatus(clientId, ObjectStatus.ACTIVE).stream().map(groupeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GroupeDTO> findByClientAndNom(Long clientId, String nom) {
        log.debug("Request to get Groupe : {} for client : {}", nom, clientId);
        return groupeRepository.findByClientAndNom(clientId, nom)
            .map(groupeMapper::toDto);
    }
}
