package com.itdev.mapper;

public interface Mapper<F, T> {

    T map(F fromObj);
}
