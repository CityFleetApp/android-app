package com.cityfleet.view.main.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.util.Constants;
import com.cityfleet.util.PermissionUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.WebFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by vika on 14.03.16.
 */
public class SettingsFragment extends BaseFragment implements SettingsPresenter.SettingsView {
    private final static int REQUEST_PERMISSION_SETTINGS = 1;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.brightnessSeekbar)
    SeekBar brightnessSeekbar;
    @Bind(R.id.displayLbl)
    TextView displayLbl;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.statusVisibleSwitch)
    SwitchCompat statusVisibleSwitch;
    @Bind(R.id.notificationSwitch)
    SwitchCompat notificationSwitch;
    @Bind(R.id.chatPrivacySwitch)
    SwitchCompat chatPrivacySwitch;
    private int brightness;
    private SettingsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.settings));
        setupSeekbar();
        presenter = new SettingsPresenter(this, CityFleetApp.getInstance().getNetworkManager());
        presenter.loadSettings();
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
        ((BaseActivity) getActivity()).changeFragment(new EditUserProfileFragment(), true);
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
    void onHelpBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.help_faq), getString(R.string.endpoint) + Constants.HELP_URL_PATH), true);
    }


    @OnClick(R.id.privacyBtn)
    void onPrivacyClick() {
        ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.privacy_policy), getString(R.string.endpoint) + Constants.PRIVACY_POLICY_PATH), true);
    }

    @OnClick(R.id.termsBtn)
    void onTermsBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.tos), getString(R.string.endpoint) + Constants.TOS_PATH), true);
    }

    @OnCheckedChanged(R.id.statusVisibleSwitch)
    void onCheckedStatusChange(boolean checked) {
        presenter.changeStatusVisible(checked);
    }

    @OnCheckedChanged(R.id.notificationSwitch)
    void onCheckedNotificationChange(boolean checked) {
        presenter.changeNotificationsEnabled(checked);
    }

    @OnCheckedChanged(R.id.chatPrivacySwitch)
    void onChatPrivacyChange(boolean checked) {
        presenter.changeChatPrivacy(checked);
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

    @Override
    public void setLoading(boolean isLoading) {
        //    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getResources().getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void setStatusVisible(boolean visible) {
        statusVisibleSwitch.setChecked(visible);
    }

    @Override
    public void setNotificationsEnabled(boolean enabled) {
        notificationSwitch.setChecked(enabled);
    }

    @Override
    public void setChatPrivacy(boolean chatPrivacy) {
        chatPrivacySwitch.setChecked(chatPrivacy);
    }
}
