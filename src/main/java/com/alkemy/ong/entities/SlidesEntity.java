package com.alkemy.ong.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "slides")
public class SlidesEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id")
  private String id;

  @Column(name="organizationId", nullable = false)
  private String organizationId;

  @Column(name="imageUrl", nullable = false)
  private String imageUrl;

  @Column(name = "text", nullable = false)
  private String text;

  @Column(name = "slide_order")
  private Integer slideOrder;

}
