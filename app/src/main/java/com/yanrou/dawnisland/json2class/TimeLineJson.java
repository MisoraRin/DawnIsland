package com.yanrou.dawnisland.json2class;

import android.text.Spanned;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

@SuppressWarnings("unused")
public class TimeLineJson {
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TimeLineJson) {
            TimeLineJson timeLineJson = (TimeLineJson) obj;
            Log.d(TAG, "equals: " + timeLineJson.getId() + " " + id);
            return id.equals(timeLineJson.getId());
        }
        return false;
    }


    @Override
    public int hashCode() {
        Log.d(TAG, "hashCode: ");
        return id.hashCode();
    }


    /**
     * id : 23456670
     * fid : 111
     * img :
     * ext :
     * now : 2020-02-06(四)15:10:39
     * userid : cACHCEf
     * name : 无名氏
     * email :
     * title : 无标题
     * content : 晾在天台的衣服不见了(((　ﾟдﾟ)))<br />
     * 气死我了，我一定要抓到那个偷我裤袜的变态(╬ﾟдﾟ)
     * sage : 0
     * admin : 0
     * replys : [{"id":"23465807","img":"","ext":"","now":"2020-02-06(四)20:55:28","userid":"8hliYc4","name":"无名氏","email":"","title":"无标题","content":"哥哥朋友有什么回复吗","sage":"0","admin":"0","status":"n"},{"id":"23465817","img":"","ext":"","now":"2020-02-06(四)20:55:48","userid":"cNqKsOS","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.23465681<\/font><br />\n(￣皿￣)⊂彡☆))д`)","sage":"0","admin":"0","status":"n"},{"id":"23465825","img":"","ext":"","now":"2020-02-06(四)20:56:09","userid":"cACHCEf","name":"无名氏","email":"","title":"无标题","content":"有一说一，大臭猪朋友的声音真好听( ´ρ`)<br />\n从他卧室里的通讯录翻出的电话号码，是正确的真是太好了(*´∀`)<br />\n听着就是年纪不大的美少女，估计还是JK，真好啊，难怪大臭猪这时候还要去她家玩(*ﾟ∀ﾟ*)<br />\n她问我要不要去接哥哥，一想到那家伙满身酒气的样子，我就有点想拒绝( ` ・´)","sage":"0","admin":"0","status":"n"},{"id":"23465831","img":"","ext":"","now":"2020-02-06(四)20:56:17","userid":"cNqKsOS","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.23465747<\/font><br />\n去出门找哥哥","sage":"0","admin":"0","status":"n"},{"id":"23465853","img":"","ext":"","now":"2020-02-06(四)20:57:08","userid":"cNqKsOS","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.23465825<\/font><br />\n這jk 80%被你哥哥吃掉了(σﾟ∀ﾟ)σ","sage":"0","admin":"0","status":"n"}]
     * remainReplys : 271
     * replyCount : 276
     */


    @SerializedName("id")
    private String id;
    @SerializedName("fid")
    private int fid;
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
    @SerializedName("sage")
    private int sage;
    @SerializedName("admin")
    private int admin;
    @SerializedName("remainReplys")
    private int remainReplys;
    @SerializedName("replyCount")
    private int replyCount;
    @SerializedName("replys")
    private List<ReplysBean> replys;

    private Spanned spannedText;

    public Spanned getSpannedText() {
        return spannedText;
    }

    public void setSpannedText(Spanned spannedText) {
        this.spannedText = spannedText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
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

    public int getSage() {
        return sage;
    }

    public void setSage(int sage) {
        this.sage = sage;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getRemainReplys() {
        return remainReplys;
    }

    public void setRemainReplys(int remainReplys) {
        this.remainReplys = remainReplys;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public List<ReplysBean> getReplys() {
        return replys;
    }

    public void setReplys(List<ReplysBean> replys) {
        this.replys = replys;
    }

    public static class ReplysBean {
        /**
         * id : 23465807
         * img :
         * ext :
         * now : 2020-02-06(四)20:55:28
         * userid : 8hliYc4
         * name : 无名氏
         * email :
         * title : 无标题
         * content : 哥哥朋友有什么回复吗
         * sage : 0
         * admin : 0
         * status : n
         */

        @SerializedName("id")
        private String id;
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
        @SerializedName("sage")
        private String sage;
        @SerializedName("admin")
        private String admin;
        @SerializedName("status")
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getSage() {
            return sage;
        }

        public void setSage(String sage) {
            this.sage = sage;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
