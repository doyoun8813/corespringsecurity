package security.io.corespringsecurity.service;

import java.util.List;

import org.springframework.security.access.annotation.Secured;

import security.io.corespringsecurity.domain.dto.AccountDto;
import security.io.corespringsecurity.domain.entity.Account;

public interface UserService {

    void createUser(Account account);

    void modifyUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void deleteUser(Long idx);

    void order();
}
