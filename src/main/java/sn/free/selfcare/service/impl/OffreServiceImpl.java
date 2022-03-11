package sn.free.selfcare.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.free.selfcare.client.OperationFeignClient;
import sn.free.selfcare.domain.Client;
import sn.free.selfcare.domain.GrilleTarifaire;
import sn.free.selfcare.domain.Ligne;
import sn.free.selfcare.domain.Offre;
import sn.free.selfcare.domain.Produit;
import sn.free.selfcare.domain.enumeration.ModePaiement;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.repository.OffreRepository;
import sn.free.selfcare.repository.search.OffreSearchRepository;
import sn.free.selfcare.service.GrilleTarifaireService;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.OffreService;
import sn.free.selfcare.service.dto.LigneDTO;
import sn.free.selfcare.service.dto.OffreDTO;
import sn.free.selfcare.service.dto.operation.SimpleAdjustOrderDTO;
import sn.free.selfcare.service.mapper.OffreMapper;
import sn.free.selfcare.web.rest.errors.OffreHasLigneException;
import sn.free.selfcare.web.rest.errors.OffreHasProduitException;

/**
 * Service Implementation for managing {@link Offre}.
 */
@Service
@Transactional
public class OffreServiceImpl implements OffreService {

	private final Logger log = LoggerFactory.getLogger(OffreServiceImpl.class);

	private final OffreRepository offreRepository;

	private final OffreMapper offreMapper;

	private final OffreSearchRepository offreSearchRepository;

	@Autowired
	private GrilleTarifaireService grilleTarifaireService;

	@Autowired
	private LigneService ligneService;

	@Autowired
	private OperationFeignClient operationClient;

	public OffreServiceImpl(OffreRepository offreRepository, OffreMapper offreMapper,
			OffreSearchRepository offreSearchRepository) {
		this.offreRepository = offreRepository;
		this.offreMapper = offreMapper;
		this.offreSearchRepository = offreSearchRepository;
	}

