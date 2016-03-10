package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class InstagramWebFragment extends Fragment implements InstagramLoginPresenter.InstagramLoginView {
    @Bind(R.id.webView)
    WebView     webView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.instagram_client_id)     String                  clientId;
    @BindString(R.string.instagram_client_secret) String                  clientSecret;
    @BindString(R.string.instagram_url)           String                  authUrl;
    @BindString(R.string.instagram_redirect_url)  String                  redirectUrl;
    @BindString(R.string.instagram_url_get_token) String                  urlGetToken;
    private                                       InstagramLoginPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instagram_web_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new InstagramLoginPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init(clientId, redirectUrl, authUrl, urlGetToken, clientSecret);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return presenter.handleAuthorizationCode(url);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showProgress(boolean progress) {
        webView.setVisibility(View.GONE);
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onError(String mes) {
        if (TextUtils.isEmpty(mes)) {
            mes = getString(R.string.default_error_mes);
        }
        Log.e(InstagramWebFragment.class.getName(), mes);
        Toast.makeText(getActivity(), mes, Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadUrl(String url) {
        Log.d(InstagramWebFragment.class.getName(), url);
        webView.loadUrl(url);
    }

    @Override
    public void onSuccessAuthorization(String token) {
        Log.d("TAG", "token");
        progressBar.setVisibility(View.GONE);
        //TODO   VeritasApp.mBus.post(new LinkedInUserUpdatedEvent());
        //  getActivity().onBackPressed();
    }

    @Override
    public void onNetworkError() {
        onError(getString(R.string.networkMesMoInternet));
    }
}
