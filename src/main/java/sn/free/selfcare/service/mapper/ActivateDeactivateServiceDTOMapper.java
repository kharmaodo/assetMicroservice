package sn.free.selfcare.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.free.selfcare.service.dto.ActivateDeactivateServiceDTO;
import sn.free.selfcare.service.dto.ActivateDeactivateServiceRequestDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ActivateDeactivateServiceDTOMapper {

    @Mapping(source = "service.serviceCode", target = "serviceName")
    ActivateDeactivateServiceRequestDTO toRequestDTO(ActivateDeactivateServiceDTO dto);
}
