package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author YHT
 */
@Entity
public class Conference implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String fullName;

    private String abbreviation;
    private String place;
    private Date startDate;
    private Date endDate;
    private Date deadline;
    private Date releaseTime;
    private String topics;
    private boolean applying; //是否在申请
    private boolean valid; //是否通过审核
    private boolean submitting; //是否开放投稿
    private boolean auditing; //是否开放审稿
    private boolean rechanging1;//是否开放第一次讨论
    private boolean rebutting;//是否开放rebut
    private boolean rechanging2;//是否开放第二次讨论

    @ManyToOne
    @JsonIgnore
    private User creator;

    private String creatorName;

    public Conference() {
    }

    public Conference(String abbreviation, String fullName, String place, Date startDate, Date endDate, Date deadline, Date releaseTime, String topics, User creator) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deadline = deadline;
        this.releaseTime = releaseTime;
        this.topics = topics;
        this.creator = creator;
        this.creatorName = creator.getUsername();
        this.applying = true;
        this.valid = false;
        this.submitting = false;
        this.auditing = false;
        this.rechanging1 = false;
        this.rebutting = false;
        this.rechanging2 = false;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
        this.creatorName = creator.getUsername();
    }

    public String getCreatorName() {
        return creatorName;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public boolean isApplying() {
        return applying;
    }

    public void setApplying(boolean applying) {
        this.applying = applying;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isSubmitting() {
        return submitting;
    }

    public void setSubmitting(boolean submitting) {
        this.submitting = submitting;
    }

    public boolean isAuditing() {
        return auditing;
    }

    public void setAuditing(boolean auditing) {
        this.auditing = auditing;
    }

    public boolean isRechanging1() {
        return rechanging1;
    }

    public void setRechanging1(boolean rechanging1) {
        this.rechanging1 = rechanging1;
    }

    public boolean isRebutting() {
        return rebutting;
    }

    public void setRebutting(boolean rebutting) {
        this.rebutting = rebutting;
    }

    public boolean isRechanging2() {
        return rechanging2;
    }

    public void setRechanging2(boolean rechanging2) {
        this.rechanging2 = rechanging2;
    }
}
