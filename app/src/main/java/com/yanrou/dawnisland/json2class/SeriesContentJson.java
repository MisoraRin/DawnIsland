package com.yanrou.dawnisland.json2class;

import com.google.gson.annotations.SerializedName;
import com.yanrou.dawnisland.database.List2String;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")

public class SeriesContentJson extends LitePalSupport {
    /**
     * 未启用回复
     */
    private final static int NONE = 0;
    /**
     * 订阅po更新数量
     */
    private final static int NEW_PO = 1;
    /**
     * 订阅po更新字数
     */
    private final static int NEW_PO_WORD_COUNT = 2;
    /**
     * 订阅有新回复
     */
    private final static int NEW_REPLY = 3;
    /**
     * 支持重设po的饼干，用来追更、只看po
     * 第一个位置是串首的饼干
     */
    @NotNull
    @Convert(converter = List2String.class, columnType = String.class)
    private List<String> po;
    /**
     * 上次阅读位置
     */
    private int lastPage;

    /**
     * 上次的回复数量，用来简单的判断有无新回复
     */
    private int lastReplyCount;
    /**
     * 指明订阅类型
     */
    private int substate = NONE;
    /**
     * 板块id
     */
    private int forumId;

    /**
     * id : 18308337
     * fid : 120
     * img : 2019-05-21/5ce3d6bf91ac1
     * ext : .jpg
     * now : 2019-05-31(五)21:13:52
     * userid : Mqc7d2u
     * name : 无名氏
     * email :
     * title : 无标题
     * content : (*ﾟーﾟ)<br />
     * <br />
     * 我突然有了一个在围炉开分酒馆的馊主意。<br />
     * 心动不如行动，说不能给跑团引流呢对不对？<br />
     * <br />
     * 撒，今天也照常开业咯！
     * sage : 0
     * admin : 0
     * replys : [{"id":9999999,"userid":"芦苇","admin":1,"title":"广告","email":"","now":"2099-01-01 00:00:01","content":"新疆艾尼大叔牛肉干，新货刚到，5种口味（香辣 芝麻 麻辣 孜然 葱香）任选，<a href=\"https://item.taobao.com/item.htm?id=555328713626\">点这里或淘宝搜索店铺：蹦蹦果，下单联系客服报暗号\u201cA岛\u201d有优惠哦~<\/a>","img":"2019-04-02/5ca33c8ea5ec7","ext":".jpg"},{"id":"18308394","img":"","ext":"","now":"2019-05-31(五)21:16:51","userid":"a6pLrFj","name":"无名氏","email":"","title":"无标题","content":"我认得这个饼干( ´_ゝ`)","sage":"0","admin":"0","status":"n"},{"id":"18308476","img":"","ext":"","now":"2019-05-31(五)21:21:49","userid":"7AVqj0D","name":"无名氏","email":"","title":"无标题","content":"(｀･ω･)老板，来一杯宫廷玉液酒","sage":"0","admin":"0","status":"n"},{"id":"18308521","img":"2019-05-22/5ce509d496053","ext":".jpg","now":"2019-05-31(五)21:24:47","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308394<\/font><br />\r\n开业五分钟就被饼干侦探逮了个正着还行。<br />\r\n酒馆角落有游戏机，如果想消遣时间就去玩塞○达可以吗？","sage":"0","admin":"0","status":"n"},{"id":"18308529","img":"","ext":"","now":"2019-05-31(五)21:25:17","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308476<\/font><br />\r\n<br />\r\n【二锅头兑水】<br />\r\n您好，一百八一杯。","sage":"0","admin":"0","status":"n"},{"id":"18308592","img":"","ext":"","now":"2019-05-31(五)21:29:37","userid":"BVwvUNg","name":"无名氏","email":"","title":"无标题","content":"老板，有那个嘛？就是那个东西\u2026( ﾟ∀。)","sage":"0","admin":"0","status":"n"},{"id":"18308630","img":"2019-05-31/5cf12cef1fb72","ext":".jpg","now":"2019-05-31(五)21:32:31","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308592<\/font><br />\r\n<br />\r\n(╬ﾟдﾟ)你说这个谁懂啊？","sage":"0","admin":"0","status":"n"},{"id":"18308631","img":"","ext":"","now":"2019-05-31(五)21:32:31","userid":"RAJ2GIG","name":"无名氏","email":"","title":"无标题","content":"( ﾟ∀。)","sage":"0","admin":"0","status":"n"},{"id":"18308646","img":"","ext":"","now":"2019-05-31(五)21:33:39","userid":"rR56rMM","name":"无名氏","email":"","title":"无标题","content":"老板，来一份82年的可乐，正宗的，不要洁厕灵( ﾟ∀ﾟ)","sage":"0","admin":"0","status":"n"},{"id":"18308653","img":"","ext":"","now":"2019-05-31(五)21:33:58","userid":"l1WCtrg","name":"无名氏","email":"","title":"无标题","content":"一碟茴香豆","sage":"0","admin":"0","status":"n"},{"id":"18308746","img":"","ext":"","now":"2019-05-31(五)21:42:28","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308646<\/font><br />\r\n<br />\r\n不要厕洁灵？那就是要洗脚水咯？(　^ω^)<br />\r\n<br />\r\n【递出一瓶包装磨损的不成样子的可乐，看不出来是○事还是可○】<br />\r\n诺。<br />\r\n<br />\r\n<br />\r\n<font color=\"#789922\">&gt;&gt;No.18308653<\/font><br />\r\n<br />\r\n我就知道...<br />\r\n都在锅里，还热着呢。<br />\r\n<br />\r\n【盛出一勺】<br />\r\n<br />\r\n就是蚕豆，只是加了八角和茴香。","sage":"0","admin":"0","status":"n"},{"id":"18308764","img":"","ext":"","now":"2019-05-31(五)21:43:58","userid":"J8rWcTI","name":"无名氏","email":"","title":"无标题","content":"来杯红茶","sage":"0","admin":"0","status":"n"},{"id":"18308795","img":"","ext":"","now":"2019-05-31(五)21:46:23","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308764<\/font><br />\r\n<br />\r\n嗯，好。<br />\r\n<br />\r\n【烧水泡茶一气呵成】<br />\r\n【倒茶的时候手却僵住了】<br />\r\n<br />\r\n哦，我懂了。<br />\r\n(　^ω^)宁要不要来点助眠粉加到茶里呀？","sage":"0","admin":"0","status":"n"},{"id":"18308899","img":"","ext":"","now":"2019-05-31(五)21:53:35","userid":"hiSg7M8","name":"无名氏","email":"","title":"无标题","content":"兔女郎呢？没有兔女郎的酒馆还有开的必要吗？","sage":"0","admin":"0","status":"n"},{"id":"18309042","img":"","ext":"","now":"2019-05-31(五)22:03:23","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18308899<\/font><br />\r\n<br />\r\n你！<br />\r\n你你你...【恼羞】<br />\r\n<br />\r\n...对了！<br />\r\n<br />\r\n<br />\r\n(　^ω^)兔女郎就在隔壁冒险者协会【<font color=\"#789922\">&gt;&gt;No.18271159<\/font> 】，那边长腿兔女郎可是大大滴有哦？","sage":"0","admin":"0","status":"n"},{"id":"18309046","img":"","ext":"","now":"2019-05-31(五)22:03:47","userid":"gmKlgne","name":"无名氏","email":"","title":"无标题","content":"一杯傻风牌烧仙草","sage":"0","admin":"0","status":"n"},{"id":"18309166","img":"2019-05-31/5cf1368ba7f88","ext":".png","now":"2019-05-31(五)22:13:31","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18309046<\/font><br />\r\n<br />\r\n烧仙草对吧，没问题...<br />\r\n只不过为什么一定要傻风牌？<br />\r\n<br />\r\n【凉粉赤小豆蜜红豆，还有牛奶和仙草冻。】<br />\r\n<br />\r\n搞定。<br />\r\n【取一个勺子，但却悟到了什么。】<br />\r\n...嘶<br />\r\n我又懂了<br />\r\n<br />\r\n(　^ω^)请问宁要下几粒珍珠？几半花生？几粒葡萄干呢？<br />\r\n是不是还要一个和别人不一样的汤勺？","sage":"0","admin":"0","status":"n"},{"id":"18309204","img":"","ext":"","now":"2019-05-31(五)22:15:56","userid":"02Bt2RZ","name":"无名氏","email":"","title":"无标题","content":"来一个说书先生( ﾟ∀ﾟ)","sage":"0","admin":"0","status":"n"},{"id":"18309270","img":"2019-05-24/5ce7bd3794d93","ext":".jpg","now":"2019-05-31(五)22:21:26","userid":"sUZmtJe","name":"无名氏","email":"","title":"无标题","content":"高达，是敌人","sage":"0","admin":"0","status":"n"},{"id":"18309275","img":"","ext":"","now":"2019-05-31(五)22:21:48","userid":"Mqc7d2u","name":"无名氏","email":"","title":"无标题","content":"<font color=\"#789922\">&gt;&gt;No.18309204<\/font><br />\r\n<br />\r\n说书唱戏劝人方，三条大路走中央。<br />\r\n善恶到头终有报，人间正道是·沧·桑！<br />\r\n【吧啦吧啦】<br />\r\n欲知后事如何？且听下回分解！","sage":"0","admin":"0","status":"n"}]
     * replyCount : 5303
     */

