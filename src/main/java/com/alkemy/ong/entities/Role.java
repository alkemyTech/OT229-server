package com.alkemy.ong.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @CreationTimestamp
    private Date timestamp;
}
