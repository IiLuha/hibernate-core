package com.itdev.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdSequence {

    private int nextId;

    public Integer generateNextId() {
        return ++nextId;
    }
}
