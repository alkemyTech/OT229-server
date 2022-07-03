package com.alkemy.ong.services;

import com.alkemy.ong.entities.Category;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

/**
 * Interface to be implemented by services which can retrieve and return a whole Entity from the persistance storage.
 *
 * Precaution: this interface should only be used by other services.
 */
public interface CategoryEntityProvider {

    /**
     * Retrieves a Category entity from the database.
     *
     * @param name    the Category name.
     * @return  the Entity found wrapped in a Java Optional.
     */
    Optional<Category> getEntityByName(String name) throws EntityNotFoundException;

}
