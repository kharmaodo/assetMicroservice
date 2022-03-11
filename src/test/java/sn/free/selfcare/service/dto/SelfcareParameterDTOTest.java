package sn.free.selfcare.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class SelfcareParameterDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SelfcareParameterDTO.class);
        SelfcareParameterDTO selfcareParameterDTO1 = new SelfcareParameterDTO();
        selfcareParameterDTO1.setId(1L);
        SelfcareParameterDTO selfcareParameterDTO2 = new SelfcareParameterDTO();
        assertThat(selfcareParameterDTO1).isNotEqualTo(selfcareParameterDTO2);
        selfcareParameterDTO2.setId(selfcareParameterDTO1.getId());
        assertThat(selfcareParameterDTO1).isEqualTo(selfcareParameterDTO2);
        selfcareParameterDTO2.setId(2L);
        assertThat(selfcareParameterDTO1).isNotEqualTo(selfcareParameterDTO2);
        selfcareParameterDTO1.setId(null);
        assertThat(selfcareParameterDTO1).isNotEqualTo(selfcareParameterDTO2);
    }
}
