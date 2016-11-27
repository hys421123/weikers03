package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by hys on 2016/11/28.
 */

public class LikePerson implements Serializable {
    private String likeName;
    private boolean isLiked;

    public String getLikeName() {
        return likeName;
    }

    public void setLikeName(String likeName) {
        this.likeName = likeName;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public LikePerson(String likeName, boolean isLiked) {
        this.likeName = likeName;
        this.isLiked = isLiked;
    }
}
