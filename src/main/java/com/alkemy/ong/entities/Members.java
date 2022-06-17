package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "members")
@NoArgsConstructor
@Getter
@Setter
public class Members {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "facebookUrl")
    private String facebookUrl;

    @Column(name = "instagramUrl")
    private String instagramUrl;

    @Column(name = "linkedinUrl")
    private String linkedinUrl;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;
    private boolean softDelete;
}
