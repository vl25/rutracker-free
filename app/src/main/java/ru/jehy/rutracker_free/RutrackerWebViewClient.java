package ru.jehy.rutracker_free;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


class RutrackerWebViewClient extends WebViewClient {

    private ProxyProcessor proxy = null;
    private Context activityContext;

    public RutrackerWebViewClient(Context c) {
        activityContext = c;
        proxy = new ProxyProcessor(c);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return false;//avoid double work
        if (!url.startsWith("magnet:"))
            view.loadUrl(url);
        else if (url.contains("dl.php?t="))
            proxy.getWebResourceResponse(Uri.parse(url), "GET", null);
        else
            ((MainActivity) activityContext).shareMagnet();
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (!request.getUrl().toString().startsWith("magnet:"))
            view.loadUrl(request.getUrl().toString());
        else if (request.getUrl().toString().contains("dl.php?t="))
            proxy.getWebResourceResponse(Uri.parse(request.getUrl().toString()), "GET", null);
        else
            ((MainActivity) activityContext).shareMagnet();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse response = proxy.getWebResourceResponse(request.getUrl(), request.getMethod(), request.getRequestHeaders());
        if (response == null) {
            return super.shouldInterceptRequest(view, request);
        } else {
            return response;
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return null;//avoid double work
        WebResourceResponse response = proxy.getWebResourceResponse(Uri.parse(url), "GET", null);
        if (response == null) {
            return super.shouldInterceptRequest(view, url);
        } else {
            return response;
        }
    }

}