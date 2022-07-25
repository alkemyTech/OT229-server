package com.alkemy.ong.dto;

import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic DTO to serve as the response body for all requests for paginated search results.
 *
 * @param <T>   the class of the listed elements in the page.
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PageResultResponse<T> {

    @Schema(description = "The results contained in the current page")
    private List<T> content = new ArrayList<>();
    @Schema(description = "The url to forward a request to access the next page of results", example = "http://localhost:8080/testimonial?page=10")
    private String next_page_url;
    @Schema(description = "The url to forward a request to access the next page of results", example = "http://localhost:8080/testimonial?page=8")
    private String previous_page_url;

}
