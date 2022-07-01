package com.alkemy.ong.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Table(name = "slides")
public class SlidesEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "uuid2")
  @Column(name = "id")
  private String id;

  @Column(name="organization_id", nullable = false)
  private String organizationId;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "text")
  private String text;

  @Column(name = "slide_order")
  private int slideOrder;

}
