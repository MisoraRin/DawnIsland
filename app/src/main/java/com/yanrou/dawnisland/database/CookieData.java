package com.yanrou.dawnisland.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * @author suche
 */
public class CookieData extends LitePalSupport {
    @Column(nullable = false, unique = true)
    public String userHash;
    public String cookieName;
}
