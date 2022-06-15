package com.alkemy.ong.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String photo;

    //@OneToMany
    //private Rol roleId; // Descomentar cuando esté la entidad Rol

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamps;
    private boolean softDelete;
}
