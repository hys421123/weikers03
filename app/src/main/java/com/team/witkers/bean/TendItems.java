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

    private boolean isLike=false;//判断用户本人是否点过赞,


    // 与点赞 相关的 List, list中存储 的是 点赞者的 userName
    private List<String> likeList;

    public void setLikeList(List<String> likeList) {
        this.likeList = likeList;
    }

    public List<String> getLikeList() {
        return likeList;
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

//    public List<TendComments> getCommentsList() {
//        return commentsList;
//    }
//
//    public void setCommentsList(List<TendComments> commentsList) {
//        this.commentsList = commentsList;
//    }



}
