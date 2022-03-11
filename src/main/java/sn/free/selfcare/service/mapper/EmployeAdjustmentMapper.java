package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.free.selfcare.domain.Employe;
import sn.free.selfcare.service.dto.EmployeDTO;
import sn.free.selfcare.service.dto.operation.EmployeAdjustmentDTO;

/**
 * Mapper for the entity {@link Employe} and its DTO {@link EmployeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeAdjustmentMapper extends EntityMapper<EmployeAdjustmentDTO, Employe> {

	@Mapping(source = "ligne.numero", target = "numero")
	// @Mapping(source = "ligne.offre.grilleTarifaire.credit", target =
	// "tarifCredit")
	@Mapping(source = "ligne.offre.grilleTarifaire.sms", target = "tarifSms")
	@Mapping(source = "ligne.offre.grilleTarifaire.minAppel", target = "tarifVoix")
	@Mapping(source = "ligne.offre.grilleTarifaire.goData", target = "tarifData")
	EmployeAdjustmentDTO toDto(Employe employe);
}
