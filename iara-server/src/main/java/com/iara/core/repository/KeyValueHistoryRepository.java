package com.iara.core.repository;

import com.iara.core.entity.Kv;
import com.iara.core.entity.KvHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeyValueHistoryRepository extends JpaRepository<KvHistory, String> {

    List<KvHistory> findByKeyValueOrderByUpdatedAtDesc(Kv kv);
}
