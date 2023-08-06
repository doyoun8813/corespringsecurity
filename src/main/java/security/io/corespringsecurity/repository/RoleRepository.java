package security.io.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import security.io.corespringsecurity.domain.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);

}
