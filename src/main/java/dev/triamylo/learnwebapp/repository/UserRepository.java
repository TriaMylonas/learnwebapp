package dev.triamylo.learnwebapp.repository;

import dev.triamylo.learnwebapp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,String> {

    /*
     *  here I can add extra custom method that I want, and they must be implemented from the user service.
     * save(S entity)
     * saveAll(Iterable<S> entities)      "list of objects"
     * findById(ID id)
     * existsById(ID id)
     * findAll();
     * and other method that are in the CrudRepository
     * can I just used them!
     *
     */

    Optional<User> findByFirstName(String firstname);

    Optional<User> findByUsername(String username);
}
