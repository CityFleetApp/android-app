package com.citifleet.view.main.posting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.Car;
import com.citifleet.model.CarPostingType;
import com.citifleet.model.GeneralGood;
import com.citifleet.model.JobOffer;
import com.citifleet.model.ManagePostModel;
import com.citifleet.model.PostingType;
import com.citifleet.util.Constants;
import com.citifleet.util.DividerItemDecoration;
import com.citifleet.util.RecycleViewClickListener;
import com.citifleet.util.SpacesItemDecoration;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.citifleet.view.main.benefits.BenefitPresenter;
import com.citifleet.view.main.benefits.BenefitsAdapter;
import com.citifleet.view.main.marketplace.*;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 31.03.16.
 */
public class ManagePostsFragment extends BaseFragment implements ManagePostsAdapter.OnItemClickListener, ManagePostsPresenter.ManagePostsView {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.postsList)
    RecyclerView postsList;
    private ManagePostsPresenter presenter;
    private ManagePostsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_posts_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.manage_previous_posts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        postsList.setLayoutManager(layoutManager);
        adapter = new ManagePostsAdapter(getActivity(), this);
        postsList.setAdapter(adapter);
        postsList.addItemDecoration(new DividerItemDecoration(getActivity()));
        presenter = new ManagePostsPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        presenter.loadPosts();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemClick(ManagePostModel item) {
        if (item.getPostingType().equals(PostingType.JOB_OFFER.getApiName())) {
            JobOfferFragment fragment = new JobOfferFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.JOB_OFFER_TAG, Parcels.wrap(getJobOfferFromManagePostModel(item)));
            fragment.setArguments(args);
            ((BaseActivity) getActivity()).changeFragment(fragment, true);
        } else if (item.getPostingType().equals(PostingType.GENERAL_GOOD.getApiName())) {
            GeneralGoodsFragment fragment = new GeneralGoodsFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.GENERAL_GOODS_TAG, Parcels.wrap(getGenGoodFromManagePostModel(item)));
            fragment.setArguments(args);
            ((BaseActivity) getActivity()).changeFragment(fragment, true);
        } else {
            PostingRentSaleDetailFragment fragment = new PostingRentSaleDetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.CAR_RENT_SALE_TAG, Parcels.wrap(getCarFromManagePostModel(item)));
            args.putSerializable(Constants.POSTING_TYPE_TAG, item.isRent() ? CarPostingType.RENT : CarPostingType.SALE);
            fragment.setArguments(args);
            ((BaseActivity) getActivity()).changeFragment(fragment, true);
        }
    }

    private Car getCarFromManagePostModel(ManagePostModel item) {
        Car car = new Car();
        car.setId(item.getId());
        car.setPhotos(item.getPhotos());
        car.setPrice(item.getPrice());
        car.setDescription(item.getDescription());
        car.setColor(item.getColor());
        car.setFuel(item.getFuel());
        car.setMake(item.getMake());
        car.setModel(item.getModel());
        car.setType(item.getType());
        car.setRent(item.isRent());
        car.setSeats(item.getSeats());
        car.setYear(item.getYear());
        return car;
    }

    private GeneralGood getGenGoodFromManagePostModel(ManagePostModel item) {
        GeneralGood generalGood = new GeneralGood();
        generalGood.setId(item.getId());
        generalGood.setCondition(item.getCondition());
        generalGood.setDescription(item.getDescription());
        generalGood.setItem(item.getItem());
        generalGood.setPhotos(item.getPhotos());
        generalGood.setPrice(item.getPrice());
        return generalGood;
    }

    private JobOffer getJobOfferFromManagePostModel(ManagePostModel item) {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setId(item.getId());
        jobOffer.setDateTime(item.getPickupDatetime());
        jobOffer.setSuite(item.isSuite());
        jobOffer.setJobType(item.getJobType());
        jobOffer.setVehicleType(item.getVehicleType());
        jobOffer.setPickupAddress(item.getPickupAddress());
        jobOffer.setDestination(item.getDestination());
        jobOffer.setFare(Double.parseDouble(item.getFare()));
        jobOffer.setGratuity(item.getGratuity());
        jobOffer.setInstructions(item.getInstructions());
        jobOffer.setStatus(item.getStatus());
        return jobOffer;
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPostsLoaded(List<ManagePostModel> posts) {
        adapter.setManagePostModels(posts);
        adapter.notifyDataSetChanged();
    }
}
