package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Set<Authority> findByAuthority(String authority);
}
