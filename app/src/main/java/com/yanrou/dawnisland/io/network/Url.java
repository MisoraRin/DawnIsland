package com.yanrou.dawnisland.io.network;

import rxhttp.wrapper.annotation.DefaultDomain;
import rxhttp.wrapper.annotation.Domain;

public class Url {
    @DefaultDomain() //设置为默认域名
    public static String baseUrl = "https://nmb.fastmirror.org/";

    @Domain(name = "Adao", className = "Adao")
    public static String adaoUrl = "https://nmb.fastmirror.org/";
}
