package com.iara.core.service;

import com.iara.core.entity.NodeInfo;

public interface ReplicationService {

    void replicateToFollowers();

    void replicateToNode(NodeInfo node);

    void addData(String key, String value);

    void replicateToMajority();
}
