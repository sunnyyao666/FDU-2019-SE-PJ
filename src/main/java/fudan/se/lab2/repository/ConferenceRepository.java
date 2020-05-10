package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    Conference findByFullName(String fullName);

    Set<Conference> findAllByApplying(boolean applying);

    Set<Conference> findAllByValid(boolean valid);    
    Set<Conference> findAllByValidAndSubmittingAndAuditing(boolean valid, boolean submitting, boolean auditing);
}
