package com.citifleet.view.main.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.CircleTransform;
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.util.PermissionUtil;
import com.citifleet.util.PrefUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.citifleet.view.login.LoginFlowActivity;
import com.citifleet.view.main.HelpFragment;
import com.citifleet.view.main.benefits.BenefitsFragment;
import com.citifleet.view.main.dashboard.DashboardPresenter.DashboardView;
import com.citifleet.view.main.docmanagement.DocManagementFragment;
import com.citifleet.view.main.legalaid.LegalAidFragment;
import com.citifleet.view.main.posting.PostingFragment;
import com.citifleet.view.main.profile.ProfileFragment;
import com.citifleet.view.main.settings.SettingsFragment;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import java.io.File;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class DashboardFragment extends BaseFragment implements DashboardView {
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
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String defaultErrorMes;
    @Bind(R.id.profileFullName)
    TextView fullName;
    private DashboardPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new DashboardPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cameraBtn)
    void onCameraBtnClick() {
        final String[] items = getResources().getStringArray(R.array.pick_image_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(pickImageTitle);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    checkPermissionsForCamera();
                } else if (item == 1) {
                    checkPermissionsForGallery();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void launchCamera() {
        Intent intent = new Intent(Constants.ACTION_PICK_IMAGE_CAMERA);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFileForProfileFromCamera()));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    private void checkPermissionsForGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_GALLERY);
        } else {
            launchGallery();
        }
    }

    private void checkPermissionsForCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CAMERA);
        } else {
            launchCamera();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    updateImageOnServer(CommonUtils.getImagePath(data.getData(), getContext()));
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    updateImageOnServer(getFileForProfileFromCamera().getAbsolutePath());
                }
                break;
        }
    }

    private void updateImageOnServer(String filepath) {
        presenter.uploadImage(filepath);
    }

    private File getFileForProfileFromCamera() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + "profile.png"); //TODO rename file?
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

    }

    @OnClick(R.id.chatBtn)
    void onChatBtnBtnClick() {

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
        ((BaseActivity) getActivity()).changeFragment(new HelpFragment(), true);
    }

    @OnClick(R.id.settingsBtn)
    void onSettingsBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new SettingsFragment(), true);
    }


    @OnClick(R.id.signOutBtn)
    void onSignoutBtnClick() {
        PrefUtil.clearAllPrefs(getActivity());
        logoutFromSocialNetworks();
        Intent i = new Intent(getActivity(), LoginFlowActivity.class);
        startActivity(i);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(broadcastIntent);

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
        if (!TextUtils.isEmpty(url) && getActivity()!=null) {
            int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
            int blurradius = (int) (screenWidth * outValue.getFloat());
            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    exception.printStackTrace();
                }
            });
            builder.build().load(url).transform(new CircleTransform(frameSize)).fit().centerCrop().into(profileImage);
          //  Picasso.with(getActivity()).load(url).transform(new CircleTransform(frameSize)).fit().centerCrop().into(profileImage);
            builder.build().load(url).transform(new BlurTransformation(getContext(), blurradius)).fit().centerCrop().into(bigProfileImage);
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
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted
                launchCamera();
            }
        } else if (requestCode == REQUEST_PERMISSION_GALLERY) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchGallery();
            }
        }
    }
}
