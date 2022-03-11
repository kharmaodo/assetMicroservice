package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.free.selfcare.domain.Client;
import sn.free.selfcare.service.dto.ClientDTO;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring", uses = { OffreMapper.class, CountryMapper.class, GroupeMapper.class,
		EmployeMapper.class })
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {

    ClientDTO toDto(Client client);

    @Mapping(target = "lignes", ignore = true)
    @Mapping(target = "removeLignes", ignore = true)
    @Mapping(target = "groupes", ignore = true)
    @Mapping(target = "removeGroupes", ignore = true)
    @Mapping(target = "employes", ignore = true)
    @Mapping(target = "removeEmployes", ignore = true)
    @Mapping(target = "produitsPersonnalises", ignore = true)
    @Mapping(target = "removeProduitsPersonnalises", ignore = true)
    @Mapping(target = "removeOffres", ignore = true)
    Client toEntity(ClientDTO clientDTO);

    default Client fromId(Long id) {
        if (id == null) {
            return null;
        }
        Client client = new Client();
        client.setId(id);
        return client;
    }
}
