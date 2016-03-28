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
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.util.DecimalDigitsInputFilter;
import com.citifleet.util.PermissionUtil;
import com.citifleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 21.03.16.
 */
public class GeneralGoodsFragment extends BaseFragment implements PostingGeneralGoodsPresenter.PostingGeneralGoodsDetailView {
    private static final int REQUEST_CAMERA = 777;
    private static final int SELECT_FILE = 888;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_GALLERY = 2;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.itemET)
    EditText itemEt;
    @Bind(R.id.descrET)
    EditText descrEt;
    @Bind(R.id.priceET)
    EditText priceEt;
    @Bind(R.id.conditionText)
    TextView condition;
    @Bind({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    List<ImageButton> images;
    @BindString(R.string.pick_image_title)
    String pickImageTitle;
    @Bind(R.id.postBtn)
    Button postBtn;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private int selectedConditionId = Constants.DEFAULT_UNSELECTED_POSITION;
    private String[] imageUrls = new String[Constants.POSTING_IMAGES_NUMBER];
    private int positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
    private PostingGeneralGoodsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_goods_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.general_goods);
        presenter = new PostingGeneralGoodsPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        priceEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        return view;
    }

    @OnClick(R.id.postBtn)
    void onPostBtnClick() {
        if (TextUtils.isEmpty(itemEt.getText().toString()) || TextUtils.isEmpty(descrEt.getText().toString()) || TextUtils.isEmpty(priceEt.getText().toString())
                || selectedConditionId == Constants.DEFAULT_UNSELECTED_POSITION) {
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
                presenter.createPost(Double.parseDouble(priceEt.getText().toString()), descrEt.getText().toString(), selectedConditionId, itemEt.getText().toString(), imageUrls);
            }
        }
    }

    @OnClick(R.id.conditionBtn)
    void onMakeBtnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_condition));
        final String[] conditions = getResources().getStringArray(R.array.general_goods_conditions);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, conditions);
        builder.setCancelable(true);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                condition.setText(conditions[which]);
                selectedConditionId = which + 1;
            }
        });
        builder.show();
    }

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(View view) {
        int clickedPosition = getClickedPosition(view);
        positionToUpdateImage = clickedPosition;
        showPickImageDialog();
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
        return new File(Environment.getExternalStorageDirectory() + File.separator + "general_good_posting_" + positionToUpdateImage + ".png"); //TODO rename file?
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

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
    public void onPostCreatesSuccessfully() {
        getActivity().onBackPressed();
    }
}
