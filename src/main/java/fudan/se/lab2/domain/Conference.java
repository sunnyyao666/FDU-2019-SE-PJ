package fudan.se.lab2.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Conference implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String conferenceName;

    @ManyToOne
    private User creator;

    public Conference() {
    }

    public Conference(String conferenceName, User creator) {
        this.conferenceName = conferenceName;
        this.creator= creator;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public User getCreator() {
        return creator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
