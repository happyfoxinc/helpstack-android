package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anirudh on 03/11/14.
 */
public class HSCachedDraft implements Serializable{

    @SerializedName("draft_subject")
    private String draftSubject;

    @SerializedName("draft_message")
    private String draftMessage;

    @SerializedName("draft_user")
    private HSUser draftUser;

    public HSCachedDraft(HSUser user, String subject, String message) {
        this.draftUser = user;
        this.draftSubject = subject;
        this.draftMessage = message;
    }

    public String getSubject() {
        return draftSubject;
    }

    public String getMessage() {
        return draftMessage;
    }

    public HSUser getUser() {
        return draftUser;
    }
}