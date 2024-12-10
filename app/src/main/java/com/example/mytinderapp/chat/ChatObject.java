package com.example.mytinderapp.chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private String messageId;

    public ChatObject(String message, Boolean currentUser, String messageId) {
        this.message = message;
        this.currentUser = currentUser;
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}



/*public class ChatObject {
    private String message;
    private boolean currentUser;

    public ChatObject(String message, boolean currentUser) {
        this.message = message;
        this.currentUser = currentUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean getcurrentUser() {
        return currentUser;
    }
}*/


