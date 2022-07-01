package com.alkemy.ong.dto;

import com.alkemy.ong.entities.Organization;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SlidesEntityDTO {

    private String id;
    private String organizationId;
    private String imageUrl;
    private String text;
    private int slideOrder;

}
