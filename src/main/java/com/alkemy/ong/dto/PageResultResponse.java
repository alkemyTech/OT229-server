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
    @Setter(AccessLevel.NONE)
    private String next_page_url;
    @Setter(AccessLevel.NONE)
    private String previous_page_url;

    public PageResultResponse<T> setNextPageUrl(int currentPageNumber, boolean hasNext) {
        if (hasNext) {
            this.next_page_url = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .replaceQueryParam(GlobalConstants.PAGE_INDEX_PARAM, currentPageNumber+1)
                    .build()
                    .encode()
                    .toUriString();
        }
        return this;
    }

    public PageResultResponse<T> setPreviousPageUrl(int currentPageNumber, boolean hasPrevious) {
        if (hasPrevious) {
            this.previous_page_url = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .replaceQueryParam(GlobalConstants.PAGE_INDEX_PARAM, currentPageNumber-1)
                    .build()
                    .encode()
                    .toUriString();
        }
        return this;
    }

}
