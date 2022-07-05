package com.alkemy.ong.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Entity
@Table(name = "activity")
@SQLDelete(sql = "UPDATE activity SET soft_delete = true WHERE id = ?")
@Where(clause = "soft_delete = false")
public class ActivityEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "uuid2")
  @Column(name="id")
  private String id;
  @Column(name = "name", nullable = false)
  private String name;
  @Length(max = 10000)
  @Column(name = "description", nullable = false)
  private String content;
  @Column(name = "image")
  private String image;

  @CreationTimestamp
  @Column(name = "timestamps", nullable = false)
  private Date timeStamps;
  private Boolean softDelete = Boolean.FALSE;


}
