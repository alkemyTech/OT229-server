package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "members")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE members SET soft_delete = true where id = ?")
@Where(clause = "soft_delete = false")
public class Member {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "facebookUrl")
    private String facebookUrl;

    @Column(name = "instagramUrl")
    private String instagramUrl;

    @Column(name = "linkedinUrl")
    private String linkedinUrl;

    @Column(name = "image")
    private String image;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    private Date timeStamp;
    private boolean softDelete;
}
