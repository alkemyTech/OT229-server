package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "organizations")
@SQLDelete(sql = "UPDATE organizations SET soft_delete = true where id = ?")
@Where(clause = "soft_delete = false")
@NoArgsConstructor
@Getter
@Setter
public class Organization {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private int phone;

    @Column(name = "email")
    private String email;

    @Column(name = "welcomeText", nullable = false)
    private String welcomeText;

    @Length(max = 2000)
    @Column(name = "aboutUsText")
    private String aboutUsText;

    @Column(name = "timeStamp")
    @CreationTimestamp
    private Date timeStamp;

    @Column(name = "soft_delete")
    private boolean softDelete = Boolean.FALSE;
    @Column(name = "urlFacebook")
    private String urlFacebook;
    @Column(name = "urlInstragram")
    private String urlInstagram;
    @Column(name = "urlLinkedin")
    private String urlLinkedin;

}