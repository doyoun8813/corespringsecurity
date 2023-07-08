package security.io.corespringsecurity.controller.user;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import security.io.corespringsecurity.domain.Account;
import security.io.corespringsecurity.domain.AccountDto;
import security.io.corespringsecurity.service.UserService;

@Controller
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/mypage")
    public String myPage() throws Exception {
        return "user/mypage";
    }

    // 회원가입 페이지 이동
    @GetMapping("/users")
    public String createUser(){
        return "user/login/register";
    }

    // 회원가입 등록 처리
    @PostMapping("/users")
    public String createUser(AccountDto accountDto){

        // AccoundDto로 받아온 값을 Account 객체로 값 넘김
        ModelMapper modelMapper = new ModelMapper();
        Account account = modelMapper.map(accountDto, Account.class);

        // 평문화된 비밀번호를 암호화 처리
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        userService.createUser(account);

        return "redirect:/";
    }

}
