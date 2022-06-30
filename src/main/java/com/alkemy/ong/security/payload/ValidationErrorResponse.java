package com.alkemy.ong.security.payload;

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

    private List<String> error = new ArrayList<>();

    public void addError(String error) {
        this.error.add(error);
    }

}
