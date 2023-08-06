package security.io.corespringsecurity.service;

import java.util.List;

import security.io.corespringsecurity.domain.entity.Role;

public interface RoleService {

    Role getRole(long id);

    List<Role> getRoles();

    void createRole(Role role);

    void deleteRole(long id);
}
