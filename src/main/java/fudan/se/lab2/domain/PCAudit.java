package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author YHT
 */
@Entity
public class PCAudit implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Authority authority;

    private String username;

    @ManyToOne
    @JsonIgnore
    private Thesis thesis;

    private Long thesisID;
    private int score;

    @Lob
    @Column(columnDefinition = "text")
    private String comment;

    private String confidence;
    private boolean audited;
    private boolean rechanged1;
    private boolean rechanged2;

    public PCAudit() {
    }

    public PCAudit(Authority authority, Thesis thesis) {
        this.authority = authority;
        this.username = authority.getUsername();
        this.thesis = thesis;
        this.thesisID = thesis.getId();
        this.audited = false;
        this.rechanged1 = false;
        this.rechanged2 = false;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
        this.username = authority.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public Thesis getThesis() {
        return thesis;
    }

    public void setThesis(Thesis thesis) {
        this.thesis = thesis;
        this.thesisID = thesis.getId();
    }

    public Long getThesisID() {
        return thesisID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public boolean isAudited() {
        return audited;
    }

    public void setAudited(boolean auditing) {
        this.audited = auditing;
    }

    public boolean isRechanged1() {
        return rechanged1;
    }

    public void setRechanged1(boolean rechanged1) {
        this.rechanged1 = rechanged1;
    }

    public boolean isRechanged2() {
        return rechanged2;
    }

    public void setRechanged2(boolean rechanged2) {
        this.rechanged2 = rechanged2;
    }
}
