package com.alkemy.ong.dto;

import com.alkemy.ong.utility.GlobalConstants;
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

    private List<T> content = new ArrayList<>();
    private String next_page_url;
    private String previous_page_url;

}
