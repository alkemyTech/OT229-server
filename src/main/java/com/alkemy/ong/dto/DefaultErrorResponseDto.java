package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultErrorResponseDto {

    @Schema(description = "The date and time of the error", example = "2022-06-25T20:27:32.309+00:00")
    private String timestamp;

    @Schema(description = "The http status code of the response", example = "404")
    private String status;

    @Schema(description = "The description of the status code", example = "Not Found")
    private String error;

    @Schema(description = "The endpoint of the request", example = "/testimonials")
    private String path;

}
