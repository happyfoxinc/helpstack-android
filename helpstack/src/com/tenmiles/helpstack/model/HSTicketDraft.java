package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anirudh on 19/11/14.
 */
public class HSTicketDraft implements Serializable {

    @SerializedName("draft_subject")
    private String draftSubject;

    @SerializedName("draft_message")
    private String draftMessage;

    @SerializedName("draft_attachments")
    private HSAttachment[] draftAttachments;

    public HSTicketDraft(String subject, String message, HSAttachment[] attachmentsArray) {
        this.draftSubject = subject;
        this.draftMessage = message;
        this.draftAttachments = attachmentsArray;
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
}
