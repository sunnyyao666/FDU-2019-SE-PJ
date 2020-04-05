package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class SearchRequest {
    private String text;
    private String conferenceFullName;

    public SearchRequest(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }
}
