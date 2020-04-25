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

    private String title;


    @ManyToOne
    @JsonIgnore
    private User submitter;


    private String authors;

    private String conferenceFullName;

    @Lob
    @Column(columnDefinition = "text")
    private String summary;

    private String path;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "thesis")
    private Set<PCAudit> pcAudits = new HashSet<>();

    public Thesis() {
    }

    public Thesis(String title, User submitter, String conferenceFullName, String summary, String path,String authors) {
        this.title = title;
        this.submitter=submitter;
        this.conferenceFullName = conferenceFullName;
        this.summary = summary;
        this.authors=authors;

        this.path = path;
    }
    public User getSubmitter()
    {
        return submitter;
    }

    public String getAuthors()
    {
        return authors;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public void setSubmitter(User submitter)
    {
        this.submitter = submitter;
    }

    public void setAuthors(String authors)
    {
        this.authors = authors;
    }
}
