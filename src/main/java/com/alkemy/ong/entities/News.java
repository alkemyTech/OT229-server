package com.alkemy.ong.entities;

import lombok.AllArgsConstructor;
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
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE news SET soft_delete = true WHERE id = ?")
@Where(clause = "soft_delete = false")
public class News {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    private String image;

    @CreationTimestamp
    private Date timestamp;
    @Column(name = "soft_delete")
    private Boolean softDelete=Boolean.FALSE;

    @ManyToOne()
    @JoinColumn(name = "categoryId")
    private Category category;





}
