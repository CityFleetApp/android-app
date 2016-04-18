package com.citifleet.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.gcm.RegistrationIntentService;
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.util.GcmRegistrationTypes;
import com.citifleet.util.PrefUtil;
import com.mobsandgeeks.saripaar.QuickRule;
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
    @Bind(R.id.phoneEt)
    EditText phoneEt;
    @NotEmpty
    @Size(min = Constants.MIN_LENGTH_HACK_LICENSE, max = Constants.MIN_LENGTH_HACK_LICENSE)
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.register);
        presenter = new RegistrationPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.put(phoneEt, phoneRule);
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
        presenter.signup(fullNameEt.getText().toString(), usernameEt.getText().toString(), phoneEt.getText().toString(), hackLicenseEt.getText().toString(),
                emailEt.getText().toString(), passwordEt.getText().toString(), confirmPasswordEt.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        CommonUtils.handleValidationError(getActivity(), errors);
    }

    QuickRule<EditText> phoneRule = new QuickRule<EditText>(0) {

        @Override
        public boolean isValid(EditText editText) {
            String phone = editText.getText().toString();
            return PhoneNumberUtils.isGlobalPhoneNumber(phone); //TODO change to regex
        }

        @Override
        public String getMessage(Context context) {
            return context.getString(R.string.phone_validation);
        }

    };

    @Override
    public void onSignUpSuccess(String token,int id) {
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
