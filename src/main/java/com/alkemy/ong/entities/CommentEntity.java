package com.alkemy.ong.entities;

import com.amazonaws.services.dynamodbv2.xspec.S;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class CommentEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "uuid2")
  @Column(name = "id")
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "news_id", nullable = false)
  private String newsId;

  @Column(name = "create_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime createDate;

  private String body;

  @PrePersist
  private void beforePersisting() {
    this.createDate = LocalDateTime.now();

  }
}