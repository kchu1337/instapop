package com.kc1337.repositories;

import com.kc1337.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT id FROM userdata where name = :name", nativeQuery=true)
    public Iterable<Integer> findIdByName(@Param("name") String name);

    @Query(value = "SELECT id FROM userdata where email = :email", nativeQuery=true)
    public Integer findIdByLogin(@Param("email") String email);

    @Query(value = "SELECT id FROM userdata", nativeQuery=true)
    public Iterable<Integer> findId();


    public User findFirstById(int id);

    public User findByEmail(String email);

    public int countByEmail(String email);
}
