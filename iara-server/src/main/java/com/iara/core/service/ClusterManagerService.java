package com.iara.core.service;

public interface ClusterManagerService {

    void discoverNodes();

    void registerSelf();

    void checkNodeHealth(String nodeId, String host, int port);

    void checkInactiveNodes();
}
