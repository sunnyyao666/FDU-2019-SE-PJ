package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Conference implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String fullName;

    private String abbreviation;

    @ManyToOne
    @JsonIgnore
    private User creator;

    public Conference() {
    }

    public Conference(String fullName, User creator) {
        this.fullName = fullName;
        this.creator = creator;
    }

    public String getFullName() {
        return fullName;
    }

    public User getCreator() {
        return creator;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
