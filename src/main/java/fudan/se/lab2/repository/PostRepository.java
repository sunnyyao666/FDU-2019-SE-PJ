package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Set<Post> findAllByThesisID(Long thesisID);
}
