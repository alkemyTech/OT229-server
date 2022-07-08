package com.alkemy.ong.entities;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Date;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE testimonials SET soft_delete = true where id = ?")
@Where(clause = "soft_delete = false")
@Table(name = "testimonials")
public class Testimonial {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
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