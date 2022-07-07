package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.PageResultResponse;
import org.springframework.data.domain.Page;

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
         * Provides a mapper function to convert the listed entities.
         *
         * @param entityMapperFunction  a mapper function to convert the entities of class T into objects of class R.
         * @return  a sub-builder with both the data source and the mapper function to continue the building process.
         */
        public MappedProvidedBuilder <T, R> mapWith(Function<T, R> entityMapperFunction) {
            return new MappedProvidedBuilder<>(this.springDataPage, entityMapperFunction);
        }

        /**
         * Builds the PageResultResponse without converting the entities to another class.
         *
         * @return  a response with the original entities.
         */
        public PageResultResponse<T> build() {
            return new PageResultResponse<T>()
                    .setContent(this.springDataPage.getContent())
                    .setNextPageUrl(this.springDataPage.getNumber(), this.springDataPage.hasNext())
                    .setPreviousPageUrl(this.springDataPage.getNumber(), this.springDataPage.hasPrevious());
        }

        /**
         * Sub-Builder to construct a PageResultResponse, containing the data source and the mapper function.
         *
         * This sub-builder is intended to produce a result with converted elements.
         *
         * @param <T>   the class of the entities retrieved by the repository
         * @param <R>   the class of the objects to be contained in the response. If the response content doesn't need to be
         *              converted, then the same class as the original entities (T) can be provided again.
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
                        .setNextPageUrl(this.springDataPage.getNumber(), this.springDataPage.hasNext())
                        .setPreviousPageUrl(this.springDataPage.getNumber(), this.springDataPage.hasPrevious());
            }

        }

    }

}
