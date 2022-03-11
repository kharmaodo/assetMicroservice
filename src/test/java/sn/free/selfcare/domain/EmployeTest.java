package sn.free.selfcare.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class EmployeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employe.class);
        Employe employe1 = new Employe();
        employe1.setId(1L);
        Employe employe2 = new Employe();
        employe2.setId(employe1.getId());
        assertThat(employe1).isEqualTo(employe2);
        employe2.setId(2L);
        assertThat(employe1).isNotEqualTo(employe2);
        employe1.setId(null);
        assertThat(employe1).isNotEqualTo(employe2);
    }
}
