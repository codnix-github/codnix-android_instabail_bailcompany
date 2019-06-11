package com.bailcompany.events;

public class NotificationReceived {
    // Event used to send message from activity to activity.
    public static class NotificationReceivedEvent {

        private String message;
        private String type;
        public NotificationReceivedEvent(String message,String type) {
            this.message = message;
            this.type = type;
        }
        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
