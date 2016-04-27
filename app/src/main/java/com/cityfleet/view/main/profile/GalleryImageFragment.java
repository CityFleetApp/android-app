package com.cityfleet.view.main.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.UserImages;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 15.03.16.
 */
public class GalleryImageFragment extends BaseFragment {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    RelativeLayout progressBar;
    @Bind(R.id.deleteBtn)
    ImageButton deleteBtn;
    private List<UserImages> imagesList;
    private int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        ButterKnife.bind(this, view);
        imagesList = Parcels.unwrap(getArguments().getParcelable(Constants.IMAGES_LIST_TAG));
        position = getArguments().getInt(Constants.IMAGES_SELECTED_TAG);
        pager.setAdapter(new GalleryPagerAdapter(getContext(), imagesList));
        pager.setCurrentItem(position, false);
        setTitleText(pager.getCurrentItem());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleText(pager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.deleteBtn)
    void onDeleteBtnClick() {
        int idToDelete = imagesList.get(pager.getCurrentItem()).getId();
        if (CityFleetApp.getInstance().getNetworkManager().isConnectedOrConnecting()) {
            setLoading(true);
            Call<Void> call = CityFleetApp.getInstance().getNetworkManager().getNetworkClient().deletePhoto(idToDelete);
            call.enqueue(callback);
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
        }
    }

    private Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            setLoading(false);
            if (response.isSuccessful()) {
                imagesList.remove(pager.getCurrentItem());
                if (imagesList.size() == 0) {
                    getActivity().onBackPressed();
                } else {
                    pager.getAdapter().notifyDataSetChanged();
                    setTitleText(pager.getCurrentItem());
                }
            } else {
                GalleryImageFragment.this.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            setLoading(false);
            GalleryImageFragment.this.onFailure(t.getMessage());
        }
    };

    private void setTitleText(int position) {
        title.setText(getString(R.string.gallery_title, position + 1, imagesList.size()));
    }

    private void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getResources().getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        deleteBtn.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
