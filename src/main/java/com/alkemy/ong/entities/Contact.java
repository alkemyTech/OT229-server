package com.alkemy.ong.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "contacts")
@SQLDelete(sql = "UPDATE contacts SET soft_delete = true where id = ?")
@Where(clause = "soft_delete = false")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Contact {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false)
    private String email;

    @Column
    private Long phone;

    @Column
    private String message;

    @Column(name = "deleted_at", updatable = false)
    private Timestamp deletedAt;

    @Column
    private boolean softDelete;

    @PreRemove
    public void deletedTimestamp(){
        deletedAt = new Timestamp(System.currentTimeMillis());
    }
}