	@Override
	public OffreDTO save(OffreDTO offreDTO) {
		log.debug("Request to save Offre : {}", offreDTO);
		boolean creation = false;
		Double montantInitial = (double) 0;
		if (offreDTO.getId() == null) {
			creation = true;
			if (offreDTO.getGrilleTarifaire().getId() == null) {
				offreDTO.setGrilleTarifaire(this.grilleTarifaireService.save(offreDTO.getGrilleTarifaire()));
			}
			if (offreDTO.getModePaiement() == ModePaiement.PREPAID) {
				montantInitial = offreDTO.getMontant();
				offreDTO.setMontant((double) 0);
			}
		} else if (offreDTO.getGrilleTarifaire() != null && offreDTO.getGrilleTarifaire().getId() != null) { // update grille tarifaire
			offreDTO.setGrilleTarifaire(this.grilleTarifaireService.save(offreDTO.getGrilleTarifaire()));
		}

		Offre offre = offreMapper.toEntity(offreDTO);
		offre = offreRepository.save(offre);

		final Set<Client> clients = offre.getClients();
		Client client = null;
		if (clients.iterator().hasNext()) {
			client = clients.iterator().next();
		}
		final Long clientId = client == null ? null : client.getId();
		final Long offreId = offre.getId();
		// reset all lines linked to this offre
		List<LigneDTO> offreLines = this.ligneService.findAllByOffreId(offreId);
		offreLines.forEach(ligne -> {
			ligne.setClientId(clientId);
			ligne.setOffreId(null);
			this.ligneService.save(ligne);
		});
		// link selected lines to this offre
		Set<LigneDTO> lignes = offreDTO.getLignes();
		lignes.forEach(ligne -> {
			ligne.setClientId(clientId);
			ligne.setOffreId(offreId);
			this.ligneService.save(ligne);
		});

		if (creation && offre.getModePaiement() == ModePaiement.PREPAID) {
			SimpleAdjustOrderDTO order = new SimpleAdjustOrderDTO();
			String indicatif = client.getCountry() != null ? client.getCountry().getIndicatif() : "221";
			order.setNumeroClient(indicatif.concat(client.getNumeroVirtuel()));
			order.setStock(montantInitial);
			this.operationClient.simpleAdjustAccount(order);
		}

		OffreDTO result = offreMapper.toDto(offre);
		offreSearchRepository.save(offre);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OffreDTO> findAll() {
		log.debug("Request to get all Offres");
		return offreRepository.findAll().stream().map(offreMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public Page<OffreDTO> findAllWithEagerRelationships(Pageable pageable) {
		return offreRepository.findAllWithEagerRelationships(pageable).map(offreMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OffreDTO> findOne(Long id) {
		log.debug("Request to get Offre : {}", id);
		return offreRepository.findById(id).map(offreMapper::toDto);
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete Offre : {}", id);
		Optional<Offre> optionalOffre = offreRepository.findById(id);
		if (optionalOffre.isPresent()) {
			Offre offre = optionalOffre.get();

			// Vérifier si l'offre comporte des lignes et produits
			if (offre.getLignes().size() > 0) {
				throw new OffreHasLigneException();
			}
			if (offre.getProduits().size() > 0) {
				throw new OffreHasProduitException();
			}

			// Procéder à la suppression logique le cas échéant.
			offre.setStatus(ObjectStatus.ARCHIVED);
			offre = offreRepository.save(offre);
			offreSearchRepository.save(offre);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OffreDTO> search(String query) {
		log.debug("Request to search Offres for query {}", query);
		return StreamSupport.stream(offreSearchRepository.search(queryStringQuery(query)).spliterator(), false)
				.map(offreMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public byte[] export(Long clientId) throws IOException {
		byte[] offresData = null;
		List<Offre> offres;
		if (clientId != null) {
			log.debug("REST request to get all Offres by client : {}", clientId);
			offres = offreRepository.findAllByClientId(clientId);
		} else {
			log.debug("REST request to get all Offres");
			offres = offreRepository.findAll();
		}

		Workbook workbook = new XSSFWorkbook();
		Sheet offresSheet = workbook.createSheet("Offres");
		Row rowHeaderOffre = offresSheet.createRow(0);
		Cell cellOffre0 = rowHeaderOffre.createCell(0);
		cellOffre0.setCellValue("Id");
		Cell cellOffre1 = rowHeaderOffre.createCell(1);
		cellOffre1.setCellValue("Code");
		Cell cellOffre2 = rowHeaderOffre.createCell(2);
		cellOffre2.setCellValue("Type");
		Cell cellOffre3 = rowHeaderOffre.createCell(3);
		cellOffre3.setCellValue("Mode de paiement");
		Cell cellOffre4 = rowHeaderOffre.createCell(4);
		cellOffre4.setCellValue("Montant");
		Cell cellOffre5 = rowHeaderOffre.createCell(5);
		cellOffre5.setCellValue("Durée d'engagement");

		Sheet tarifSheet = workbook.createSheet("Tarif");
		Row rowHeaderTarif = tarifSheet.createRow(0);
		Cell cellTarif0 = rowHeaderTarif.createCell(0);
		cellTarif0.setCellValue("Id offre");
		Cell cellTarif1 = rowHeaderTarif.createCell(1);
		cellTarif1.setCellValue("Code offre");
		Cell cellTarif2 = rowHeaderTarif.createCell(2);
		cellTarif2.setCellValue("Type tarification");
		Cell cellTarif3 = rowHeaderTarif.createCell(3);
		cellTarif3.setCellValue("SMS");
		Cell cellTarif4 = rowHeaderTarif.createCell(4);
		cellTarif4.setCellValue("Minutes d'appel");
		Cell cellTarif5 = rowHeaderTarif.createCell(5);
		cellTarif5.setCellValue("Go de données");

		Sheet produitsSheet = workbook.createSheet("Produits");
		Row rowHeaderProduit = produitsSheet.createRow(0);
		Cell cellProduit0 = rowHeaderProduit.createCell(0);
		cellProduit0.setCellValue("Id offre");
		Cell cellProduit1 = rowHeaderProduit.createCell(1);
		cellProduit1.setCellValue("Code offre");
		Cell cellProduit2 = rowHeaderProduit.createCell(2);
		cellProduit2.setCellValue("Nom");
		Cell cellProduit3 = rowHeaderProduit.createCell(3);
		cellProduit3.setCellValue("Description");
		Cell cellProduit4 = rowHeaderProduit.createCell(4);
		cellProduit4.setCellValue("Type produit");
		Cell cellProduit5 = rowHeaderProduit.createCell(5);
		cellProduit5.setCellValue("Crédit");
		Cell cellProduit6 = rowHeaderProduit.createCell(6);
		cellProduit6.setCellValue("SMS");
		Cell cellProduit7 = rowHeaderProduit.createCell(7);
		cellProduit7.setCellValue("Minutes d'appel");
		Cell cellProduit8 = rowHeaderProduit.createCell(8);
		cellProduit8.setCellValue("Go de données");

		Sheet lignesSheet = workbook.createSheet("Lignes");
		Row rowHeaderLigne = lignesSheet.createRow(0);
		Cell cellLigne0 = rowHeaderLigne.createCell(0);
		cellLigne0.setCellValue("Id offre");
		Cell cellLigne1 = rowHeaderLigne.createCell(1);
		cellLigne1.setCellValue("Code offre");
		Cell cellLigne2 = rowHeaderLigne.createCell(2);
		cellLigne2.setCellValue("Numéro de téléphone");
		Cell cellLigne3 = rowHeaderLigne.createCell(3);
		cellLigne3.setCellValue("IMSI");

		int rowIndexOffre = 1;
		int rowIndexTarif = 1;
		int rowIndexProduit = 1;
		int rowIndexLigne = 1;
		for (Offre offre : offres) {
			String codeOffre = offre.getCode() != null ? offre.getCode() : "";
			Row rowOffre = offresSheet.createRow(rowIndexOffre++);
			cellOffre0 = rowOffre.createCell(0);
			cellOffre0.setCellValue(offre.getId());
			cellOffre1 = rowOffre.createCell(1);
			cellOffre1.setCellValue(codeOffre);
			cellOffre2 = rowOffre.createCell(2);
			cellOffre2.setCellValue(offre.getTypeOffre().toString());
			cellOffre3 = rowOffre.createCell(3);
			cellOffre3.setCellValue(offre.getModePaiement().toString());
			cellOffre4 = rowOffre.createCell(4);
			cellOffre4.setCellValue(offre.getMontant());
			cellOffre5 = rowOffre.createCell(5);
			if (offre.getDureeEngagement() != null) {
				cellOffre5.setCellValue(offre.getDureeEngagement());
			} else {
				cellOffre5.setCellValue("");
			}

			GrilleTarifaire tarif = offre.getGrilleTarifaire();
			if (tarif != null) {
				Row rowTarif = tarifSheet.createRow(rowIndexTarif++);
				cellTarif0 = rowTarif.createCell(0);
				cellTarif0.setCellValue(offre.getId());
				cellTarif1 = rowTarif.createCell(1);
				cellTarif1.setCellValue(codeOffre);
				cellTarif2 = rowTarif.createCell(2);
				cellTarif2.setCellValue(tarif.getTypeTarification().toString());
				cellTarif3 = rowTarif.createCell(3);
				cellTarif3.setCellValue(tarif.getSms());
				cellTarif4 = rowTarif.createCell(4);
				cellTarif4.setCellValue(tarif.getMinAppel());
				cellTarif5 = rowTarif.createCell(5);
				cellTarif5.setCellValue(tarif.getGoData());
			}

			Set<Produit> produits = offre.getProduits();
			for (Produit produit : produits) {
				Row rowProduit = produitsSheet.createRow(rowIndexProduit++);
				cellProduit0 = rowProduit.createCell(0);
				cellProduit0.setCellValue(offre.getId());
				cellProduit1 = rowProduit.createCell(1);
				cellProduit1.setCellValue(codeOffre);
				cellProduit2 = rowProduit.createCell(2);
				cellProduit2.setCellValue(produit.getNom());
				cellProduit3 = rowProduit.createCell(3);
				cellProduit3.setCellValue(produit.getDescription() != null ? produit.getDescription() : "");
				cellProduit4 = rowProduit.createCell(4);
				cellProduit4.setCellValue(produit.getTypeProduit().toString());
				cellProduit5 = rowProduit.createCell(5);
				cellProduit5.setCellValue(produit.getCredit());
				cellProduit6 = rowProduit.createCell(6);
				cellProduit6.setCellValue(produit.getSms());
				cellProduit7 = rowProduit.createCell(7);
				cellProduit7.setCellValue(produit.getMinAppel());
				cellProduit8 = rowProduit.createCell(8);
				cellProduit8.setCellValue(produit.getGoData());
			}

			Set<Ligne> lignes = offre.getLignes();
			for (Ligne ligne : lignes) {
				Row rowLigne = lignesSheet.createRow(rowIndexLigne++);
				cellLigne0 = rowLigne.createCell(0);
				cellLigne0.setCellValue(offre.getId());
				cellLigne1 = rowLigne.createCell(1);
				cellLigne1.setCellValue(codeOffre);
				cellLigne2 = rowLigne.createCell(2);
				cellLigne2.setCellValue(ligne.getNumero());
				cellLigne3 = rowLigne.createCell(3);
				cellLigne3.setCellValue(ligne.getImsi());
			}
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			workbook.write(baos);
			offresData = baos.toByteArray();
			baos.flush();
			workbook.close();
		} catch (IOException e) {
			throw e;
		}

		return offresData;
	}
}
