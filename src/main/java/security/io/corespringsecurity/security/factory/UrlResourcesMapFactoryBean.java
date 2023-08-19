package security.io.corespringsecurity.security.factory;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;

import security.io.corespringsecurity.security.service.SecurityResourceService;

public class UrlResourcesMapFactoryBean implements FactoryBean<List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>>> {

    private SecurityResourceService securityResourceService;

    public void setSecurityResourceService(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    private List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;

    @Override
    public List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> getObject() throws Exception {

        if(mappings == null){
            init();
        }

        return mappings;
    }

    private void init() {
        mappings = securityResourceService.getResourceList();
    }

    @Override
    public Class<?> getObjectType() {
        return List.class;
    }
}
