package com.yanrou.dawnisland.database;

/**
 * @author suche
 */
//public class CookieData extends LitePalSupport {
public class CookieData {
  //    @Column(nullable = false, unique = true)
    private String userHash;


    private String cookieName;

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

}
