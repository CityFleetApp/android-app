package com.cityfleet.view.main.posting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.Car;
import com.cityfleet.model.CarOption;
import com.cityfleet.model.CarPostingType;
import com.cityfleet.model.Photo;
import com.cityfleet.util.Constants;
import com.cityfleet.util.DecimalDigitsInputFilter;
import com.cityfleet.util.MultipleImagePickerUtil;
import com.cityfleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by vika on 21.03.16.
 */
public class PostingRentSaleDetailFragment extends BaseFragment implements PostingRentSaleDetailPresenter.PostingRentSaleDetailView, MultipleImagePickerUtil.ImageResultListener {
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
    @Bind(R.id.updateBtn)
    Button updateBtn;
    @Bind(R.id.deleteBtn)
    Button deleteBtn;
    private CarPostingType postingType;
    private Car car;
    private boolean isEditMode = false;

    private PostingRentSaleDetailPresenter presenter;
    private List<CarOption> makeList;
    private List<CarOption> modelList;
    private List<CarOption> typesList;
    private List<CarOption> seatsList;
    private List<CarOption> fuelsList;
    private List<CarOption> colorsList;
    private List<String> yearList;
    private Photo[] imageUrls = new Photo[Constants.POSTING_IMAGES_NUMBER];
    private List<Integer> photosToDelete = new ArrayList<Integer>();
    private MultipleImagePickerUtil imagePickerUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        postingType = (CarPostingType) args.getSerializable(Constants.POSTING_TYPE_TAG);
        if (args != null && args.containsKey(Constants.CAR_RENT_SALE_TAG)) {
            isEditMode = true;
            car = Parcels.unwrap(getArguments().getParcelable(Constants.CAR_RENT_SALE_TAG));
            initWithEditValues();
        } else {
            car = new Car();
            enableModelBtn(false);
        }
        title.setText(postingType == CarPostingType.RENT ? R.string.rent : R.string.sale);
        presenter = new PostingRentSaleDetailPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        imagePickerUtil = new MultipleImagePickerUtil(this, images, getString(R.string.car_posting_image_name), this);
        return view;
    }

    private void initWithEditValues() {
        makeText.setText(car.getMake());
        modelText.setText(car.getModel());
        typeText.setText(car.getType());
        colorText.setText(car.getColor());
        fuelText.setText(car.getFuel());
        seatsText.setText(String.valueOf(car.getSeats()));
        yearText.setText(String.valueOf(car.getYear()));
        price.setText(car.getPrice());
        descr.setText(car.getDescription());
        if (car.getPhotos().size() > 0) {
            imageUrls = car.getPhotos().toArray(imageUrls);
            for (int i = 0; i < car.getPhotos().size(); i++) {
                Picasso.with(getContext()).load(car.getPhotos().get(i).getUrl()).centerCrop().fit().into(images.get(i));
            }
        }
        postBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
    }

    private void enableModelBtn(boolean enable) {
        modelBtn.setClickable(enable);
        modelBtn.setAlpha(enable ? Constants.ENABLED_LAYOUT_ALPHA : Constants.DISABLED_LAYOUT_ALPHA);
    }

    @OnClick(R.id.deleteBtn)
    void onDeleteBtnClick() {
        presenter.deletePost(postingType, car.getId());
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

    @OnClick({R.id.postBtn, R.id.updateBtn})
    void onPostBtnClick() {
        if (car.getMakeId() == Constants.DEFAULT_UNSELECTED_POSITION || car.getModelId() == Constants.DEFAULT_UNSELECTED_POSITION || car.getTypeId() == Constants.DEFAULT_UNSELECTED_POSITION
                || car.getSeatsId() == Constants.DEFAULT_UNSELECTED_POSITION || car.getFuelId() == Constants.DEFAULT_UNSELECTED_POSITION || car.getColorId() == Constants.DEFAULT_UNSELECTED_POSITION ||
                TextUtils.isEmpty(yearText.getText().toString()) || TextUtils.isEmpty(price.getText().toString()) || TextUtils.isEmpty(descr.getText().toString())) {
            Toast.makeText(getActivity(), R.string.posting_empty, Toast.LENGTH_SHORT).show();
        } else {
            car.setYear(Integer.valueOf(yearText.getText().toString()));
            car.setPrice(price.getText().toString());
            car.setDescription(descr.getText().toString());
            car.setPhotos(Arrays.asList(imageUrls));
            presenter.createPost(postingType, car, isEditMode, photosToDelete);
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
            if (makeList.get(which).getId() != car.getMakeId()) {
                car.setMakeId(makeList.get(which).getId());
                car.setModelId(Constants.DEFAULT_UNSELECTED_POSITION);
                car.setModel(null);
                makeText.setText(makeList.get(which).getName());
                if (modelList != null) {
                    modelList.clear();
                    modelText.setText(getString(R.string.select_model));
                }
                presenter.getCarModelsByMakeId(car.getMakeId());
            }
        }
    };
    private DialogInterface.OnClickListener modelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            modelText.setText(modelList.get(which).getName());
            car.setModelId(modelList.get(which).getId());
        }
    };
    private DialogInterface.OnClickListener typeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            typeText.setText(typesList.get(which).getName());
            car.setTypeId(typesList.get(which).getId());
        }
    };
    private DialogInterface.OnClickListener colorListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            colorText.setText(colorsList.get(which).getName());
            car.setColorId(colorsList.get(which).getId());
        }
    };
    private DialogInterface.OnClickListener fuelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            fuelText.setText(fuelsList.get(which).getName());
            car.setFuelId(fuelsList.get(which).getId());
        }
    };
    private DialogInterface.OnClickListener seatsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            seatsText.setText(seatsList.get(which).getName());
            car.setSeatsId(seatsList.get(which).getId());
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
        if (isEditMode) {
            for (CarOption make : makeList) {
                if (make.getName().equals(car.getMake())) {
                    car.setMakeId(make.getId());
                    break;
                }
            }
            presenter.getCarModelsByMakeId(car.getMakeId());
        }
    }

    @Override
    public void onModelLoaded(List<CarOption> modelList) {
        this.modelList = modelList;
        enableModelBtn(true);
        if (isEditMode) {
            for (CarOption model : modelList) {
                if (model.getName().equals(car.getModel())) {
                    car.setModelId(model.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onTypeLoaded(List<CarOption> typeList) {
        this.typesList = typeList;
        if (isEditMode) {
            for (CarOption type : typeList) {
                if (type.getName().equals(car.getType())) {
                    car.setTypeId(type.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onFuelLoaded(List<CarOption> fuelList) {
        this.fuelsList = fuelList;
        if (isEditMode) {
            for (CarOption fuel : fuelList) {
                if (fuel.getName().equals(car.getFuel())) {
                    car.setFuelId(fuel.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onColorLoaded(List<CarOption> colorList) {
        this.colorsList = colorList;
        if (isEditMode) {
            for (CarOption color : colorList) {
                if (color.getName().equals(car.getColor())) {
                    car.setColorId(color.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onSeatsLoaded(List<CarOption> seatsList) {
        this.seatsList = seatsList;
        if (isEditMode) {
            for (CarOption seats : seatsList) {
                if (seats.getId() == car.getSeats()) {
                    car.setSeatsId(seats.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onPostCreatesSuccessfully() {
        getActivity().onBackPressed();
    }

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(ImageView view) {
        int position = imagePickerUtil.getClickedPosition(view);
        if (imageUrls[position] == null || imageUrls[position].getId() == 0) {
            imagePickerUtil.onImageClick(view);
        }
    }

    @OnLongClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    boolean onLongImageClick(final View view) {
        final int clickedPosition = imagePickerUtil.getClickedPosition(view);
        if (imageUrls[clickedPosition] != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.delete_dialog_title))
                    .setMessage(getString(R.string.delete_dialog_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteImage(clickedPosition);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }

    private void deleteImage(int position) {
        if (imageUrls[position].getId() != 0) {
            photosToDelete.add(imageUrls[position].getId());
        }
        imageUrls[position] = null;
        images.get(position).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.painting));
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
        Photo photo = new Photo();
        photo.setUrl(url);
        imageUrls[position] = photo;
        Picasso.with(getContext()).load(new File(imageUrls[position].getUrl())).fit().centerCrop().into(images.get(position));
    }

    @Override
    public void onImageCanceledOrFailed(int position) {

    }
}
