package sn.free.selfcare.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class SolutionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolutionDTO.class);
        SolutionDTO solutionDTO1 = new SolutionDTO();
        solutionDTO1.setId(1L);
        SolutionDTO solutionDTO2 = new SolutionDTO();
        assertThat(solutionDTO1).isNotEqualTo(solutionDTO2);
        solutionDTO2.setId(solutionDTO1.getId());
        assertThat(solutionDTO1).isEqualTo(solutionDTO2);
        solutionDTO2.setId(2L);
        assertThat(solutionDTO1).isNotEqualTo(solutionDTO2);
        solutionDTO1.setId(null);
        assertThat(solutionDTO1).isNotEqualTo(solutionDTO2);
    }
}
