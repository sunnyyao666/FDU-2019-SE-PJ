package fudan.se.lab2.repository;


import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.PCAudit;
import fudan.se.lab2.domain.Thesis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface PCAuditRepository extends CrudRepository<PCAudit, Long> {
    PCAudit findByAuthorityAndThesisID(Authority authority, Long thesisID);

    Set<PCAudit> findAllByAuthority_ConferenceFullName(String conferenceFullName);

    void deleteAllByAuthority_ConferenceFullName(String conferenceFullName);

    void deleteAllByThesisID(Long thesisID);
}
