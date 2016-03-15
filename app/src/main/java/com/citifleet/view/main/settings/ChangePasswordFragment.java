package com.citifleet.view.main.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 15.03.16.
 */
public class ChangePasswordFragment extends BaseFragment implements ChangePasswordPresenter.ChangePasswordView, Validator.ValidationListener {
    private Validator validator;
    @Bind(R.id.title)
    TextView title;
    @NotEmpty
    @Bind(R.id.currentPasswordEt)
    EditText currentPasswordEt;
    @Password(min = Constants.MIN_PASSWORD_LENGTH, scheme = Password.Scheme.ALPHA_NUMERIC, messageResId = R.string.password_validation_mes)
    @Bind(R.id.newPasswordEt)
    EditText newPasswordEt;
    @NotEmpty
    @ConfirmPassword(messageResId = R.string.password_match_mes)
    @Bind(R.id.confirmNewPasswordEt)
    EditText confirmNewPasswordEt;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.changePasswordBtn)
    Button changePasswordBtn;
    private ChangePasswordPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.change_password));
        validator = new Validator(this);
        validator.setValidationListener(this);
        presenter = new ChangePasswordPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.changePasswordBtn)
    void onChangePasswordClick() {
        validator.validate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setLoading(boolean isLoading) {
        changePasswordBtn.setEnabled(!isLoading);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getResources().getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess() {
        getActivity().onBackPressed();
    }

    @Override
    public void onValidationSucceeded() {
        presenter.changePassword(currentPasswordEt.getText().toString(), newPasswordEt.getText().toString(), confirmNewPasswordEt.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        CommonUtils.handleValidationError(getActivity(), errors);
    }
}
