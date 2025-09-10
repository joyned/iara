package com.iara.rest.mapper;

import com.iara.core.entity.KvHistory;
import com.iara.rest.dto.KvHistoryDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface KeyValueHistoryMapper extends BaseMapper<KvHistory, KvHistoryDTO> {
}
