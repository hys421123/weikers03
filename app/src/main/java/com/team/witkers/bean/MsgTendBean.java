package com.team.witkers.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by hys on 2016/8/29.
 */
//动态消息bean,从TendComments里获取出来，不需要上传
public class MsgTendBean {

    private TendItems tend;//用作点击链接的tend
    private String commentHeadUrl;//评论人头像
    private String commentUserName;
    //    private boolean isComment;//判断是评论还是点赞,若为评论则为true
    private String commentContent;
    private String commentTime;//评论产生的时间



    public TendItems getTend() {
        return tend;
    }

    public void setTend(TendItems tend) {
        this.tend = tend;
    }

    public String getCommentHeadUrl() {
        return commentHeadUrl;
    }

    public void setCommentHeadUrl(String commentHeadUrl) {
        this.commentHeadUrl = commentHeadUrl;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }


}
