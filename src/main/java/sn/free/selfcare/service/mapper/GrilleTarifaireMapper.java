package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.GrilleTarifaireDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link GrilleTarifaire} and its DTO {@link GrilleTarifaireDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GrilleTarifaireMapper extends EntityMapper<GrilleTarifaireDTO, GrilleTarifaire> {


    @Mapping(target = "offres", ignore = true)
    @Mapping(target = "removeOffres", ignore = true)
    GrilleTarifaire toEntity(GrilleTarifaireDTO grilleTarifaireDTO);

    default GrilleTarifaire fromId(Long id) {
        if (id == null) {
            return null;
        }
        GrilleTarifaire grilleTarifaire = new GrilleTarifaire();
        grilleTarifaire.setId(id);
        return grilleTarifaire;
    }
}
