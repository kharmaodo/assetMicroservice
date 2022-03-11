package sn.free.selfcare.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ServiceMapperTest {

    private ServiceMapper serviceMapper;

    @BeforeEach
    public void setUp() {
        serviceMapper = new ServiceMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(serviceMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(serviceMapper.fromId(null)).isNull();
    }
}
