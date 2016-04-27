package com.cityfleet.view.main.legalaid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.LegalAidLocation;
import com.cityfleet.model.LegalAidPerson;
import com.cityfleet.util.Constants;
import com.cityfleet.util.LegalAidType;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 16.03.16.
 */
public class LegalAidDetailFragment extends BaseFragment implements LegalAidPresenter.LegalAidDetailView {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.personTitle)
    TextView personTitle;
    @Bind(R.id.personText)
    TextView personText;
    @Bind(R.id.progressBar)
    RelativeLayout progressBar;
    @Bind(R.id.contactBtn)
    Button contactBtn;
    @Bind(R.id.locationText)
    TextView locationText;
    @Bind(R.id.personInfo)
    LinearLayout personInfo;
    @Bind(R.id.nameLbl)
    TextView name;
    @Bind(R.id.yearsOfExperienceLbl)
    TextView yearsOfExperience;
    @Bind({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    List<ImageView> stars;
    @Bind(R.id.personBtn)
    RelativeLayout personBtn;
    private LegalAidType type;
    private LegalAidPresenter presenter;
    private List<LegalAidLocation> locations;
    private List<LegalAidPerson> persons;
    private LegalAidLocation selectedLocation;
    private LegalAidPerson selectedPerson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.legalaid_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        type = (LegalAidType) getArguments().getSerializable(Constants.LEGAL_AID_TYPE_TAG);
        presenter = new LegalAidPresenter(this, CityFleetApp.getInstance().getNetworkManager());
        if (locations == null) {
            presenter.loadLocations();
        }
        initLbl();
        return view;
    }

    private void initLbl() {
        title.setText(getTitleTextByType(type));
        String personType = getPersonNameByType(type);
        personTitle.setText(personType);
        personText.setText(getString(R.string.select_person, personType));
        contactBtn.setText(getString(R.string.contact_person, personType));
        if (selectedLocation != null) {
            locationText.setText(selectedLocation.getName());
            if (selectedPerson != null) {
                personText.setText(selectedPerson.getName());
                updatePersonInfo();
            }
        } else {
            personInfo.setVisibility(View.GONE);
            personBtn.setAlpha(Constants.DISABLED_LAYOUT_ALPHA);
        }
    }

    private String getTitleTextByType(LegalAidType type) {
        switch (type) {
            case DMV_LAWYERS:
                return getString(R.string.dmv_lawyers);
            case TLC_LAWYERS:
                return getString(R.string.tlc_lawyers);
            case ACCOUNTANTS:
                return getString(R.string.accountants);
            case BROKERS:
                return getString(R.string.insurance_brokers);
        }
        return "";
    }

    private String getPersonNameByType(LegalAidType type) {
        switch (type) {
            case DMV_LAWYERS:
            case TLC_LAWYERS:
                return getString(R.string.lawyer);
            case ACCOUNTANTS:
                return getString(R.string.accountant);
            case BROKERS:
                return getString(R.string.broker);
        }
        return "";
    }

    @OnClick(R.id.contactBtn)
    void onContactBtnClick() {
        if (selectedPerson != null) {
            LegalAidContactFragment contactFragment = new LegalAidContactFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.SELECTED_PERSON_TAG, Parcels.wrap(selectedPerson));
            contactFragment.setArguments(bundle);
            ((BaseActivity) getActivity()).changeFragment(contactFragment, true);
        }
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

    @OnClick(R.id.locationBtn)
    void onLocationBtnClick() {
        if (locations != null && locations.size() > 0) {
            AlertDialog.Builder locationDialogBuilder = new AlertDialog.Builder(getActivity());
            locationDialogBuilder.setTitle(getString(R.string.select_location));
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
            for (LegalAidLocation location : locations) {
                arrayAdapter.add(location.getName());
            }
            locationDialogBuilder.setCancelable(true);
            locationDialogBuilder.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!locations.get(which).equals(selectedLocation)) {
                                selectedLocation = locations.get(which);
                                locationText.setText(selectedLocation.getName());
                                personBtn.setAlpha(Constants.ENABLED_LAYOUT_ALPHA);
                                if (persons != null) {
                                    persons.clear();
                                    personText.setText(getString(R.string.select_person, getPersonNameByType(type)));
                                    personInfo.setVisibility(View.GONE);
                                }
                                presenter.loadPersons(type, selectedLocation.getId());
                            }
                        }
                    });
            locationDialogBuilder.show();
        }
    }

    @OnClick(R.id.personBtn)
    void onPersonBtnClick() {
        if (persons != null) {
            if (persons.size() > 0) {
                AlertDialog.Builder personDialogBuilder = new AlertDialog.Builder(getActivity());
                String personType = getPersonNameByType(type);
                personDialogBuilder.setTitle(getString(R.string.select_person, personType));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
                for (LegalAidPerson person : persons) {
                    arrayAdapter.add(person.getName());
                }
                personDialogBuilder.setCancelable(true);
                personDialogBuilder.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!persons.get(which).equals(selectedPerson)) {
                                    selectedPerson = persons.get(which);
                                    personText.setText(selectedPerson.getName());
                                    updatePersonInfo();
                                    personBtn.setAlpha(Constants.ENABLED_LAYOUT_ALPHA);
                                }
                            }
                        });
                personDialogBuilder.show();
            } else {
                Toast.makeText(getContext(), R.string.other_location, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePersonInfo() {
        if (selectedPerson != null) {
            personInfo.setVisibility(View.VISIBLE);
            name.setText(selectedPerson.getName());
            yearsOfExperience.setText(String.valueOf(selectedPerson.getYearsOfExperience()));
            int rating = selectedPerson.getRating();
            for (int i = 0; i < rating; i++) {
                stars.get(i).setSelected(true);
            }
            for (int i = rating; i < stars.size(); i++) {
                stars.get(i).setSelected(false);
            }
        }
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
    public void onLocationLoaded(List<LegalAidLocation> locations) {
        this.locations = locations;
    }

    @Override
    public void onPersonsLoaded(List<LegalAidPerson> persons) {
        this.persons = persons;
    }
}
