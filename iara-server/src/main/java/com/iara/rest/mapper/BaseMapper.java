package com.iara.rest.mapper;

public interface BaseMapper<T, D> {

    T toEntity(D dto);

    D toDTO(T entity);
}