    @SerializedName("id")
    private String seriesId;
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
    @SerializedName("replyCount")
    private int replyCount;
    @SerializedName("replys")
    @Transient
    private List<ReplysBean> replys = new ArrayList<>();

    @Generated(hash = 2083512233)
    public SeriesContentJson(@NotNull List<String> po, int lastPage, int lastReplyCount, int substate, int forumId, String seriesId, int fid, String img, String ext, String now, String userid, String name, String email, String title, String content, int sage, int admin, int replyCount) {
        this.po = po;
        this.lastPage = lastPage;
        this.lastReplyCount = lastReplyCount;
        this.substate = substate;
        this.forumId = forumId;
        this.seriesId = seriesId;
        this.fid = fid;
        this.img = img;
        this.ext = ext;
        this.now = now;
        this.userid = userid;
        this.name = name;
        this.email = email;
        this.title = title;
        this.content = content;
        this.sage = sage;
        this.admin = admin;
        this.replyCount = replyCount;
    }
    @Generated(hash = 1367723520)
    public SeriesContentJson() {
    }

    public List<String> getPo() {
        return this.po;
    }

    public void setPo(List<String> po) {
        this.po = po;
    }

    public int getLastPage() {
        return this.lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getLastReplyCount() {
        return this.lastReplyCount;
    }

    public void setLastReplyCount(int lastReplyCount) {
        this.lastReplyCount = lastReplyCount;
    }

    public int getSubstate() {
        return this.substate;
    }

    public void setSubstate(int substate) {
        this.substate = substate;
    }

    public int getForumId() {
        return this.forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }
    public String getSeriesId() {
        return this.seriesId;
    }
    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public int getFid() {
        return this.fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getExt() {
        return this.ext;
    }
    public void setExt(String ext) {
        this.ext = ext;
    }
    public String getNow() {
        return this.now;
    }
    public void setNow(String now) {
        this.now = now;
    }
    public String getUserid() {
        return this.userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getSage() {
        return this.sage;
    }
    public void setSage(int sage) {
        this.sage = sage;
    }
    public int getAdmin() {
        return this.admin;
    }
    public void setAdmin(int admin) {
        this.admin = admin;
    }
    public int getReplyCount() {
        return this.replyCount;
    }
    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public List<ReplysBean> getReplys() {
        return replys;
    }
}
