package com.yanrou.dawnisland.database;

//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Generated;

/**
 * @author suche
 */
//@Entity
public class ContentDAO {
    /**
     * 该回复所在的页数
     */
    private int page;

    /**
     * 该回复所在的页面的位置
     */
    private int posInPage;

    /**
     * 该回复所属的串
     */
    private String parentId;

    //下面为json数据中的成员

    private String seriesId;

    private String userid;

    private boolean admin;

    private String title;

    private String email;

    private String now;

    private String content;

    private String img;

    private String ext;

    private String name;

    private boolean sage;

  //    @Generated(hash = 995619909)
    public ContentDAO(int page, int posInPage, String parentId, String seriesId,
                      String userid, boolean admin, String title, String email, String now,
                      String content, String img, String ext, String name, boolean sage) {
        this.page = page;
        this.posInPage = posInPage;
        this.parentId = parentId;
        this.seriesId = seriesId;
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
    }

  //    @Generated(hash = 591922847)
    public ContentDAO() {
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPosInPage() {
        return this.posInPage;
    }

    public void setPosInPage(int posInPage) {
        this.posInPage = posInPage;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNow() {
        return this.now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSage() {
        return this.sage;
    }

    public void setSage(boolean sage) {
        this.sage = sage;
    }
}
