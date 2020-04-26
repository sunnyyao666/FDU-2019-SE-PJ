package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YHT
 */
@Entity
public class Thesis implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String conferenceFullName;
    private String title;

    @Lob
    @Column(columnDefinition = "text")
    private String summary;

    @ManyToOne
    @JsonIgnore
    private User submitter;
    private String authors;
    private String topics;
    private String path;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "thesis")
    private Set<PCAudit> pcAudits = new HashSet<>();

    public Thesis() {
    }

    public Thesis(String conferenceFullName, String title, String summary, User submitter, String authors, String topics, String path) {
        this.conferenceFullName = conferenceFullName;
        this.title = title;
        this.summary = summary;
        this.submitter = submitter;
        this.authors = authors;
        this.topics = topics;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getTopics() {
        return topics;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<PCAudit> getPcAudits() {
        return pcAudits;
    }

    public void setPcAudits(Set<PCAudit> pcAudits) {
        this.pcAudits = pcAudits;
    }
}
