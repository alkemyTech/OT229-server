package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String id;
    private String name;
    private String facebookUrl;
    private String instagramUrl;
    private String linkedInUrl;
    private String image;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;
    private boolean softDelete;
}
