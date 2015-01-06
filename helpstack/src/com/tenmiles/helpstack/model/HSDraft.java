package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anirudh on 19/11/14.
 */
public class HSDraft implements Serializable {

    @SerializedName("draft_subject")
    private String draftSubject;

    @SerializedName("draft_message")
    private String draftMessage;

    @SerializedName("draft_attachments")
    private HSAttachment[] draftAttachments;

    @SerializedName("draft_user")
    private HSUser draftUser;

    @SerializedName("draft_reply_message")
    private String draftReplyMessage;

    @SerializedName("draft_reply_attachments")
    private HSAttachment[] draftReplyAttachments;

    public HSDraft() {

    }

    public String getSubject() {
        return draftSubject;
    }

    public String getMessage() {
        return draftMessage;
    }

    public HSAttachment[] getAttachments() {
        return draftAttachments;
    }

    public void setDraftSubject(String subject){
        this.draftSubject = subject;
    }

    public void setDraftMessage(String message) {
        this.draftMessage = message;
    }

    public HSUser getDraftUser() {
        return draftUser;
    }

    public String getDraftReplyMessage() {
        return draftReplyMessage;
    }

    public HSAttachment[] getDraftReplyAttachments() {
        return draftReplyAttachments;
    }

    public void setDraftAttachments(HSAttachment[] attachmentsArray) {
        this.draftAttachments = attachmentsArray;
    }

    public void setDraftUSer(HSUser user) {
        this.draftUser = user;
    }

    public void setDraftReplyMessage(String message) {
        this.draftReplyMessage = message;
    }

    public void setDraftReplyAttachments(HSAttachment[] attachments) {
        this.draftReplyAttachments = attachments;
    }
}
