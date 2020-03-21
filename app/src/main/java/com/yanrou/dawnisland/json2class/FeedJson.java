package com.yanrou.dawnisland.json2class;

import com.google.gson.annotations.SerializedName;

/**
 * @author suche
 */
public class FeedJson {

    /**
     * id : 24609272
     * fid : 20
     * category :
     * img :
     * ext :
     * now : 2020-03-13(五)18:19:32
     * userid : OpeAe0k
     * name : 无名氏
     * email :
     * title : 无标题
     * content : 04.24，和朱由检去紫禁城办公，世界上最暖和的地方在乾清宫的暖阁。<br />
     * status : n
     * admin : 0
     */

    @SerializedName("id")
    private String id;
    @SerializedName("fid")
    private String fid;
    @SerializedName("category")
    private String category;
    @SerializedName("img")
    private String img;
    @SerializedName("ext")
    private String ext;
    @SerializedName("now")
    private String now;
    @SerializedName("userid")
    private String userid;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("status")
    private String status;
    @SerializedName("admin")
    private String admin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
