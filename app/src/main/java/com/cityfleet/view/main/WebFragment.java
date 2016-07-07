package com.cityfleet.view.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 18.03.16.
 */
public class WebFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private String path;
    private String toolbarTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();

        path = args.getString("path");
        toolbarTitle = args.getString("title");

        title.setText(toolbarTitle);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new FrameWebViewClient());
        webView.loadUrl(path);
        return view;
    }

    public static WebFragment getInstance(String title, String path){
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("path", path);
        webFragment.setArguments(args);
        return webFragment;
    }
    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class FrameWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
