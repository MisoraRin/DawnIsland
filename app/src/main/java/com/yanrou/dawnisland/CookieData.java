package com.yanrou.dawnisland;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * @author suche
 */
public class CookieData extends LitePalSupport {
    @Column(nullable = false, unique = true)
    String userHash;
    String cookieName;
}
