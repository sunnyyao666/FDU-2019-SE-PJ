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

    public PCAudit(){
    }

    public PCAudit(Authority authority, Thesis thesis) {
        this.authority = authority;
        this.thesis = thesis;
        this.thesisID = thesis.getId();
        this.audited = false;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
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
}
