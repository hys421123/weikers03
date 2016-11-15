package com.team.witkers.bean;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by hys on 2016/8/10.
 */
public class TendItems extends BmobObject implements Serializable{

    private MyUser pubUser;
    private String friendName;
    private String friendHeadUrl;
    private String content;
    private List<String> picUrlList;
    private Integer commentNum;
    private Integer likeNum;
    private boolean isLike=false;//判断用户本人是否点过赞,
//    private List<TendComments> commentsList;
    private String LikeName="0";//标记某用户是否点赞，若点赞留下用户名,默认无点赞字符串"0"
//    private BmobRelation likeUsers;//记录点赞的用户
    private String createTime="";

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setPubUser(MyUser pubUser) {
        this.pubUser = pubUser;
    }

    public MyUser getPubUser() {
        return pubUser;
    }

    //    public BmobRelation getLikeUsers() {
//        return likeUsers;
//    }
//
//    public void setLikeUsers(BmobRelation likeUsers) {
//        this.likeUsers = likeUsers;
//    }
    public String getLikeName() {
        return LikeName;
    }

    public void setLikeName(String likeName) {
        LikeName = likeName;
    }
    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public void setPicUrlList(List<String> picUrlList) {
        this.picUrlList = picUrlList;
    }
    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendHeadUrl() {
        return friendHeadUrl;
    }

    public void setFriendHeadUrl(String friendHeadUrl) {
        this.friendHeadUrl = friendHeadUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

//    public List<TendComments> getCommentsList() {
//        return commentsList;
//    }
//
//    public void setCommentsList(List<TendComments> commentsList) {
//        this.commentsList = commentsList;
//    }



}
