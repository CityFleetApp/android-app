package com.citifleet.view.main.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.util.PermissionUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by vika on 14.03.16.
 */
public class SettingsFragment extends BaseFragment {
    private final static int REQUEST_PERMISSION_SETTINGS = 1;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.brightnessSeekbar)
    SeekBar brightnessSeekbar;
    @Bind(R.id.displayLbl)
    TextView displayLbl;
    private int brightness;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.profile));
        setupSeekbar();
        return view;
    }

    private void setupSeekbar() {
        brightnessSeekbar.setMax(Constants.MAX_BRIGHTNESS);
        brightnessSeekbar.setKeyProgressIncrement(1);
        try {
            brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightnessSeekbar.setProgress(brightness);
        } catch (Exception e) {
            brightnessSeekbar.setVisibility(View.GONE);
            displayLbl.setVisibility(View.GONE);
            Log.e(SettingsFragment.class.getName(), e.getMessage());
        }
        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                checkPermissionsForBrigtness();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress;
            }
        });
    }

    private void checkPermissionsForBrigtness() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, REQUEST_PERMISSION_SETTINGS);
        } else {
            changeBrightness();
        }
    }

    private void changeBrightness() {
        Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        WindowManager.LayoutParams layoutpars = getActivity().getWindow().getAttributes();
        layoutpars.screenBrightness = brightness / (float) Constants.MAX_BRIGHTNESS;
        getActivity().getWindow().setAttributes(layoutpars);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.profileBtn)
    void onProfileBtnClick() {

    }

    @OnClick(R.id.changePasswordBtn)
    void onChangePasswordBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new ChangePasswordFragment(), true);
    }

    @OnClick(R.id.displayBtn)
    void onDisplayBtnClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @OnClick(R.id.helpFaqBtn)
    void onHelpFaqBtnClick() {
    }

    @OnCheckedChanged(R.id.statusVisibleSwitch)
    void onCheckedStatusChange(boolean checked) {

    }

    @OnCheckedChanged(R.id.notificationSwitch)
    void onCheckedNotificationChange(boolean checked) {

    }

    @OnCheckedChanged(R.id.chatPrivacySwitch)
    void onChatPrivacyChange(boolean checked) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                changeBrightness();
            }
        }
    }
}
