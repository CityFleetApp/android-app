package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import android.support.v7.widget.RecyclerView;
/**
 * Created by vika on 11.03.16.
 */
public class ProfileFragment extends Fragment {
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.profileFullName)
    TextView fullName;
    @Bind({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    List<ImageView> stars;
    @Bind(R.id.ratingText)
    TextView ratingText;
    @Bind(R.id.bio)
    TextView bio;
    @Bind(R.id.drives)
    TextView drives;
    @Bind(R.id.documents)
    TextView documents;
    @Bind(R.id.jobsCompleted)
    TextView jobsCompleted;
    @Bind(R.id.imagesList)
    RecyclerView imagesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
