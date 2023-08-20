package security.io.corespringsecurity.security.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;

import jakarta.servlet.http.HttpServletRequest;
import security.io.corespringsecurity.security.service.SecurityResourceService;

public class CustomRequestMatcherDelegatingAuthorizationManager implements AuthorizationManager<HttpServletRequest> {

    private final Log logger = LogFactory.getLog(getClass());
    
    private final SecurityResourceService securityResourceService;

    private List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;

    public CustomRequestMatcherDelegatingAuthorizationManager(
        List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings, SecurityResourceService securityResourceService) {
        this.mappings = mappings;
        this.securityResourceService = securityResourceService;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, HttpServletRequest request) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Authorizing %s", request));
        }

        // mappings.add(new RequestMatcherEntry<>(new AntPathRequestMatcher("/mypage"),
        //     new WebExpressionAuthorizationManager("hasRole('ROLE_USER')")));

        for (RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> mapping : this.mappings) {

            RequestMatcher matcher = mapping.getRequestMatcher();
            RequestMatcher.MatchResult matchResult = matcher.matcher(request);
            if (matchResult.isMatch()) {
                AuthorizationManager<RequestAuthorizationContext> manager = mapping.getEntry();
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(LogMessage.format("Checking authorization on %s using %s", request, manager));
                }
                return manager.check(authentication,
                    new RequestAuthorizationContext(request, matchResult.getVariables()));
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.of(() -> "Denying request since did not find matching RequestMatcher"));
        }
        return null;
    }

    public void reload() {

        mappings.clear();
        mappings = securityResourceService.getResourceList();

    }
    
}
