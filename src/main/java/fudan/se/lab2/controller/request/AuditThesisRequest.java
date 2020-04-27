package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class AuditThesisRequest {
    private String conferenceFullName;
    private Long thesisID;
    private int score;
    private String comment;
    private String confidence;

    public AuditThesisRequest() {
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public Long getThesisID() {
        return thesisID;
    }

    public void setThesisID(Long thesisID) {
        this.thesisID = thesisID;
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
}
