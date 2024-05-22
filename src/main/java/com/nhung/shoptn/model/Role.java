package com.nhung.shoptn.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Role {
    @Enumerated(EnumType.STRING)
    USER,
    @Enumerated(EnumType.STRING)
    ADMIN
}
