package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Thesis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThesisRepository extends CrudRepository<Thesis, Long> {
    Optional<Thesis> findById(Long id);
}
