package fudan.se.lab2.controller.request;

import org.springframework.web.multipart.MultipartFile;

public class SubmitThesisRequest {
    private String conferenceFullName;
    private String title;
    private String summary;
    private MultipartFile file;

    public SubmitThesisRequest(){
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
