package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReducedSlideDTO {
    private String imageUrl;
    private int slideOrder;
}
