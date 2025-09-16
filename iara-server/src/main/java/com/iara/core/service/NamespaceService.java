package com.iara.core.service;

import com.iara.core.entity.Namespace;

import java.util.Optional;

public interface NamespaceService extends BaseService<Namespace> {

    Optional<Namespace> findByName(String name);
}
