package security.io.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import security.io.corespringsecurity.domain.Account;

public interface UserRepository extends JpaRepository<Account, Long> {
}
