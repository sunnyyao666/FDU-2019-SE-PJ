package fudan.se.lab2.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author YHT
 */
public class ApplyConferenceRequest {
    private String fullName;
    private String abbreviation;
    private String place;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseTime;

    private String topics;

    public ApplyConferenceRequest() {
    }

    public ApplyConferenceRequest(String abbreviation, String fullName, String place, Date startDate, Date endDate, Date releaseTime, Date deadline, String topics) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.releaseTime = releaseTime;
        this.deadline = deadline;
        this.topics = topics;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
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
}
