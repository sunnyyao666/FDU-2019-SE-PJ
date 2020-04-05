package fudan.se.lab2.controller.request;

public class AuditApplicationRequest {
    private String conferenceFullName;
    private boolean passed;

    public AuditApplicationRequest(){}

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
