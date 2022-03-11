package sn.free.selfcare.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class OffreMapperTest {

    private OffreMapper offreMapper;

    @BeforeEach
    public void setUp() {
        offreMapper = new OffreMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(offreMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(offreMapper.fromId(null)).isNull();
    }
}
