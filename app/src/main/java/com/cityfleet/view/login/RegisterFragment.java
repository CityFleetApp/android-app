package com.cityfleet.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.gcm.RegistrationIntentService;
import com.cityfleet.util.CommonUtils;
import com.cityfleet.util.Constants;
import com.cityfleet.util.GcmRegistrationTypes;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.main.WebFragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFragment extends Fragment implements Validator.ValidationListener, RegistrationPresenter.RegistrationView {
    private Validator validator;
    private RegistrationPresenter presenter;
    @NotEmpty
    @Bind(R.id.fullNameEt)
    EditText fullNameEt;
    @NotEmpty
    @Bind(R.id.usernameEt)
    EditText usernameEt;
    @NotEmpty
    @Size(value = Constants.PHONE_NUMBER_SIZE)
    @Bind(R.id.phoneEt)
    EditText phoneEt;
    @NotEmpty
    @Bind(R.id.hackLicenseEt)
    EditText hackLicenseEt;
    @Email
    @Bind(R.id.emailEt)
    EditText emailEt;
    @Password(min = Constants.MIN_PASSWORD_LENGTH, scheme = Password.Scheme.ALPHA_NUMERIC, messageResId = R.string.password_validation_mes)
    @Bind(R.id.passwordEt)
    EditText passwordEt;
    @NotEmpty
    @ConfirmPassword(messageResId = R.string.password_match_mes)
    @Bind(R.id.confirmPasswordEt)
    EditText confirmPasswordEt;
    @Bind(R.id.signupBtn)
    Button signupBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String defaultErrorMes;
    @Bind(R.id.tosAndPrivacyChb)
    CheckBox tosAndPrivacyChb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.register);
        presenter = new RegistrationPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        SpannableString checkboxText = new SpannableString(getString(R.string.signup_checkbox));
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.privacy_policy), getString(R.string.endpoint) + Constants.PRIVACY_POLICY_PATH), true);
            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ((BaseActivity) getActivity()).changeFragment(WebFragment.getInstance(getString(R.string.tos), getString(R.string.endpoint) + Constants.TOS_PATH), true);
            }
        };
        int privacyStart = checkboxText.toString().indexOf(getString(R.string.privacy_policy));
        int privacyEnd = privacyStart + getString(R.string.privacy_policy).length();
        int termsStart = checkboxText.toString().indexOf(getString(R.string.tos));
        int termsEnd = termsStart + getString(R.string.tos).length();
        checkboxText.setSpan(span1, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkboxText.setSpan(span2, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tosAndPrivacyChb.setText(checkboxText);
        tosAndPrivacyChb.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.signupBtn)
    void onSignupBtnClick() {
        validator.validate();
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onValidationSucceeded() {
        if (!tosAndPrivacyChb.isChecked()) {
            Toast.makeText(getContext(), R.string.registration_chb_message, Toast.LENGTH_SHORT).show();
        } else {
            presenter.signup(fullNameEt.getText().toString(), usernameEt.getText().toString(), phoneEt.getText().toString(), hackLicenseEt.getText().toString(),
                    emailEt.getText().toString(), passwordEt.getText().toString(), confirmPasswordEt.getText().toString());
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        CommonUtils.handleValidationError(getActivity(), errors);
    }


    @Override
    public void onSignUpSuccess(String token, int id) {
        PrefUtil.setToken(getContext(), token);
        PrefUtil.setId(getContext(), id);
        Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
        intent.putExtra(Constants.GCM_REGISTRATION_TYPE_TAG, GcmRegistrationTypes.REGISTER);
        getActivity().startService(intent);
        ((LoginFlowActivity) getActivity()).startMainScreen();
    }

    @Override
    public void startLoading() {
        signupBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        signupBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSignUpFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        signupBtn.setEnabled(true);
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }
}
