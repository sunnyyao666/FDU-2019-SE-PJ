package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference,Long> {
    Conference findByConferenceName (String conferenceName);
}
