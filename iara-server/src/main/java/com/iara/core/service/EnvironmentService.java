package com.iara.core.service;

import com.iara.core.entity.Environment;
import com.iara.core.entity.Namespace;

import java.util.List;

public interface EnvironmentService extends BaseService<Environment> {

    List<Environment> findByNamespace(Namespace namespace);
    void deleteAll(List<Environment> environments);
}
