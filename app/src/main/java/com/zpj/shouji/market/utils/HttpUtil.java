package com.zpj.shouji.market.utils;

import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;

import java.io.IOException;

public final class HttpUtil {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private HttpUtil() {

    }

    public static Document getDocument(String url) throws IOException {
        return ZHttp.get(url)
                .userAgent(USER_AGENT)
                .onRedirect(new IHttp.OnRedirectListener() {
                    @Override
                    public boolean onRedirect(String redirectUrl) {
                        Log.d("getDocument", "onRedirect redirectUrl=" + redirectUrl);
                        return true;
                    }
                })
                .cookie(UserManager.getCookie())
//                .header(HEADER_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING)
                .referrer(url)
                .ignoreContentType(true)
                .toHtml();
    }

}
