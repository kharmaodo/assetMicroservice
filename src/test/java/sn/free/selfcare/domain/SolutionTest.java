package sn.free.selfcare.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import sn.free.selfcare.web.rest.TestUtil;

public class SolutionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Solution.class);
        Solution solution1 = new Solution();
        solution1.setId(1L);
        Solution solution2 = new Solution();
        solution2.setId(solution1.getId());
        assertThat(solution1).isEqualTo(solution2);
        solution2.setId(2L);
        assertThat(solution1).isNotEqualTo(solution2);
        solution1.setId(null);
        assertThat(solution1).isNotEqualTo(solution2);
    }
}
