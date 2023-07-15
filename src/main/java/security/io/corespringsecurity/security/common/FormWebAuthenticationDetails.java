package security.io.corespringsecurity.security.common;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private final String secretKey;
    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);

        secretKey = request.getParameter("secretKey");
    }

    public String getSecretKey() {
        return secretKey;
    }
}
