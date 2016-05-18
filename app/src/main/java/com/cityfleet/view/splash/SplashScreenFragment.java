package com.cityfleet.view.splash;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cityfleet.R;
import com.cityfleet.view.login.MainLoginSignupFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 18.05.16.
 */
public class SplashScreenFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    @Bind(R.id.textureView)
    TextureView textureView;
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);
        ButterKnife.bind(this, view);
        textureView.setSurfaceTextureListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMediaPlayer();
        }
    }

    private void stopMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            int videoRes = R.raw.splash;
            mediaPlayer = MediaPlayer.create(getContext(), videoRes);
            Surface surfaceTexture = new Surface(surface);
            mediaPlayer.setSurface(surfaceTexture);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            adjustAspectRatio(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        } catch (Exception e) {
            Log.e(MainLoginSignupFragment.class.getName(), e.getMessage());
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;
        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Matrix txform = new Matrix();
        textureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        txform.postTranslate(xoff, yoff);
        textureView.setTransform(txform);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mediaPlayer != null) {
            stopMediaPlayer();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ((SplashScreenActivity) getActivity()).startAppAfterSplashScreen();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

}
