package sn.free.selfcare.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class SelfcareParameterTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SelfcareParameter.class);
        SelfcareParameter selfcareParameter1 = new SelfcareParameter();
        selfcareParameter1.setId(1L);
        SelfcareParameter selfcareParameter2 = new SelfcareParameter();
        selfcareParameter2.setId(selfcareParameter1.getId());
        assertThat(selfcareParameter1).isEqualTo(selfcareParameter2);
        selfcareParameter2.setId(2L);
        assertThat(selfcareParameter1).isNotEqualTo(selfcareParameter2);
        selfcareParameter1.setId(null);
        assertThat(selfcareParameter1).isNotEqualTo(selfcareParameter2);
    }
}
