package com.alkemy.ong.security.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStatusResponse {

    @Schema(description = "Whether the response is ok or not.", example = "false")
    private String ok;

}
