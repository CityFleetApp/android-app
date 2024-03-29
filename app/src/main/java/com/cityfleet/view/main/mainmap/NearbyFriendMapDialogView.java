package com.cityfleet.view.main.mainmap;

import android.animation.Animator;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.FriendNearby;
import com.cityfleet.util.CircleTransform;
import com.cityfleet.util.Constants;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 07.04.16.
 */
public class NearbyFriendMapDialogView {
    @Bind(R.id.nearFriendDialog)
    LinearLayout nearFriendDialog;
    @Bind(R.id.userImage)
    ImageView userImage;
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.chatBtn)
    Button chatBtn;
    MainMapFragment fragment;
    int dialogHeight;
    FriendNearby friendNearby;
    private boolean isAnimatingClosing = false;

    public NearbyFriendMapDialogView(MainMapFragment fragment, View viewCont) {
        ButterKnife.bind(this, viewCont);
        this.fragment = fragment;
        waitForDialogToMeasure();
    }

    public void onDestroy() {
        ButterKnife.unbind(this);
    }

    public boolean isVisible() {
        if (!isAnimatingClosing) {
            return nearFriendDialog.getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }

    public FriendNearby getSelectedFriend() {
        return friendNearby;
    }

    private void waitForDialogToMeasure() {
        ViewTreeObserver viewTreeObserver = nearFriendDialog.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        nearFriendDialog.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        nearFriendDialog.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    dialogHeight = nearFriendDialog.getHeight();
                    nearFriendDialog.setTranslationY(-dialogHeight);
                    nearFriendDialog.setVisibility(View.GONE);
                }
            });
        }
    }

    @OnClick(R.id.chatBtn)
    public void onChatBtnClicked() {
        fragment.onChatFriend(friendNearby.getId());
    }

    @OnClick(R.id.nearFriendDialog)
    public void profileClicked() {
        fragment.onProfileFriendOpen(friendNearby.getId());
    }

    public void show(FriendNearby friendNearby) {
        this.friendNearby = friendNearby;
        if (!TextUtils.isEmpty(friendNearby.getAvatarUrl())) {
            Picasso.with(fragment.getContext()).load(friendNearby.getAvatarUrl()).transform(new CircleTransform()).fit().centerCrop().into(userImage);
        } else {
            Picasso.with(fragment.getContext()).load(R.drawable.default_large).transform(new CircleTransform()).fit().centerCrop().into(userImage);
        }
        userName.setText(friendNearby.getFullName());
        if (nearFriendDialog.getVisibility() == View.GONE) {
            playShowHideAnimation();
        }
    }

    public void hide() {
        playShowHideAnimation();
    }

    private void playShowHideAnimation() {

        if (nearFriendDialog.getVisibility() == View.VISIBLE) {
            isAnimatingClosing = true;
            nearFriendDialog.animate().translationY(-dialogHeight).setDuration(Constants.DIALOG_ANIMATION_DURATION).setInterpolator(new AccelerateInterpolator()).
                    setListener(closingAnimatorListener).start();
        } else {
            nearFriendDialog.setVisibility(View.VISIBLE);
            nearFriendDialog.animate().translationY(0).setDuration(Constants.DIALOG_ANIMATION_DURATION).setInterpolator(new AccelerateInterpolator()).setListener(null).start();
        }
    }

    private Animator.AnimatorListener closingAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (nearFriendDialog != null) {
                nearFriendDialog.setVisibility(View.GONE);
            }
            friendNearby = null;
            isAnimatingClosing = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}
