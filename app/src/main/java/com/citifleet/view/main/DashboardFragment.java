package com.citifleet.view.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.CircleTransform;
import com.citifleet.util.CommonUtils;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.main.DashboardPresenter.DashboardView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class DashboardFragment extends Fragment implements DashboardView {
    private static final int REQUEST_CAMERA = 111;
    private static final int SELECT_FILE    = 222;
    @Bind(R.id.profileImage)
    ImageView   profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView   bigProfileImage;
    @BindString(R.string.pick_image_title)
    String      pickImageTitle;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String      defaultErrorMes;

    private DashboardPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
        int blurradius = (int) (screenWidth * outValue.getFloat());
        Picasso.with(getActivity()).load(R.drawable.testprofile).transform(new CircleTransform(frameSize)).into(profileImage);
        Picasso.with(getActivity()).load(R.drawable.testprofile).transform(new BlurTransformation(getContext(), blurradius)).into(bigProfileImage);
        presenter = new DashboardPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
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
                    launchCamera();
                } else if (item == 1) {
                    launchGallery();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void launchCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFileForProfileFromCamera()));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_FILE:
                updateImage(CommonUtils.getImagePath(data.getData(), getContext()));
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    updateImage(getFileForProfileFromCamera().getAbsolutePath());
                }
                break;
        }
    }

    private void updateImage(String filepath) {
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

    }

    @OnClick(R.id.postingBtn)
    void onPostingBtnClick() {

    }

    @OnClick(R.id.earningsBtn)
    void onEarningsBtnClick() {

    }

    @OnClick(R.id.inviteDriverBtn)
    void onInviteDriverBtnClick() {

    }

    @OnClick(R.id.chatBtn)
    void onChatBtnBtnClick() {

    }

    @OnClick(R.id.benefitsBtn)
    void onBenefitsBtnClick() {

    }

    @OnClick(R.id.legalAidBtn)
    void onLegalAidBtnBtnClick() {

    }

    @OnClick(R.id.helpBtn)
    void onHelpBtnBtnClick() {

    }

    @OnClick(R.id.signOutBtn)
    void onSignoutBtnClick() {

    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onImageUploadSuccess(String url) {
        int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
        int blurradius = (int) (screenWidth * outValue.getFloat());
        Picasso.with(getActivity()).load(url).transform(new CircleTransform(frameSize)).into(profileImage);
        Picasso.with(getActivity()).load(url).transform(new BlurTransformation(getContext(), blurradius)).into(bigProfileImage);
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }
}
