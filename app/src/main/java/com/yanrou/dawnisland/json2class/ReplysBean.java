package com.yanrou.dawnisland.json2class;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class ReplysBean extends LitePalSupport {
    /**
     * id : 9999999
     * userid : 芦苇
     * admin : 1
     * title : 广告
     * email :
     * now : 2099-01-01 00:00:01
     * content : 新疆艾尼大叔牛肉干，新货刚到，5种口味（香辣 芝麻 麻辣 孜然 葱香）任选，<a href="https://item.taobao.com/item.htm?id=555328713626">点这里或淘宝搜索店铺：蹦蹦果，下单联系客服报暗号“A岛”有优惠哦~</a>
     * img : 2019-04-02/5ca33c8ea5ec7
     * ext : .jpg
     * name : 无名氏
     * sage : 0
     * status : n
     */

    @SerializedName("id")
    private String seriesId;
    @SerializedName("userid")
    private String userid;
    @SerializedName("admin")
    private int admin;
    @SerializedName("title")
    private String title;
    @SerializedName("email")
    private String email;
    @SerializedName("now")
    private String now;
    @SerializedName("content")
    private String content;
    @SerializedName("img")
    private String img;
    @SerializedName("ext")
    private String ext;
    @SerializedName("name")
    private String name;
    @SerializedName("sage")
    private int sage;
    @SerializedName("status")
    private String status;

    public ReplysBean(String id, String userid, int admin, String title, String email, String now, String content, String img, String ext, String name, int sage) {
        this.seriesId = id;
        this.userid = userid;
        this.admin = admin;
        this.title = title;
        this.email = email;
        this.now = now;
        this.content = content;
        this.img = img;
        this.ext = ext;
        this.name = name;
        this.sage = sage;
        this.status = "n";
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSage() {
        return sage;
    }

    public void setSage(int sage) {
        this.sage = sage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
