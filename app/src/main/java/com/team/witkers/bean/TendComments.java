package com.team.witkers.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by hys on 2016/8/10.
 */
public class TendComments extends BmobObject{

    private String commentContent;//评论内容
    private String commentUserName;//评论用户名
    private String commentUserHead;//评论用户头像
    private String commentPubtime;//评论时间
    private MyUser tendUser;//发动态的用户
    private TendItems tendItems;//评论所针对的动态

    public void setTendUser(MyUser tendUser) {
        this.tendUser = tendUser;
    }

    public MyUser getTendUser() {
        return tendUser;
    }

    public void setTendItems(TendItems tendItems) {
        this.tendItems = tendItems;
    }

    public TendItems getTendItems() {
        return tendItems;
    }

    public String getCommentUserHead() {
        return commentUserHead;
    }

    public void setCommentUserHead(String commentUserHead) {
        this.commentUserHead = commentUserHead;
    }

    public String getCommentPubtime() {
        return commentPubtime;
    }

    public void setCommentPubtime(String commentPubtime) {
        this.commentPubtime = commentPubtime;
    }




    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }




}
