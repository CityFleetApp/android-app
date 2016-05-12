package com.cityfleet.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.cityfleet.R;

import java.io.File;
import java.util.List;

/**
 * Created by vika on 30.03.16.
 */
public class MultipleImagePickerUtil {
    private Fragment fragment;
    private List<? extends ImageView> images;
    private int positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
    public static final int REQUEST_CAMERA = 555;
    public static final int SELECT_FILE = 666;
    public static final int REQUEST_PERMISSION_CAMERA = 1;
    public static final int REQUEST_PERMISSION_GALLERY = 2;
    private String cameraImageName;
    private ImageResultListener listener;

    public MultipleImagePickerUtil(Fragment fragment, List<? extends ImageView> images, String cameraImageName, ImageResultListener listener) {
        this.fragment = fragment;
        this.images = images;
        this.listener = listener;
        this.cameraImageName = cameraImageName;
    }

    public void onImageClick(ImageView view) {
        int clickedPosition = getClickedPosition(view);
        positionToUpdateImage = clickedPosition;
        showPickImageDialog();
    }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    String url = CommonUtils.getImagePath(data.getData(), fragment.getContext());
                    listener.onImageReceived(url, positionToUpdateImage);
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                } else {
                    listener.onImageCanceledOrFailed(positionToUpdateImage);
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    String url = getFileForProfileFromCamera().getAbsolutePath();
                    listener.onImageReceived(url, positionToUpdateImage);
                    positionToUpdateImage = Constants.DEFAULT_UNSELECTED_POSITION;
                } else {
                    listener.onImageCanceledOrFailed(positionToUpdateImage);
                }
                break;
        }
    }

    public boolean isImagePickerRequestResultCode(int resultCode) {
        return (resultCode == REQUEST_CAMERA || resultCode == SELECT_FILE);
    }

    public boolean isImagePickerPermissionResultCode(int resultCode) {
        return (resultCode == REQUEST_PERMISSION_CAMERA || resultCode == REQUEST_PERMISSION_GALLERY);
    }

    public int getClickedPosition(View view) {
        int clickedPosition = Constants.DEFAULT_UNSELECTED_POSITION;
        for (ImageView imageButton : images) {
            if (imageButton.getId() == view.getId()) {
                clickedPosition = images.indexOf(imageButton);
                break;
            }
        }
        return clickedPosition;
    }

    private void showPickImageDialog() {
        final String[] items = fragment.getResources().getStringArray(R.array.pick_image_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setTitle(fragment.getString(R.string.pick_image_title));
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
        if (ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_GALLERY);
        } else {
            launchGallery();
        }
    }

    private void checkPermissionsForCamera() {
        if (ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            fragment.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CAMERA);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(Constants.ACTION_PICK_IMAGE_CAMERA);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFileForProfileFromCamera()));
        fragment.startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        fragment.startActivityForResult(photoPickerIntent, SELECT_FILE);
    }


    private File getFileForProfileFromCamera() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + cameraImageName + "_" + positionToUpdateImage + ".png"); //TODO
    }

    public interface ImageResultListener {
        void onImageReceived(String url, int position);

        void onImageCanceledOrFailed(int position);
    }
}
