package security.io.corespringsecurity.service;

import security.io.corespringsecurity.domain.entity.Account;

public interface UserService {

    void createUser(Account account);

}
