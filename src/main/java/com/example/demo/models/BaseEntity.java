package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class BaseEntity {

    private String name;

    private Long createdOn;

    private Long createdBy;

}