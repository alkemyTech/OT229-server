package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "testimonials")
public class Testimonial {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid")
    private String id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "image")
    private String image;
    @Column(name = "content")
    private String content;

    @CreationTimestamp
    private Date timeStamp;
    private boolean softDelete;

}