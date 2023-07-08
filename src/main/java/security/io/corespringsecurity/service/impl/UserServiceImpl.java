package security.io.corespringsecurity.service.impl;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import security.io.corespringsecurity.domain.Account;
import security.io.corespringsecurity.repository.UserRepository;
import security.io.corespringsecurity.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
