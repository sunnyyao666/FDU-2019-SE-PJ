package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Thesis;
import fudan.se.lab2.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ThesisRepository extends CrudRepository<Thesis, Long> {
    Set<Thesis> findAllByAuthorAndConferenceFullName(User author, String conferenceFullName);
}
