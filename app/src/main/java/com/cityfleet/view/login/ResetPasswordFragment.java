package com.cityfleet.view.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.CommonUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 25.03.16.
 */
public class ResetPasswordFragment extends Fragment implements Validator.ValidationListener {
    @Bind(R.id.resetBtn)
    Button resetBtn;
    @NotEmpty
    @Email
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_fragment, container, false);
        ButterKnife.bind(this, view);
        validator = new Validator(this);
        validator.setValidationListener(this);
        return view;
    }

    @OnClick(R.id.resetBtn)
    void resetBtnClick() {
        validator.validate();
    }

    private Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            setLoading(false);
            if (response.isSuccessful()) {
                Toast.makeText(getActivity(), R.string.password_reset, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else {
                String error = NetworkErrorUtil.gerErrorMessage(response);
                if (TextUtils.isEmpty(error)) {
                    error = getString(R.string.default_error_mes);
                }
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            setLoading(false);
            Log.d(ResetPasswordFragment.class.getName(), t.getMessage());
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        resetBtn.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void resetPassword() {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            setLoading(true);
            Call<Void> call = networkManager.getNetworkClient().resetPassword(email.getText().toString());
            call.enqueue(callback);
        } else {
            Toast.makeText(getContext(), R.string.networkMesMoInternet, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onValidationSucceeded() {
        resetPassword();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        CommonUtils.handleValidationError(getActivity(), errors);
    }
}
