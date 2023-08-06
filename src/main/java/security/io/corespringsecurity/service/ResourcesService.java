package security.io.corespringsecurity.service;

import java.util.List;

import security.io.corespringsecurity.domain.entity.Resources;

public interface ResourcesService {

    Resources getResources(long id);

    List<Resources> getResources();

    void createResources(Resources Resources);

    void deleteResources(long id);
}
