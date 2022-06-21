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

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "photo")
    private String photo;

    //@OneToMany
    //private Rol roleId; // Descomentar cuando esté la entidad Rol

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamps;
    private boolean softDelete;
}
