package sn.free.selfcare.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class OffreDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OffreDTO.class);
        OffreDTO offreDTO1 = new OffreDTO();
        offreDTO1.setId(1L);
        OffreDTO offreDTO2 = new OffreDTO();
        assertThat(offreDTO1).isNotEqualTo(offreDTO2);
        offreDTO2.setId(offreDTO1.getId());
        assertThat(offreDTO1).isEqualTo(offreDTO2);
        offreDTO2.setId(2L);
        assertThat(offreDTO1).isNotEqualTo(offreDTO2);
        offreDTO1.setId(null);
        assertThat(offreDTO1).isNotEqualTo(offreDTO2);
    }
}
