package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.free.selfcare.domain.Groupe;
import sn.free.selfcare.service.dto.GroupeDTO;
import sn.free.selfcare.service.dto.operation.GroupeAdjustmentDTO;

/**
 * Mapper for the entity {@link Groupe} and its DTO {@link GroupeDTO}.
 */
@Mapper(componentModel = "spring", uses = { EmployeAdjustmentMapper.class })
public interface GroupeAdjustmentMapper extends EntityMapper<GroupeAdjustmentDTO, Groupe> {

	@Mapping(source = "client.id", target = "clientId")
	@Mapping(source = "client.numeroVirtuel", target = "numeroClient")
	@Mapping(source = "produit.credit", target = "credit")
	@Mapping(source = "produit.sms", target = "sms")
	@Mapping(source = "produit.minAppel", target = "voix")
	@Mapping(source = "produit.goData", target = "data")
	GroupeAdjustmentDTO toDto(Groupe groupe);
}
