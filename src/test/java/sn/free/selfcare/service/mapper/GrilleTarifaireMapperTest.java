package sn.free.selfcare.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class GrilleTarifaireMapperTest {

    private GrilleTarifaireMapper grilleTarifaireMapper;

    @BeforeEach
    public void setUp() {
        grilleTarifaireMapper = new GrilleTarifaireMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(grilleTarifaireMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(grilleTarifaireMapper.fromId(null)).isNull();
    }
}
