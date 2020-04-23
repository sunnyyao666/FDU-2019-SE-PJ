package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class InvitePCMemberRequest {
    private String[] invitee;
    private String conferenceFullName;

    public InvitePCMemberRequest(){
    }

    public String[] getInvitee() {
        return invitee;
    }

    public void setInvitee(String[] invitee) {
        this.invitee = invitee;
    }

    public String getConferenceFullName() {
        return conferenceFullName;
    }

    public void setConferenceFullName(String conferenceFullName) {
        this.conferenceFullName = conferenceFullName;
    }
}
