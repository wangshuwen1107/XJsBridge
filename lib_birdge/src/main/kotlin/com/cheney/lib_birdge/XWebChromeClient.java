package com.cheney.lib_birdge;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by wangshuwen on 2017/5/15.
 */
public class XWebChromeClient extends WebChromeClient {

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (view instanceof XWebView) {
            XWebView webView = (XWebView) view;
            if (newProgress > 25) {
                webView.injectJS();
            }
        }
        super.onProgressChanged(view, newProgress);
    }


}
