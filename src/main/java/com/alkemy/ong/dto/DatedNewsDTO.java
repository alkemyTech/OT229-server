package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Extension of the previous NewsDTO with the inclusion of the timestamp attribute.
 */
@Getter
@Setter
public class DatedNewsDTO extends NewsDTO {

    private String timestamp;

}
