package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.free.selfcare.domain.Employe;
import sn.free.selfcare.service.dto.EmployeDTO;

/**
 * Mapper for the entity {@link Employe} and its DTO {@link EmployeDTO}.
 */
@Mapper(componentModel = "spring", uses = {LigneMapper.class, ClientMapper.class, GroupeMapper.class})
public interface EmployeMapper extends EntityMapper<EmployeDTO, Employe> {

    @Mapping(source = "client.id", target = "clientId")
    EmployeDTO toDto(Employe employe);

    @Mapping(source = "ligneId", target = "ligne")
    @Mapping(source = "clientId", target = "client")
    @Mapping(source = "groupeId", target = "groupe")
    Employe toEntity(EmployeDTO employeDTO);

    default Employe fromId(Long id) {
        if (id == null) {
            return null;
        }
        Employe employe = new Employe();
        employe.setId(id);
        return employe;
    }
}
