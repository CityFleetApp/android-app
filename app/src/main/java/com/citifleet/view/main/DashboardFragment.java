package com.citifleet.view.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.CircleTransform;
import com.citifleet.view.BaseActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;

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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.cameraBtn)
    void onCameraBtnClick() {
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        File f = new File(getContext().getCacheDir(), "avatar");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.splash)).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f);

        Call<Object> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().uploadAvatar(requestBody, description);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Upload", t.getMessage());
            }
        });
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
}
