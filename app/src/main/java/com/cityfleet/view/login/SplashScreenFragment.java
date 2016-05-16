package com.cityfleet.view.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cityfleet.R;
import com.cityfleet.view.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashScreenFragment extends Fragment {
//    @Bind(R.id.textureView)
//    TextureView textureView;
//    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splashscreen_fragment, container, false);
        ButterKnife.bind(this, view);
        //textureView.setSurfaceTextureListener(this);
        return view;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            stopMediaPlayer();
//        }
//    }

//    private void stopMediaPlayer() {
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//        mediaPlayer.release();
//        mediaPlayer = null;
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.loginBtn)
    public void onLoginBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new LoginFragment(), true);
    }

    @OnClick(R.id.registerBtn)
    public void onRegisterBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new RegisterFragment(), true);
    }

//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        try {
//            int videoRes = R.raw.splash;
//            mediaPlayer = MediaPlayer.create(getContext(), videoRes);
//            Surface surfaceTexture = new Surface(surface);
//            mediaPlayer.setSurface(surfaceTexture);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.setOnPreparedListener(this);
//        } catch (Exception e) {
//            Log.e(SplashScreenFragment.class.getName(), e.getMessage());
//            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        if (mediaPlayer != null) {
//            stopMediaPlayer();
//        }
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Log.d("TAG", "completed");
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        mediaPlayer.start();
//    }
}
