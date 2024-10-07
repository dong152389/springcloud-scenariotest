import org.cloud.demo.auth.AuthApplication;
import org.cloud.demo.auth.mapper.RoleMapper;
import org.cloud.demo.common.domain.RoleDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = AuthApplication.class)
public class RoleTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void roleUser() {
        long userId = 1842963261159907330L;
        List<RoleDTO> roleDTOS = roleMapper.selectRoleByUserId(userId);
        System.out.println(roleDTOS);
    }
}
