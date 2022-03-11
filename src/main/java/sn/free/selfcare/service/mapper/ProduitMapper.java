package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.ProduitDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produit} and its DTO {@link ProduitDTO}.
 */
@Mapper(componentModel = "spring", uses = {ClientMapper.class})
public interface ProduitMapper extends EntityMapper<ProduitDTO, Produit> {

    @Mapping(source = "client.id", target = "clientId")
    ProduitDTO toDto(Produit produit);

    @Mapping(target = "groupes", ignore = true)
    @Mapping(target = "removeGroupes", ignore = true)
    @Mapping(source = "clientId", target = "client")
    @Mapping(target = "offres", ignore = true)
    @Mapping(target = "removeOffres", ignore = true)
    Produit toEntity(ProduitDTO produitDTO);

    default Produit fromId(Long id) {
        if (id == null) {
            return null;
        }
        Produit produit = new Produit();
        produit.setId(id);
        return produit;
    }
}
