package sn.free.selfcare.service.mapper;


import sn.free.selfcare.domain.*;
import sn.free.selfcare.service.dto.SolutionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Solution} and its DTO {@link SolutionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SolutionMapper extends EntityMapper<SolutionDTO, Solution> {



    default Solution fromId(Long id) {
        if (id == null) {
            return null;
        }
        Solution solution = new Solution();
        solution.setId(id);
        return solution;
    }
}
