package fudan.se.lab2.controller.request;

public class AuditPCRequest
{
        private String topics;
        private String conferenceFullName;

        public AuditPCRequest(){
        }

        public String getTopics() {
            return topics;
        }

        public void setTopics(String text) {
            this.topics = text;
        }

        public String getConferenceFullName() {
            return conferenceFullName;
        }

        public void setConferenceFullName(String conferenceFullName) {
            this.conferenceFullName = conferenceFullName;
        }
    }

