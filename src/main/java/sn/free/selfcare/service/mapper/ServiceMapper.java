package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.ServiceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Service} and its DTO {@link ServiceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ServiceMapper extends EntityMapper<ServiceDTO, Service> {


    @Mapping(target = "lignes", ignore = true)
    @Mapping(target = "removeLignes", ignore = true)
    Service toEntity(ServiceDTO serviceDTO);

    default Service fromId(Long id) {
        if (id == null) {
            return null;
        }
        Service service = new Service();
        service.setId(id);
        return service;
    }
}
