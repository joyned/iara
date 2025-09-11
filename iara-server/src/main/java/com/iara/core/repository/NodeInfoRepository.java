package com.iara.core.repository;

import com.iara.core.entity.NodeInfo;
import com.iara.core.model.NodeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NodeInfoRepository extends JpaRepository<NodeInfo, String> {

    List<NodeInfo> findByActiveTrue();

    List<NodeInfo> findByRoleAndActiveTrue(NodeRole role);

    Optional<NodeInfo> findByHostAndPort(String host, int port);

    @Query("SELECT n FROM NodeInfo n WHERE n.active = true AND n.lastHeartbeat < :cutoffTime")
    List<NodeInfo> findInactiveNodes(@Param("cutoffTime") Date cutoffTime);

    @Modifying
    @Query("UPDATE NodeInfo n SET n.active = false WHERE n.lastHeartbeat < :cutoffTime")
    void deactivateInactiveNodes(@Param("cutoffTime") Date cutoffTime);

    @Modifying
    @Query("UPDATE NodeInfo n SET n.role = :role, n.term = :term WHERE n.id = :id")
    void updateNodeRoleAndTerm(@Param("id") String id, @Param("role") NodeRole role, @Param("term") long term);
}
