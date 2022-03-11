package sn.free.selfcare.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LigneMapperTest {

    private LigneMapper ligneMapper;

    @BeforeEach
    public void setUp() {
        ligneMapper = new LigneMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(ligneMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(ligneMapper.fromId(null)).isNull();
    }
}
