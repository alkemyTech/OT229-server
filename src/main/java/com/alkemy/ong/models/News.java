package com.alkemy.ong.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(generator = "UUID")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @CreationTimestamp
    private Date timestamp;

    private Boolean softDelete;

   /* uncomment when the category entity is created
    @ManyToOne()
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

   */
}
