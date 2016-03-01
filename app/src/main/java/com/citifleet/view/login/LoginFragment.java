package com.citifleet.view.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.PrefUtil;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements Validator.ValidationListener, LoginPresenter.LoginView {
    private Validator      validator;
    private LoginPresenter presenter;
    @NotEmpty
    @Email
    @Bind(R.id.email)
    EditText    email;
    @NotEmpty
    @Bind(R.id.password)
    EditText    password;
    @Bind(R.id.loginBtn)
    Button      loginBtn;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String      defaultErrorMes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new LoginPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        return view;
    }

    @OnClick(R.id.loginBtn)
    void loginBtnClick() {
        validator.validate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onLoginSuccess(String token) {
        PrefUtil.setToken(getContext(), token);
        Toast.makeText(getContext(), "Successful login", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLoading() {
        loginBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        loginBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoginFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        loginBtn.setEnabled(true);
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onValidationSucceeded() {
        presenter.login(email.getText().toString().trim().toLowerCase(), password.getText().toString().trim());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
