package sn.free.selfcare.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class LigneTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ligne.class);
        Ligne ligne1 = new Ligne();
        ligne1.setId(1L);
        Ligne ligne2 = new Ligne();
        ligne2.setId(ligne1.getId());
        assertThat(ligne1).isEqualTo(ligne2);
        ligne2.setId(2L);
        assertThat(ligne1).isNotEqualTo(ligne2);
        ligne1.setId(null);
        assertThat(ligne1).isNotEqualTo(ligne2);
    }
}
