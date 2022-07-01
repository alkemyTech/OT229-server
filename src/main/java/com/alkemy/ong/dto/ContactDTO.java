package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ContactDTO {
    private String name;
    private String email;
    private Long phone;
    private String message;
    private Timestamp deletedAt;

}
