package com.alkemy.ong.security.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class ValidationErrorResponse {

    @Schema(description = "The attribute validation error.", example = "Mandatory attribute content missing.")
    private List<String> error = new ArrayList<>();

    public void addError(String error) {
        this.error.add(error);
    }

}
