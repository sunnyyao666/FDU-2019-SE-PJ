package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class ConferenceApplyRequest {
    private String fullName;
    private String abbreviation;

    public ConferenceApplyRequest() {}


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
}
