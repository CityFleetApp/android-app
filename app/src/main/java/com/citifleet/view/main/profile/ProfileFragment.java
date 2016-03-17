package com.citifleet.view.main.profile;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.UserImages;
import com.citifleet.model.UserInfo;
import com.citifleet.util.CircleTransform;
import com.citifleet.util.CommonUtils;
import com.citifleet.util.Constants;
import com.citifleet.util.PermissionUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * Created by vika on 11.03.16.
 */
public class ProfileFragment extends BaseFragment implements ProfilePresenter.ProfileView {
    private static final int REQUEST_CAMERA = 333;
    private static final int SELECT_FILE = 444;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_GALLERY = 2;
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;
    @Bind(R.id.progressBar)
    RelativeLayout progressBar;
    @Bind(R.id.profileFullName)
    TextView fullName;
    @Bind({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    List<ImageView> stars;
    @Bind(R.id.ratingText)
    TextView ratingText;
    @Bind(R.id.bio)
    TextView bio;
    @Bind(R.id.drives)
    TextView drives;
    @Bind(R.id.documents)
    TextView documents;
    @Bind(R.id.jobsCompleted)
    TextView jobsCompleted;
    @BindString(R.string.default_error_mes)
    String defaultErrorMes;
    @BindString(R.string.pick_image_title)
    String pickImageTitle;
    @Bind(R.id.title)
    TextView title;
    @Bind({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    List<ImageButton> images;
    private ProfilePresenter presenter;
    private List<UserImages> imagesList;
    private int positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new ProfilePresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.init();
        title.setText(getString(R.string.profile));
        return view;
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

    @Override
    public void startLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void stopLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void updateImage(String url) {
        if (!TextUtils.isEmpty(url) && profileImage != null && bigProfileImage != null) {
            int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            TypedValue outValue = new TypedValue();
            getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
            int blurradius = (int) (screenWidth * outValue.getFloat());
            Picasso.with(getActivity()).load(url).transform(new CircleTransform(frameSize)).fit().centerCrop().into(profileImage);
            Picasso.with(getActivity()).load(url).transform(new BlurTransformation(getContext(), blurradius)).fit().centerCrop().into(bigProfileImage);
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }


    @Override
    public void setUserInfo(UserInfo userInfo) {
        fullName.setText(userInfo.getUsername());
        bio.setText(userInfo.getBio());
        drives.setText(userInfo.getDrives());
        jobsCompleted.setText(String.valueOf(userInfo.getJobsCompleted()));
        documents.setText(getString(userInfo.isDocumentsUpToDate() ? R.string.documents_true : R.string.documents_false));
        int rating = userInfo.getRating();
        ratingText.setText(getString(R.string.rating, rating));
        for (int i = 0; i < rating; i++) {
            stars.get(i).setSelected(true);
        }
        for (int i = rating; i < stars.size(); i++) {
            stars.get(i).setSelected(false);
        }
    }

    @Override
    public void updateImageFromList(UserImages userImages) {
        if (positionToUpdateImage != Constants.DEFAULT_UNSELECTED_POSITION) {
            Picasso.with(getContext()).load(userImages.getFile()).into(images.get(positionToUpdateImage));
            imagesList.add(positionToUpdateImage, userImages);
            positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
        }
    }

    @Override
    public void onDeleteImageSuccess() {
        imagesList.remove(positionToUpdateImage);
        onUserImagesLoaded(imagesList);
    }

    @Override
    public void onUserImagesLoaded(List<UserImages> list) {
        this.imagesList = list;
        for (int i = 0; i < images.size(); i++) {
            if (i < imagesList.size()) {
                Picasso.with(getContext()).load(list.get(i).getThumbnail()).into(images.get(i));
            } else {
                images.get(i).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.painting));
            }
        }
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
        return new File(Environment.getExternalStorageDirectory() + File.separator + "car.png"); //TODO rename file?
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
                    updateImageOnServer(CommonUtils.getImagePath(data.getData(), getContext()));
                } else {
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    updateImageOnServer(getFileForProfileFromCamera().getAbsolutePath());
                } else {
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
        }
    }

    private void updateImageOnServer(String filepath) {
        presenter.uploadImageForList(filepath);
    }

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(View view) {
        int clickedPosition = getClickedPosition(view);
        if (imagesList != null && imagesList.size() > clickedPosition && imagesList.get(clickedPosition) != null) {
            showGallery(clickedPosition);
        } else {
            positionToUpdateImage = clickedPosition;
            showPickImageDialog();
        }
    }

    @OnLongClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    boolean onLongImageClick(final View view) {
        final int clickedPosition = getClickedPosition(view);
        if (imagesList != null && imagesList.size() > clickedPosition && imagesList.get(clickedPosition) != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.delete_dialog_title))
                    .setMessage(getString(R.string.delete_dialog_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            positionToUpdateImage = clickedPosition;
                            presenter.deleteImageFromList(imagesList.get(clickedPosition).getId());
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

    private void showGallery(int position) {
        GalleryImageFragment fragment = new GalleryImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.IMAGES_LIST_TAG, Parcels.wrap(imagesList));
        bundle.putInt(Constants.IMAGES_SELECTED_TAG, position);
        fragment.setArguments(bundle);
        ((BaseActivity) getActivity()).changeFragment(fragment, true);
    }
}
