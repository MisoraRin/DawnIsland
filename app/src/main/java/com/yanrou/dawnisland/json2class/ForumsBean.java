package com.yanrou.dawnisland.json2class;

import com.google.gson.annotations.SerializedName;


public class ForumsBean {
    /**
     * id : -1
     * name : 时间线
     * msg : 这里是匿名版最新的串
     * fgroup : 4
     * sort : 2
     * showName :
     * interval : 60
     * createdAt : 2011-10-21 15:49:28
     * updateAt : 2015-06-23 17:26:28
     * status : n
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("msg")
    private String msg;
    @SerializedName("fgroup")
    private String fgroup;
    @SerializedName("sort")
    private String sort;
    @SerializedName("showName")
    private String showName;
    @SerializedName("interval")
    private String interval;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updateAt")
    private String updateAt;
    @SerializedName("status")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFgroup() {
        return fgroup;
    }

    public void setFgroup(String fgroup) {
        this.fgroup = fgroup;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
