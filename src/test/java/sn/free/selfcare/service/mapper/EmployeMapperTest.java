package sn.free.selfcare.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class EmployeMapperTest {

    private EmployeMapper employeMapper;

    @BeforeEach
    public void setUp() {
        employeMapper = new EmployeMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(employeMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(employeMapper.fromId(null)).isNull();
    }
}
