package sn.free.selfcare.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class LigneDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneDTO.class);
        LigneDTO ligneDTO1 = new LigneDTO();
        ligneDTO1.setId(1L);
        LigneDTO ligneDTO2 = new LigneDTO();
        assertThat(ligneDTO1).isNotEqualTo(ligneDTO2);
        ligneDTO2.setId(ligneDTO1.getId());
        assertThat(ligneDTO1).isEqualTo(ligneDTO2);
        ligneDTO2.setId(2L);
        assertThat(ligneDTO1).isNotEqualTo(ligneDTO2);
        ligneDTO1.setId(null);
        assertThat(ligneDTO1).isNotEqualTo(ligneDTO2);
    }
}
