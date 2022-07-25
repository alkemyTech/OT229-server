package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder to construct a PageResultResponse from a <code>org.springframework.data.domain.Page</code> which is returned
 * as the result of a paginated search made by the repository.
 *
 * @param <T>   the class of the entities retrieved by the repository
 * @param <R>   the class of the objects to be contained in the response. If the response content doesn't need to be
 *              converted, then the same class as the original entities (T) can be provided again.
 */
public class PageResultResponseBuilder<T, R> {

    /**
     * Builds the url to send a request to get the next page of results.
     *
     * @param hasNext   indicates whether a page with an index higher than the current one exists.
     * @param currentPageIndex the number of the current page of the results.
     * @return  the url for the next result page, or <code>null</code> if there is no page after the current one.
     */
    public static String buildNextPageUrl(boolean hasNext, long currentPageIndex) {
        if (!hasNext) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replaceQueryParam(GlobalConstants.PAGE_INDEX_PARAM, currentPageIndex + 1)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Builds the url to send a request to get the previous page of results.
     *
     * @param hasPrevious indicates whether a page with an index lower than the current one exists.
     * @param currentPageIndex the number of the current page of the results.
     * @param lastPageIndex the number of the last page of results.
     * @return  the url for the previous result page, or <code>null</code> if there is no page before the current one,
     *          or the index of the last page if an index beyond the last page was provided.
     */
    public static String buildPreviousPageUrl(boolean hasPrevious, long currentPageIndex, long lastPageIndex) {
        if (!hasPrevious || lastPageIndex < 0) {
            return null;
        }
        long previousPageIndex;
        if (currentPageIndex > lastPageIndex) {
            previousPageIndex = lastPageIndex;
        } else {
            previousPageIndex = currentPageIndex - 1;
        }
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replaceQueryParam(GlobalConstants.PAGE_INDEX_PARAM, previousPageIndex)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Provides the original data source.
     *
     * @param springDataPage    the page returned by the repository's paginated search.
     * @return  a sub-builder with the provided data source to continue the building process.
     */
    public ContentProvidedBuilder<T, R> from(Page<T> springDataPage) {
        return new ContentProvidedBuilder<>(springDataPage);
    }

    /**
     * Sub-Builder to construct a PageResultResponse, containing the data source.
     *
     * @param <T>   the class of the entities retrieved by the repository
     * @param <R>   the class of the objects to be contained in the response. If the response content doesn't need to be
     *              converted, then the same class as the original entities (T) can be provided again.
     */
    public static class ContentProvidedBuilder<T, R> {

        private final Page<T> springDataPage;

        public ContentProvidedBuilder(Page<T> springDataPage) {
            this.springDataPage = springDataPage;
        }

        /**
         * Builds the PageResultResponse without converting the entities to another class.
         *
         * @return  a response with the original entities.
         */
        public PageResultResponse<T> build() {
            return new PageResultResponse<T>()
                    .setContent(this.springDataPage.getContent())
                    .setNext_page_url(
                            PageResultResponseBuilder.buildNextPageUrl(this.springDataPage.hasNext(), this.springDataPage.getNumber())
                    )
                    .setPrevious_page_url(
                            PageResultResponseBuilder.buildPreviousPageUrl(
                                    this.springDataPage.hasPrevious(),
                                    this.springDataPage.getNumber(),
                                    this.springDataPage.getTotalPages() - 1
                            )
                    );
        }

        /**
         * Provides a mapper function to convert the listed entities.
         *
         * @param entityMapperFunction  a mapper function to convert the entities of class T into objects of class R.
         * @return  a sub-builder with both the data source and the mapper function to continue the building process.
         */
        public MappedProvidedBuilder <T, R> mapWith(Function<T, R> entityMapperFunction) {
            return new MappedProvidedBuilder<>(this.springDataPage, entityMapperFunction);
        }

        /**
         * Sub-Builder to construct a PageResultResponse, containing the data source and the mapper function.
         *
         * This sub-builder is intended to produce a result with converted elements.
         *
         * @param <T>   the class of the entities retrieved by the repository
         * @param <R>   the class of the objects that the entities will be converted to.
         */
        public static class MappedProvidedBuilder <T, R> {

            private final Page<T> springDataPage;
            private final Function<T, R> entityMapperFunction;

            public MappedProvidedBuilder(Page<T> springDataPage, Function<T, R> entityMapperFunction) {
                this.springDataPage = springDataPage;
                this.entityMapperFunction = entityMapperFunction;
            }

            /**
             * Builds the PageResultResponse, mapping the contained entities into another class (probably a DTO or a String).
             *
             * @return  a response with the mapped objects.
             */
            public PageResultResponse<R> build() {
                return new PageResultResponse<R>()
                        .setContent(
                                this.springDataPage.get()
                                        .map(this.entityMapperFunction)
                                        .collect(Collectors.toList())
                        )
                        .setNext_page_url(
                                PageResultResponseBuilder.buildNextPageUrl(this.springDataPage.hasNext(), this.springDataPage.getNumber())
                        )
                        .setPrevious_page_url(
                                PageResultResponseBuilder.buildPreviousPageUrl(
                                        this.springDataPage.hasPrevious(),
                                        this.springDataPage.getNumber(),
                                        this.springDataPage.getTotalPages() - 1
                                )
                        );
            }

        } // MappedProvidedBuilder class

    } // End of ContentProvidedBuilder class

} // End of PageResultResponseBuilder class
