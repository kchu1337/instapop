package com.kc1337.repositories;

import com.kc1337.models.*;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by student on 7/13/17.
 */
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    public Iterable<Comment> findAllByImage(Image image);
}
