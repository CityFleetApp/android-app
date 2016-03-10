package com.citifleet.view.main;

import android.util.Log;

import com.citifleet.network.NetworkManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InstagramLoginPresenter {
    private static final String GRANT_TYPE = "authorization_code";
    private NetworkManager     networkManager;
    private InstagramLoginView view;
    private String             clientId;
    private String             redirectUrl;
    private String             authUrl;
    private String             urlGetToken;
    private String             clientSecret;

    public InstagramLoginPresenter(NetworkManager networkManager, InstagramLoginView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init(String clientId, String redirectUrl, String authUrl, String urlGetToken, String clientSecret) {
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.authUrl = authUrl;
        this.urlGetToken = urlGetToken;
        this.clientSecret = clientSecret;
        String url = getUrl();
        view.loadUrl(url);
    }

    public boolean handleAuthorizationCode(String redirectedUrl) {
        Log.d(InstagramLoginPresenter.class.getName(), redirectedUrl);
        if (redirectedUrl.startsWith(redirectUrl)) {
            try {
                Map<String, List<String>> queryParams = splitQuery(redirectedUrl.substring(redirectedUrl.indexOf("?") + 1, redirectedUrl.length()));
                if (queryParams.containsKey("access_token")) {
                    view.onSuccessAuthorization(queryParams.get("access_token").toString());
                } else if (queryParams.containsKey("error")) {
                    //   handleFailureAuthorization(queryParams);
                }
                return true;
            } catch (UnsupportedEncodingException e) {
                view.onError(null);
            }
        }
        return false;
    }

    private void handleSuccessAuthorization(String token) {
        view.onSuccessAuthorization(token);
    }

    private void handleFailureAuthorization(Map<String, List<String>> queryParams) {
        //TODO
//        mView.onError(queryParams.get("error") + ":" + queryParams.get("error_description"));
    }


    private Map<String, List<String>> splitQuery(String queryParams) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = queryParams.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }

    private String getUrl() {
        HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("client_id", clientId);
        queryParams.put("redirect_uri", redirectUrl);
        queryParams.put("response_type", "token");
        String urlWithParams = authUrl + urlEncodeUTF8(queryParams);
        return urlWithParams;
    }

    private String urlEncodeUTF8(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    private String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public interface InstagramLoginView {
        void showProgress(boolean progress);

        void onError(String mes);

        void loadUrl(String url);

        void onSuccessAuthorization(String token);

        void onNetworkError();
    }
}
