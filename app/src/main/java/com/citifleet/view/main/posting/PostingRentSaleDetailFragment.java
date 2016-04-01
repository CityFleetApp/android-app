package com.citifleet.view.main.posting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.CarOption;
import com.citifleet.model.CarPostingType;
import com.citifleet.util.Constants;
import com.citifleet.util.DecimalDigitsInputFilter;
import com.citifleet.util.ImagePickerUtil;
import com.citifleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 21.03.16.
 */
public class PostingRentSaleDetailFragment extends BaseFragment implements PostingRentSaleDetailPresenter.PostingRentSaleDetailView, ImagePickerUtil.ImageResultListener {
    private static final int REQUEST_CAMERA = 555;
    private static final int SELECT_FILE = 666;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_GALLERY = 2;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.modelBtn)
    RelativeLayout modelBtn;
    @Bind(R.id.makeText)
    TextView makeText;
    @Bind(R.id.modelText)
    TextView modelText;
    @Bind(R.id.typeText)
    TextView typeText;
    @Bind(R.id.colorText)
    TextView colorText;
    @Bind(R.id.fuelText)
    TextView fuelText;
    @Bind(R.id.seatsText)
    TextView seatsText;
    @Bind(R.id.yearText)
    TextView yearText;
    @Bind(R.id.priceText)
    EditText price;
    @Bind(R.id.descrET)
    EditText descr;
    @Bind({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    List<ImageButton> images;
    @BindString(R.string.pick_image_title)
    String pickImageTitle;
    @Bind(R.id.postBtn)
    Button postBtn;
    private CarPostingType postingType;
    private PostingRentSaleDetailPresenter presenter;
    private List<CarOption> makeList;
    private List<CarOption> modelList;
    private List<CarOption> typesList;
    private List<CarOption> seatsList;
    private List<CarOption> fuelsList;
    private List<CarOption> colorsList;
    private List<String> yearList;
    private String[] imageUrls = new String[Constants.POSTING_IMAGES_NUMBER];
    private int selectedMakeId = Constants.DEFAULT_UNSELECTED_POSITION;
    private int selectedModelId = Constants.DEFAULT_UNSELECTED_POSITION;
    private int selectedTypeId = Constants.DEFAULT_UNSELECTED_POSITION;
    private int selectedColorId = Constants.DEFAULT_UNSELECTED_POSITION;
    private int selectedFuelId = Constants.DEFAULT_UNSELECTED_POSITION;
    private int selectedSeatsId = Constants.DEFAULT_UNSELECTED_POSITION;
    private ImagePickerUtil imagePickerUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_fragment, container, false);
        ButterKnife.bind(this, view);
        postingType = (CarPostingType) getArguments().getSerializable(Constants.POSTING_TYPE_TAG);
        title.setText(postingType == CarPostingType.RENT ? R.string.rent : R.string.sale);
        presenter = new PostingRentSaleDetailPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        enableModelBtn(false);
        price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        imagePickerUtil = new ImagePickerUtil(this, images, getString(R.string.car_posting_image_name), this);
        return view;
    }

    private void enableModelBtn(boolean enable) {
        modelBtn.setClickable(enable);
        modelBtn.setAlpha(enable ? Constants.ENABLED_LAYOUT_ALPHA : Constants.DISABLED_LAYOUT_ALPHA);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void showListDialog(String title, List<CarOption> options, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        List<String> stringOptions = new ArrayList<String>();
        for (CarOption carOption : options) {
            stringOptions.add(carOption.getName());
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringOptions);
        builder.setCancelable(true);
        builder.setAdapter(arrayAdapter, listener);
        builder.show();
    }

    private void showYearDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (Constants.MIN_POSTING_YEAR < currentYear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.select_year));
            yearList = new ArrayList<String>();
            for (int i = Constants.MIN_POSTING_YEAR; i <= currentYear; i++) {
                yearList.add(String.valueOf(i));
            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, yearList);
            builder.setCancelable(true);
            builder.setAdapter(arrayAdapter, dateListener);
            builder.show();
        }
    }

    @OnClick(R.id.postBtn)
    void onPostBtnClick() {
        if (selectedMakeId == -1 || selectedModelId == -1 || selectedTypeId == -1 || selectedSeatsId == -1 || selectedFuelId == -1 || selectedColorId == -1 ||
                TextUtils.isEmpty(yearText.getText().toString()) || TextUtils.isEmpty(price.getText().toString()) || TextUtils.isEmpty(descr.getText().toString())) {
            Toast.makeText(getActivity(), R.string.posting_empty, Toast.LENGTH_SHORT).show();
        } else {
            int images = 0;
            for (int i = 0; i < imageUrls.length; i++) {
                if (imageUrls[i] != null) {
                    images++;
                }
            }
            if (images == 0) {
                Toast.makeText(getActivity(), R.string.one_image, Toast.LENGTH_SHORT).show();
            } else {
                presenter.createPost(postingType, selectedMakeId, selectedModelId, selectedTypeId, selectedColorId, Integer.valueOf(yearText.getText().toString()),
                        selectedFuelId, selectedSeatsId, Double.parseDouble(price.getText().toString()),
                        descr.getText().toString(), imageUrls);
            }
        }
    }

    @OnClick(R.id.makeBtn)
    void onMakeBtnClick() {
        if (makeList != null && !makeList.isEmpty()) {
            showListDialog(getString(R.string.select_make), makeList, makeListener);
        }
    }

    @OnClick(R.id.yearBtn)
    void onYearBtnClick() {
        showYearDialog();
    }

    @OnClick(R.id.modelBtn)
    void onModelBtnClick() {
        if (modelList != null && !modelList.isEmpty()) {
            showListDialog(getString(R.string.select_model), modelList, modelListener);
        }
    }

    @OnClick(R.id.typeBtn)
    void onTypeBtnClick() {
        if (typesList != null && !typesList.isEmpty()) {
            showListDialog(getString(R.string.select_type), typesList, typeListener);
        }
    }

    @OnClick(R.id.colorBtn)
    void onColorBtnClick() {
        if (colorsList != null && !colorsList.isEmpty()) {
            showListDialog(getString(R.string.select_color), colorsList, colorListener);
        }
    }

    @OnClick(R.id.fuelBtn)
    void onFuelBtnClick() {
        if (fuelsList != null && !fuelsList.isEmpty()) {
            showListDialog(getString(R.string.select_fuel), fuelsList, fuelListener);
        }
    }

    @OnClick(R.id.seatsBtn)
    void onSeatsBtnClick() {
        if (seatsList != null && !seatsList.isEmpty()) {
            showListDialog(getString(R.string.select_seats), seatsList, seatsListener);
        }
    }

    private DialogInterface.OnClickListener makeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (makeList.get(which).getId() != selectedMakeId) {
                selectedMakeId = makeList.get(which).getId();
                makeText.setText(makeList.get(which).getName());
                if (modelList != null) {
                    modelList.clear();
                    modelText.setText(getString(R.string.select_model));
                }
                presenter.getCarModelsByMakeId(selectedMakeId);
            }
        }
    };
    private DialogInterface.OnClickListener modelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            modelText.setText(modelList.get(which).getName());
            selectedModelId = modelList.get(which).getId();
        }
    };
    private DialogInterface.OnClickListener typeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            typeText.setText(typesList.get(which).getName());
            selectedTypeId = typesList.get(which).getId();
        }
    };
    private DialogInterface.OnClickListener colorListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            colorText.setText(colorsList.get(which).getName());
            selectedColorId = colorsList.get(which).getId();
        }
    };
    private DialogInterface.OnClickListener fuelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            fuelText.setText(fuelsList.get(which).getName());
            selectedFuelId = fuelsList.get(which).getId();
        }
    };
    private DialogInterface.OnClickListener seatsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            seatsText.setText(seatsList.get(which).getName());
            selectedSeatsId = seatsList.get(which).getId();
        }
    };
    private DialogInterface.OnClickListener dateListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            yearText.setText(yearList.get(which));
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void startLoading() {
        postBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        postBtn.setEnabled(true);
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
    public void onMakesLoaded(List<CarOption> makeList) {
        this.makeList = makeList;
    }

    @Override
    public void onModelLoaded(List<CarOption> modelList) {
        this.modelList = modelList;
        enableModelBtn(true);
    }

    @Override
    public void onTypeLoaded(List<CarOption> typeList) {
        this.typesList = typeList;
    }

    @Override
    public void onFuelLoaded(List<CarOption> fuelList) {
        this.fuelsList = fuelList;
    }

    @Override
    public void onColorLoaded(List<CarOption> colorList) {
        this.colorsList = colorList;
    }

    @Override
    public void onSeatsLoaded(List<CarOption> seatsList) {
        this.seatsList = seatsList;
    }

    @Override
    public void onPostCreatesSuccessfully() {
        getActivity().onBackPressed();
    }

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(ImageView view) {
        imagePickerUtil.onImageClick(view);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (imagePickerUtil.isImagePickerPermissionResultCode(requestCode)) {
            imagePickerUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePickerUtil.isImagePickerRequestResultCode(requestCode)) {
            imagePickerUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onImageReceived(String url, int position) {
        imageUrls[position] = url;
        Picasso.with(getContext()).load(new File(imageUrls[position])).fit().centerCrop().into(images.get(position));
    }

    @Override
    public void onImageCanceledOrFailed(int position) {

    }
}
