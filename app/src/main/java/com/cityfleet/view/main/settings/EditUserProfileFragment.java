package com.cityfleet.view.main.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
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

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.UserEditInfo;
import com.cityfleet.util.CircleFrameTransform;
import com.cityfleet.util.Constants;
import com.cityfleet.util.EditUserCarEvent;
import com.cityfleet.util.ImagePickerUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

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
    private UserEditInfo userEditInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user_profile, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.edit_account));
        if (presenter == null) {
            presenter = new EditUserProfilePresenter(CityFleetApp.getInstance().getNetworkManager(), this);
            presenter.initInfo();
        }
        presenter.initImage();
        if (imagePickerUtil == null) {
            imagePickerUtil = new ImagePickerUtil(this, new ArrayList<ImageView>() {{
                add(profileImage);
            }}, getString(R.string.profile_image_name), this);
        }
        if (userEditInfo!=null && !TextUtils.isEmpty(userEditInfo.getCarMakeDisplay()) && !TextUtils.isEmpty(userEditInfo.getCarModelDisplay())) {
            drivesLbl.setText(userEditInfo.getCarMakeDisplay() + ", " + userEditInfo.getCarModelDisplay());
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.saveBtn)
    void onSaveBtnClick() {
        String bio = bioEt.getText().toString();
        if (!bio.equals(userEditInfo.getBio())) {
            userEditInfo.setBio(bio);
            userEditInfo.setBioChanged(true);
        }
        String username = usernameEt.getText().toString();
        if (!username.equals(userEditInfo.getUsername())) {
            if (username.length() > 0) {
                userEditInfo.setUsername(username);
                userEditInfo.setUsernameChanged(true);
            } else {
                Toast.makeText(getContext(), R.string.username_empty, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String phonenumber = phoneEt.getText().toString();
        if (!phonenumber.equals(userEditInfo.getPhone())) {
            if (!TextUtils.isEmpty(phonenumber) && PhoneNumberUtils.isGlobalPhoneNumber(phonenumber)) {
                userEditInfo.setPhone(phonenumber);
                userEditInfo.setPhoneChanged(true);
            } else {
                Toast.makeText(getContext(), R.string.phone_validation, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!userEditInfo.isBioChanged() && !userEditInfo.isPhoneChanged() && !userEditInfo.isUsernameChanged() && !userEditInfo.isCarMakeChanged() && !userEditInfo.isCarModelChanged()
                && !userEditInfo.isCarTypeChanged() && !userEditInfo.isCarYearChanged() && !userEditInfo.isCarColorChanged()) {
            Toast.makeText(getContext(), R.string.nothing, Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.updateUserInfo(userEditInfo);
    }

    @Subscribe(sticky = false)
    public void onEvent(EditUserCarEvent event) {
        userEditInfo = new UserEditInfo(event.getEditInfo());
    }


    @OnClick(R.id.drivesBtn)
    void onDrivesBtnClick() {
        if (userEditInfo != null) {
            EditUserCarFragment fragment = new EditUserCarFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.EDIT_USER_INFO_TAG, Parcels.wrap(userEditInfo));
            fragment.setArguments(bundle);
            ((BaseActivity) getActivity()).changeFragment(fragment, true);
        }
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
            Picasso.with(getActivity()).load(url).transform(new CircleFrameTransform(frameSize)).fit().centerCrop().into(profileImage);
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
    public void setUserInfo(UserEditInfo info) {
        this.userEditInfo = info;
        bioEt.setText(info.getBio());
        phoneEt.setText(info.getPhone());
        usernameEt.setText(info.getUsername());
        if (!TextUtils.isEmpty(info.getCarMakeDisplay()) && !TextUtils.isEmpty(info.getCarModelDisplay())) {
            drivesLbl.setText(info.getCarMakeDisplay() + ", " + info.getCarModelDisplay());
        }
    }

    @Override
    public void onInfoUpdateSuccessfully() {
        getActivity().onBackPressed();
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
