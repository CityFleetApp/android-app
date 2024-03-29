package com.cityfleet.view.main.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.gcm.RegistrationIntentService;
import com.cityfleet.util.CircleFrameTransform;
import com.cityfleet.util.Constants;
import com.cityfleet.util.GcmRegistrationTypes;
import com.cityfleet.util.ImagePickerUtil;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.login.LoginFlowActivity;
import com.cityfleet.view.main.WebFragment;
import com.cityfleet.view.main.addfriends.AddFriendsFragment;
import com.cityfleet.view.main.benefits.BenefitsFragment;
import com.cityfleet.view.main.chat.ChatActivity;
import com.cityfleet.view.main.dashboard.DashboardPresenter.DashboardView;
import com.cityfleet.view.main.docmanagement.DocManagementFragment;
import com.cityfleet.view.main.legalaid.LegalAidFragment;
import com.cityfleet.view.main.posting.PostingFragment;
import com.cityfleet.view.main.profile.ProfileFragment;
import com.cityfleet.view.main.settings.SettingsFragment;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class DashboardFragment extends BaseFragment implements DashboardView, ImagePickerUtil.ImageResultListener {
    private static final int REQUEST_CAMERA = 111;
    private static final int SELECT_FILE = 222;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_GALLERY = 2;
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;
    @BindString(R.string.pick_profile_image_title)
    String pickImageTitle;
    @Bind(R.id.progressBar)
    RelativeLayout progressBar;
    @BindString(R.string.default_error_mes)
    String defaultErrorMes;
    @Bind(R.id.profileFullName)
    TextView fullName;
    private DashboardPresenter presenter;
    private ImagePickerUtil imagePickerUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new DashboardPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        imagePickerUtil = new ImagePickerUtil(this, this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cameraBtn)
    void onCameraBtnClick() {
        imagePickerUtil.onImageClick();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePickerUtil.isImagePickerRequestResultCode(requestCode)) {
            imagePickerUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateImageOnServer(String filepath) {
        presenter.uploadImage(filepath);
    }


    @OnClick(R.id.homeBtn)
    void onHomeBtnClick() {
        ((BaseActivity) getActivity()).onBackPressed();
    }

    @OnClick(R.id.profileBtn)
    void onProfileBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new ProfileFragment(), true);
    }

    @OnClick(R.id.postingBtn)
    void onPostingBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new PostingFragment(), true);
    }

    @OnClick(R.id.docManagementBtn)
    void onDocManagementBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new DocManagementFragment(), true);
    }

    @OnClick(R.id.inviteDriverBtn)
    void onInviteDriverBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new AddFriendsFragment(), true);
    }

    @OnClick(R.id.chatBtn)
    void onChatBtnBtnClick() {
        //  ((BaseActivity) getActivity()).changeFragment(new FriendsListTabbedFragment(), true);
        Intent i = new Intent(getActivity(), ChatActivity.class);
        getActivity().startActivity(i);
    }

    @OnClick(R.id.benefitsBtn)
    void onBenefitsBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new BenefitsFragment(), true);
    }

    @OnClick(R.id.legalAidBtn)
    void onLegalAidBtnBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new LegalAidFragment(), true);
    }

    @OnClick(R.id.helpBtn)
    void onHelpBtnBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.help_faq),getString(R.string.endpoint) + Constants.HELP_URL_PATH ), true);
    }

    @OnClick(R.id.settingsBtn)
    void onSettingsBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new SettingsFragment(), true);
    }


    @OnClick(R.id.signOutBtn)
    void onSignoutBtnClick() {
        unregisterFromPushNotifications();
        PrefUtil.clearAllPrefs(getActivity());
        logoutFromSocialNetworks();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(broadcastIntent);
        Intent i = new Intent(getActivity(), LoginFlowActivity.class);
        startActivity(i);
    }

    private void unregisterFromPushNotifications() {
        Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
        intent.putExtra(Constants.GCM_REGISTRATION_TYPE_TAG, GcmRegistrationTypes.UNREGISTER);
        intent.putExtra(Constants.PREFS_TOKEN, PrefUtil.getToken(getActivity()));
        getActivity().startService(intent);
    }

    private void logoutFromSocialNetworks() {
        //logout from fb
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            LoginManager.getInstance().logOut();
        }
        //twitter
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
        //instagram
        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void startLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void stopLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    public void updateImage(String url) {
        if (!TextUtils.isEmpty(url) && getActivity() != null && profileImage != null) {
            int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
            int blurradius = (int) (screenWidth * outValue.getFloat());
            Picasso.with(getContext()).load(url).transform(new CircleFrameTransform(frameSize)).fit().centerCrop().into(profileImage);
            Picasso.with(getContext()).load(url).transform(new BlurTransformation(getContext(), blurradius)).fit().centerCrop().into(bigProfileImage);
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void setName(String name) {
        if (fullName != null) {
            fullName.setText(name);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (imagePickerUtil.isImagePickerPermissionResultCode(requestCode)) {
            imagePickerUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageReceived(String url) {
        updateImageOnServer(url);
    }

    @Override
    public void onImageCanceledOrFailed() {

    }
}
