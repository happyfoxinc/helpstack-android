package com.tenmiles.helpstack.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anirudh on 19/11/14.
 */
public class HSUserDraft implements Serializable {
    @SerializedName("draft_user")
    private HSUser draftUser;

    public HSUserDraft(HSUser user) {
        this.draftUser = user;
    }

    public HSUser getUser() {
        return draftUser;
    }
}
