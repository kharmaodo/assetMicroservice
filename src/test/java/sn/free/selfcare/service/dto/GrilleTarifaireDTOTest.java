package sn.free.selfcare.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class GrilleTarifaireDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GrilleTarifaireDTO.class);
        GrilleTarifaireDTO grilleTarifaireDTO1 = new GrilleTarifaireDTO();
        grilleTarifaireDTO1.setId(1L);
        GrilleTarifaireDTO grilleTarifaireDTO2 = new GrilleTarifaireDTO();
        assertThat(grilleTarifaireDTO1).isNotEqualTo(grilleTarifaireDTO2);
        grilleTarifaireDTO2.setId(grilleTarifaireDTO1.getId());
        assertThat(grilleTarifaireDTO1).isEqualTo(grilleTarifaireDTO2);
        grilleTarifaireDTO2.setId(2L);
        assertThat(grilleTarifaireDTO1).isNotEqualTo(grilleTarifaireDTO2);
        grilleTarifaireDTO1.setId(null);
        assertThat(grilleTarifaireDTO1).isNotEqualTo(grilleTarifaireDTO2);
    }
}
