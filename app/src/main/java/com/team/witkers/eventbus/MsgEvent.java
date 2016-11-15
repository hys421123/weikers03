package com.team.witkers.eventbus;

import com.team.witkers.bean.TendComments;

import java.util.List;

/**
 * Created by hys on 2016/9/5.
 */
public class MsgEvent {
    private List<TendComments> commentsList;
    private boolean isClearMsg=false;

    public MsgEvent(List<TendComments> commentsList){
        this.commentsList=commentsList;
    }
    public MsgEvent(boolean isClearMsg){
        this.isClearMsg=isClearMsg;
    }
    public List<TendComments> getCommentsList() {
        return commentsList;
    }

    public void setClearMsg(boolean clearMsg) {
        isClearMsg = clearMsg;
    }

    public boolean isClearMsg() {
        return isClearMsg;
    }
}
