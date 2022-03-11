package sn.free.selfcare.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class GrilleTarifaireTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GrilleTarifaire.class);
        GrilleTarifaire grilleTarifaire1 = new GrilleTarifaire();
        grilleTarifaire1.setId(1L);
        GrilleTarifaire grilleTarifaire2 = new GrilleTarifaire();
        grilleTarifaire2.setId(grilleTarifaire1.getId());
        assertThat(grilleTarifaire1).isEqualTo(grilleTarifaire2);
        grilleTarifaire2.setId(2L);
        assertThat(grilleTarifaire1).isNotEqualTo(grilleTarifaire2);
        grilleTarifaire1.setId(null);
        assertThat(grilleTarifaire1).isNotEqualTo(grilleTarifaire2);
    }
}
