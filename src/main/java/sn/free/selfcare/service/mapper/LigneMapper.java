package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.LigneDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ligne} and its DTO {@link LigneDTO}.
 */
@Mapper(componentModel = "spring", uses = {ClientMapper.class, OffreMapper.class, ServiceMapper.class})
public interface LigneMapper extends EntityMapper<LigneDTO, Ligne> {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "offre.id", target = "offreId")
    LigneDTO toDto(Ligne ligne);

    @Mapping(target = "employe", ignore = true)
    @Mapping(source = "clientId", target = "client")
    @Mapping(source = "offreId", target = "offre")
    Ligne toEntity(LigneDTO ligneDTO);

    @Mapping(target = "removeServices", ignore = true)

    default Ligne fromId(Long id) {
        if (id == null) {
            return null;
        }
        Ligne ligne = new Ligne();
        ligne.setId(id);
        return ligne;
    }
}
