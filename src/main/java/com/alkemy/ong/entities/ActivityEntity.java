package com.alkemy.ong.entities;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "activity")
@SQLDelete(sql = "UPDATE activity SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ActivityEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name="id", updatable = false, nullable = false)
  private UUID id;
  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "description", nullable = false)
  private String content;
  @Column(name = "image", nullable = false)
  private String image;
  @Column(name = "created_at", nullable = false)
  private Timestamp createdAt;
  private Boolean deleted = Boolean.FALSE;


}
