package security.io.corespringsecurity.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MassageController {

    @GetMapping("/messages")
    public String message() throws Exception {
        return "user/messges";
    }
}
