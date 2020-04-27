package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Set<Authority> findAllByAuthorityAndConferenceFullName(String authority, String conferenceFullName);

    Set<Authority> findAllByAuthorityContainingAndConferenceFullName(String authority, String conferenceFullName);

    Set<Authority> findAllByAuthorityContainingAndUserAndConferenceFullName(String authority, User user, String conferenceFullName);

    Authority findByAuthorityAndUserAndConferenceFullName(String authority, User user, String conferenceFullName);
}
