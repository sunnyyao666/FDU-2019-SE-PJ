package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class PostRequest {
    private Long thesisID;
    private String text;

    public PostRequest(){}

    public Long getThesisID() {
        return thesisID;
    }

    public void setThesisID(Long thesisID) {
        this.thesisID = thesisID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
