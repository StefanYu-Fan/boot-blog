package com.stefan.blog.model;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long conId;

    private Long comId;

    private Long byId;

    private Date commTime;

    private String children;

    private Integer upvote;

    private String comContent;

    //表中不存在字段时使用
    @Transient
    private com.stefan.blog.model.User user;

    @Transient
    private com.stefan.blog.model.User byUser;

    @Transient
    private List<Comment> comList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConId() {
        return conId;
    }

    public void setConId(Long conId) {
        this.conId = conId;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public Long getById() {
        return byId;
    }

    public void setById(Long byId) {
        this.byId = byId;
    }

    public Date getCommTime() {
        return commTime;
    }

    public void setCommTime(Date commTime) {
        this.commTime = commTime;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children == null ? null : children.trim();
    }

    public Integer getUpvote() {
        return upvote;
    }

    public void setUpvote(Integer upvote) {
        this.upvote = upvote;
    }

    public String getComContent() {
        return comContent;
    }

    public void setComContent(String comContent) {
        this.comContent = comContent == null ? null : comContent.trim();
    }

    public com.stefan.blog.model.User getUser() {
        return user;
    }

    public void setUser(com.stefan.blog.model.User user) {
        this.user = user;
    }

    public com.stefan.blog.model.User getByUser() {
        return byUser;
    }

    public void setByUser(com.stefan.blog.model.User byUser) {
        this.byUser = byUser;
    }

    public List<Comment> getComList() {
        return comList;
    }

    public void setComList(List<Comment> comList) {
        this.comList = comList;
    }

}