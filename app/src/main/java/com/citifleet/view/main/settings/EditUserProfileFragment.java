package com.citifleet.view.main.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.CircleTransform;
import com.citifleet.util.ImagePickerUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.mobsandgeeks.saripaar.Validator;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserProfileFragment extends BaseFragment implements EditUserProfilePresenter.EditUserProfileVIew, ImagePickerUtil.ImageResultListener {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;
    @Bind(R.id.profileFullName)
    TextView fullName;
    @Bind(R.id.bioEt)
    EditText bioEt;
    @Bind(R.id.drivesLbl)
    TextView drivesLbl;
    @Bind(R.id.usernameEt)
    EditText usernameEt;
    @Bind(R.id.phoneEt)
    EditText phoneEt;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private EditUserProfilePresenter presenter;
    private ImagePickerUtil imagePickerUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user_profile, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.edit_account));
        presenter = new EditUserProfilePresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        imagePickerUtil = new ImagePickerUtil(this, new ArrayList<ImageView>() {{
            add(profileImage);
        }}, getString(R.string.profile_image_name), this);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.saveBtn)
    void onSaveBtnClick() {

    }

    @OnClick(R.id.drivesBtn)
    void onDrivesBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new EditUserCarFragment(), true);
    }

    @OnClick(R.id.cameraBtn)
    void onCameraBtnClick() {
        imagePickerUtil.onImageClick(profileImage);
    }

    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void updateProfileImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
            int blurradius = (int) (screenWidth * outValue.getFloat());
            Picasso.with(getActivity()).load(url).transform(new CircleTransform(frameSize)).fit().centerCrop().into(profileImage);
            Picasso.with(getActivity()).load(url).transform(new BlurTransformation(getContext(), blurradius)).fit().centerCrop().into(bigProfileImage);
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
    public void setUserInfo() {

    }

    @Override
    public void onImageReceived(String url, int position) {
        presenter.uploadImage(url);
    }

    @Override
    public void onImageCanceledOrFailed(int position) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (imagePickerUtil.isImagePickerPermissionResultCode(requestCode)) {
            imagePickerUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePickerUtil.isImagePickerRequestResultCode(requestCode)) {
            imagePickerUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

}
