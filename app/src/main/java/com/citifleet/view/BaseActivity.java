package com.citifleet.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.citifleet.R;
import com.citifleet.util.Constants;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    private FragmentManager   fragmentManager = getSupportFragmentManager();
    private BroadcastReceiver logoutReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, new IntentFilter(Constants.ACTION_LOGOUT));
        changeFragment(getInitFragment(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
    }

    protected abstract Fragment getInitFragment();

    @Override
    public void onBackPressed() {
        fragmentManager.popBackStack();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (int i = 0; i < fragmentList.size(); i++) {
            fragmentList.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void goToTop() {
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void changeFragment(Fragment f, boolean addToBackStack) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // BackStack
        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        // Adding fragment
        Fragment oldFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(R.id.fragmentContainer, f);

        // Commit transaction
        ft.commit();
    }

    public void changeFragmentWithAnimation(Fragment f, boolean addToBackStack, @AnimRes int enter,
                                            @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // BackStack
        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.setCustomAnimations(enter, exit, popEnter, popExit);
        ft.add(R.id.fragmentContainer, f);

        // Commit transaction
        ft.commit();
    }
}