package com.alkemy.ong.services;
import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;

import java.util.List;


public interface CategoriesService {

    public CategoryDTO getById (String id);

    public CategoryDTO save (CategoryDTO dto);

    public CategoryDTO edit (CategoryDTO dto, String id);

    List<String> getAllCategoryNames();

    /**
     * Performs a paginated search for all the Category entries in the database and returns their names and the urls
     * to get the previous and next page results.
     *
     * <p>Page size and sorting criteria are read from global constants.
     *
     * @param pageNumber    the index of the page to be retrieved.
     * @return  the list of category names. If the provided index exceeds the last existing index, an empty list will
     *          be returned, and the previous page url attribute will point to the last available page.
     * @throws PageIndexOutOfBoundsException    if the index is not a positive integer.
     */
    PageResultResponse<String> getAllCategoryNames(int pageNumber) throws PageIndexOutOfBoundsException;

    public void deleteCategory(String id);
}

