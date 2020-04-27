package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YHT
 */
@Entity
public class Authority implements GrantedAuthority, Comparable<Authority> {
    private static final long serialVersionUID = -8974777274465208640L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String authority;

    @ManyToOne
    @JsonIgnore
    private User user;

    private String username;
    private String conferenceFullName;
    private String inviter;
    private String topics;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "authority")
    private Set<PCAudit> pcAudits = new HashSet<>();

    public Authority() {
    }

    public Authority(String authority, User user, String conferenceFullName, String inviter) {
        this.authority = authority;
        this.user = user;
        this.username = user.getUsername();
        this.conferenceFullName = conferenceFullName;
        this.inviter = inviter;
        this.topics = null;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.username = user.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Set<PCAudit> getPCAudits() {
        return pcAudits;
    }

    public void setPCAudits(Set<PCAudit> pcAudits) {
        this.pcAudits = pcAudits;
    }

    @Override
    public int compareTo(Authority o) {
        return this.pcAudits.size() - o.pcAudits.size();
    }
}
