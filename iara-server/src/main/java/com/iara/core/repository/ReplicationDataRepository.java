package com.iara.core.repository;

import com.iara.core.entity.ReplicationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReplicationDataRepository extends JpaRepository<ReplicationData, String> {

    Optional<ReplicationData> findByKey(String key);

    List<ReplicationData> findByCommittedTrue();

    List<ReplicationData> findByCommittedFalse();

    List<ReplicationData> findByTerm(long term);

    @Query("SELECT r FROM ReplicationData r WHERE r.timestamp > :since")
    List<ReplicationData> findSince(@Param("since") Date since);

    @Modifying
    @Query("UPDATE ReplicationData r SET r.committed = true WHERE r.id IN :ids")
    void markAsCommitted(@Param("ids") List<Long> ids);

    @Modifying
    @Query("DELETE FROM ReplicationData r WHERE r.key = :key")
    void deleteByKey(@Param("key") String key);

}
