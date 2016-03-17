package com.citifleet.view.main.legalaid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.LegalAidPerson;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 17.03.16.
 */
public class LegalAidContactFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.phoneLbl)
    TextView phoneLbl;
    @Bind(R.id.addressLbl)
    TextView addressLbl;
    private LegalAidPerson selectedPerson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.legalaid_contact_fragment, container, false);
        ButterKnife.bind(this, view);
        selectedPerson = Parcels.unwrap(getArguments().getParcelable(Constants.SELECTED_PERSON_TAG));
        title.setText(selectedPerson.getName());
        phoneLbl.setText(selectedPerson.getPhone());
        addressLbl.setText(selectedPerson.getAddress());
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.phoneBtn)
    void onPhoneBtnClick() {
        if (!TextUtils.isEmpty(selectedPerson.getPhone())) {
            Uri call = Uri.parse("tel:" + selectedPerson.getPhone());
            Intent intent = new Intent(Intent.ACTION_DIAL, call);
            startActivity(intent);
        }
    }

    @OnClick(R.id.addressBtn)
    void onAddressBtnClick() {
        if (!TextUtils.isEmpty(selectedPerson.getAddress())) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + selectedPerson.getAddress());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
