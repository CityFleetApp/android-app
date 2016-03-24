package com.citifleet.view.main.posting;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.CarOption;
import com.citifleet.model.PostingType;
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.util.PermissionUtil;
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
public class PostingRentSaleDetailFragment extends BaseFragment implements PostingRentSaleDetailPresenter.PostingRentSaleDetailView {
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
    private PostingType postingType;
    private PostingRentSaleDetailPresenter presenter;
    private List<CarOption> makeList;
    private List<CarOption> modelList;
    private List<CarOption> typesList;
    private List<CarOption> seatsList;
    private List<CarOption> fuelsList;
    private List<CarOption> colorsList;
    private List<String> yearList;
    private String[] imageUrls = new String[Constants.POSTING_IMAGES_NUMBER];
    private int selectedMakeId = -1;
    private int selectedModelId = -1;
    private int selectedTypeId = -1;
    private int selectedColorId = -1;
    private int selectedFuelId = -1;
    private int selectedSeatsId = -1;
    private int positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_fragment, container, false);
        ButterKnife.bind(this, view);
        postingType = (PostingType) getArguments().getSerializable(Constants.POSTING_TYPE_TAG);
        title.setText(postingType == PostingType.RENT ? R.string.rent : R.string.sale);
        presenter = new PostingRentSaleDetailPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        enableModelBtn(false);
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
            Toast.makeText(getActivity(), "Fill all the fields!", Toast.LENGTH_SHORT).show();
        } else {

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

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(View view) {
        int clickedPosition = getClickedPosition(view);
        positionToUpdateImage = clickedPosition;
        showPickImageDialog();
    }

    private int getClickedPosition(View view) {
        int clickedPosition = Constants.DEFAULT_UNSELECTED_POSITION;
        for (ImageButton imageButton : images) {
            if (imageButton.getId() == view.getId()) {
                clickedPosition = images.indexOf(imageButton);
                break;
            }
        }
        return clickedPosition;
    }

    private void showPickImageDialog() {
        final String[] items = getResources().getStringArray(R.array.pick_image_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(pickImageTitle);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    checkPermissionsForCamera();
                } else if (item == 1) {
                    checkPermissionsForGallery();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermissionsForGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_GALLERY);
        } else {
            launchGallery();
        }
    }

    private void checkPermissionsForCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CAMERA);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(Constants.ACTION_PICK_IMAGE_CAMERA);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFileForProfileFromCamera()));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    private File getFileForProfileFromCamera() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + "car_posting_" + positionToUpdateImage + ".png"); //TODO rename file?
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted
                launchCamera();
            }
        } else if (requestCode == REQUEST_PERMISSION_GALLERY) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    imageUrls[positionToUpdateImage] = CommonUtils.getImagePath(data.getData(), getContext());
                    Picasso.with(getContext()).load(new File(imageUrls[positionToUpdateImage])).fit().centerCrop().into(images.get(positionToUpdateImage));
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    imageUrls[positionToUpdateImage] = getFileForProfileFromCamera().getAbsolutePath();
                    Picasso.with(getContext()).load(new File(imageUrls[positionToUpdateImage])).fit().centerCrop().into(images.get(positionToUpdateImage));
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
        }
    }
}
