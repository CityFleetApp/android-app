package com.citifleet.view.main.posting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.PostingType;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 21.03.16.
 */
public class PostingRentSaleDetailFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    private PostingType postingType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_fragment, container, false);
        ButterKnife.bind(this, view);
        postingType = (PostingType) getArguments().getSerializable(Constants.POSTING_TYPE_TAG);
        title.setText(postingType == PostingType.RENT ? R.string.rent : R.string.sale);
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
}
