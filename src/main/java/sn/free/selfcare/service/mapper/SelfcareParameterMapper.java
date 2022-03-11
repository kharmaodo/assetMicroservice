package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.SelfcareParameterDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SelfcareParameter} and its DTO {@link SelfcareParameterDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SelfcareParameterMapper extends EntityMapper<SelfcareParameterDTO, SelfcareParameter> {



    default SelfcareParameter fromId(Long id) {
        if (id == null) {
            return null;
        }
        SelfcareParameter selfcareParameter = new SelfcareParameter();
        selfcareParameter.setId(id);
        return selfcareParameter;
    }
}
