package com.citifleet.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.citifleet.R;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    private FragmentManager mFm = getSupportFragmentManager();


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFragment(getInitFragment(), false);
    }

    protected abstract Fragment getInitFragment();

    @Override
    public void onBackPressed() {
        mFm.popBackStack();
        if (mFm.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = mFm.getFragments();
        for (int i = 0; i < fragmentList.size(); i++) {
            fragmentList.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void goToTop() {
        mFm.popBackStack(mFm.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void changeFragment(Fragment f, boolean addToBackStack) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = mFm.beginTransaction();

        // BackStack
        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        // Adding fragment
        Fragment oldFragment = mFm.findFragmentById(R.id.fragmentContainer);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(R.id.fragmentContainer, f);

        // Commit transaction
        ft.commit();
    }

}