package security.io.corespringsecurity.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;

import security.io.corespringsecurity.domain.entity.Resources;
import security.io.corespringsecurity.repository.ResourcesRepository;

public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;

    public SecurityResourceService(ResourcesRepository resourcesRepository) {
        this.resourcesRepository = resourcesRepository;
    }

    public List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> getResourceList() {
        List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> result = new ArrayList<>();
        List<Resources> allResources = resourcesRepository.findAllResources();
        allResources.forEach(resources -> {
            final String[] roleStr = {""};
            resources.getRoleSet().forEach(role -> {
                if(!roleStr[0].isEmpty()) {
                    roleStr[0] += "','";
                }
                roleStr[0] += role.getRoleName();
            });
            result.add(new RequestMatcherEntry<>(new AntPathRequestMatcher(resources.getResourceName()),
                new WebExpressionAuthorizationManager("hasAnyRole('"+ roleStr[0] +"')")));
        });

        return result;
    }
}
