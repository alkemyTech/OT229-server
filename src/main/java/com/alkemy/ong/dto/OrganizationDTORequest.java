package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTORequest extends OrganizationDTO {

    @Valid
    private EncodedImageDTO encoded_image;

}
