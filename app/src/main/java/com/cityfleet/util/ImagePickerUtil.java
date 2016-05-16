package com.cityfleet.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.cityfleet.R;

import java.io.File;

/**
 * Created by vika on 12.05.16.
 */
public class ImagePickerUtil {
    private Fragment fragment;
    public static final int REQUEST_CAMERA = 555;
    public static final int SELECT_FILE = 666;
    public static final int REQUEST_PERMISSION_CAMERA = 1;
    public static final int REQUEST_PERMISSION_GALLERY = 2;
    private ImageResultListener listener;

    public ImagePickerUtil(Fragment fragment, ImageResultListener listener) {
        this.fragment = fragment;
        this.listener = listener;
    }

    public void onDestroy() {
        fragment = null;
    }

    public void onImageClick() {
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
                    listener.onImageReceived(url);
                } else {
                    listener.onImageCanceledOrFailed();
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    String url = getFileForImageFromCamera().getAbsolutePath();
                    listener.onImageReceived(url);
                } else {
                    listener.onImageCanceledOrFailed();
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFileForImageFromCamera()));
        fragment.startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void launchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        fragment.startActivityForResult(photoPickerIntent, SELECT_FILE);
    }

    private File getFileForImageFromCamera() {
        return new File(fragment.getContext().getExternalFilesDir(null) + File.separator + System.currentTimeMillis() + ".png");
    }

    public interface ImageResultListener {
        void onImageReceived(String url);

        void onImageCanceledOrFailed();
    }
}
