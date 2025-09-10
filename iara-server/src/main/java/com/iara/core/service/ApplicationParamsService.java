package com.iara.core.service;

import com.iara.core.entity.ApplicationParams;

public interface ApplicationParamsService extends BaseService<ApplicationParams> {

    ApplicationParams findByKey(String key);

}
