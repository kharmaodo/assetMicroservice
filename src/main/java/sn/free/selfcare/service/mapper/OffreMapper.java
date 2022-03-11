package sn.free.selfcare.service.mapper;

import java.util.Set;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import sn.free.selfcare.domain.Offre;
import sn.free.selfcare.service.dto.OffreDTO;

/**
 * Mapper for the entity {@link Offre} and its DTO {@link OffreDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClientMapper.class, ProduitMapper.class, GrilleTarifaireMapper.class, LigneMapper.class })
public interface OffreMapper extends EntityMapper<OffreDTO, Offre> {

    @Mapping(source = "grilleTarifaire.id", target = "grilleTarifaireId")
    OffreDTO toDto(Offre offre);

    // @Mapping(target = "lignes", ignore = true)
    @Mapping(target = "removeLignes", ignore = true)
    @Mapping(target = "removeClients", ignore = true)
    @Mapping(target = "removeProduits", ignore = true)
    // @Mapping(source = "grilleTarifaireId", target = "grilleTarifaire")
    Offre toEntity(OffreDTO offreDTO);

    default Offre fromId(Long id) {
        if (id == null) {
            return null;
        }
        Offre offre = new Offre();
        offre.setId(id);
        return offre;
    }

    @IterableMapping(qualifiedByName = "mapWithoutClients")
	Set<OffreDTO> offresToOffreDTOs(Set<Offre> offres);

	Set<Offre> offreDTOsToOffres(Set<OffreDTO> offreDTOs);

	@Named("mapWithoutClients")
	@Mapping(target = "clients", ignore = true)
	OffreDTO mapWithoutClients(Offre source);
}
