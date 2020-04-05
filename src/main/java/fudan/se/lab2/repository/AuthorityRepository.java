package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.Conference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author YHT
 */
@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Authority findByAuthority(String authority);//找出如管理员这类没有会议字段的特殊权限
    Authority findByAuthority(String authority, Conference conference);//找某会议的某权限
}
