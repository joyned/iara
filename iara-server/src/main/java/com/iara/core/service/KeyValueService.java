package com.iara.core.service;

import com.iara.core.entity.Kv;
import com.iara.core.entity.KvHistory;

import java.util.List;

public interface KeyValueService extends BaseService<Kv> {

    List<KvHistory> history(String kvId);

    Kv get(String namespace, String environment, String kv);
}
