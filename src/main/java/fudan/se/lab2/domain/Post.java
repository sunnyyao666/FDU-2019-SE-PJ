package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author YHT
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
