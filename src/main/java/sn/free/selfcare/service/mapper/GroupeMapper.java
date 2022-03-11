package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.free.selfcare.domain.Groupe;
import sn.free.selfcare.service.dto.GroupeDTO;

/**
 * Mapper for the entity {@link Groupe} and its DTO {@link GroupeDTO}.
 */
@Mapper(componentModel = "spring", uses = {ClientMapper.class, ProduitMapper.class})
public interface GroupeMapper extends EntityMapper<GroupeDTO, Groupe> {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(target = "employes", ignore = true)
    @Mapping(source = "produit.id", target = "produitId")
    GroupeDTO toDto(Groupe groupe);

    @Mapping(target = "employes", ignore = true)
    @Mapping(target = "removeEmployes", ignore = true)
    @Mapping(source = "clientId", target = "client")
    @Mapping(source = "produitId", target = "produit")
    Groupe toEntity(GroupeDTO groupeDTO);

    default Groupe fromId(Long id) {
        if (id == null) {
            return null;
        }
        Groupe groupe = new Groupe();
        groupe.setId(id);
        return groupe;
    }
}
