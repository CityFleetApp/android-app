package com.citifleet.view.main.legalaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.util.LegalAidType;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 16.03.16.
 */
public class LegalAidFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.legal_aid_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.legal_aid));
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.dmvLawyers)
    void onDmvLawyersClick() {
        openDetailFragment(LegalAidType.DMV_LAWYERS);
    }

    @OnClick(R.id.tlcLawyers)
    void onTlcLawyersClick() {
        openDetailFragment(LegalAidType.TLC_LAWYERS);
    }

    @OnClick(R.id.accountants)
    void onAccountantsClick() {
        openDetailFragment(LegalAidType.ACCOUNTANTS);
    }

    @OnClick(R.id.brokers)
    void onBrokersClick() {
        openDetailFragment(LegalAidType.BROKERS);
    }

    private void openDetailFragment(LegalAidType type) {
        LegalAidDetailFragment fragment = new LegalAidDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.LEGAL_AID_TYPE_TAG, type);
        fragment.setArguments(bundle);
        ((BaseActivity) getActivity()).changeFragment(fragment, true);
    }
}
