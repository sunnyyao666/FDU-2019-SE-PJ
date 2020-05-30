package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author YHT
 */
@Entity
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Thesis thesis;

    private Long thesisID;

    private String username;

    @Lob
    @Column(columnDefinition = "text")
    private String text;

    @Column(name = "CREATE_TIME", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Timestamp createTime;

    public Post() {
    }

    public Post(Thesis thesis, String username, String text) {
        this.thesis = thesis;
        this.thesisID = thesis.getId();
        this.username = username;
        this.text = text;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }
}
