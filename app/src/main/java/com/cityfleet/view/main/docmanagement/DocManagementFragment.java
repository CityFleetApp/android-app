package com.cityfleet.view.main.docmanagement;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.Document;
import com.cityfleet.util.CommonUtils;
import com.cityfleet.util.Constants;
import com.cityfleet.util.PermissionUtil;
import com.cityfleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;

/**
 * Created by vika on 22.03.16.
 */
public class DocManagementFragment extends BaseFragment implements DocManagementPresenter.DocManagementView, DatePickerDialog.OnDateSetListener {
    private static final int REQUEST_CAMERA = 555;
    private static final int SELECT_FILE = 666;
    private static final int REQUEST_PERMISSION_CAMERA = 1;
    private static final int REQUEST_PERMISSION_GALLERY = 2;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    RelativeLayout progressBar;
    @Bind({R.id.doc1Image, R.id.doc2Image, R.id.doc3Image, R.id.doc4Image, R.id.doc5Image, R.id.doc6Image, R.id.doc7Image, R.id.doc8Image})
    List<ImageView> docImages;
    @Bind({R.id.doc1Title, R.id.doc2Title, R.id.doc3Title, R.id.doc4Title, R.id.doc5Title, R.id.doc6Title, R.id.doc7Title, R.id.doc8Title})
    List<TextView> docTitles;
    @Bind({R.id.doc1ExpDate, R.id.doc2ExpDate, R.id.doc3ExpDate, R.id.doc4ExpDate, R.id.doc5ExpDate, R.id.doc6ExpDate, R.id.doc7PlateNumber, R.id.doc8ExpDate})
    List<EditText> docFields;
    @Bind({R.id.doc1SaveBtn, R.id.doc2SaveBtn, R.id.doc3SaveBtn, R.id.doc4SaveBtn, R.id.doc5SaveBtn, R.id.doc6SaveBtn, R.id.doc7SaveBtn, R.id.doc8SaveBtn})
    List<Button> docSaveBtns;
    @BindString(R.string.pick_image_title)
    String pickImageTitle;
    private int positionToUpdate = Constants.DEFAULT_UNSELECTED_POSITION;
    private DocManagementPresenter presenter;
    private Document[] documentsList = new Document[Constants.DOCUMENTS_TYPES_COUNT];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.docmanagement_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.doc_management);
        presenter = new DocManagementPresenter(this, CityFleetApp.getInstance().getNetworkManager());
        presenter.getAllDocuments();
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            getActivity().onBackPressed();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.doc1Image, R.id.doc2Image, R.id.doc3Image, R.id.doc4Image, R.id.doc5Image, R.id.doc6Image, R.id.doc7Image, R.id.doc8Image})
    void onImageClick(View view) {
        int clickedPosition = getClickedImagePosition(view);
        positionToUpdate = clickedPosition;
        showPickImageDialog();
    }

    @OnLongClick({R.id.doc1Image, R.id.doc2Image, R.id.doc3Image, R.id.doc4Image, R.id.doc5Image, R.id.doc6Image, R.id.doc7Image, R.id.doc8Image})
    boolean onImageLongClick(View view) {
        int clickedPosition = getClickedImagePosition(view);
        Document document = documentsList[clickedPosition];
        if (document != null && document.getId() != 0) {
            FragmentManager fm = getChildFragmentManager();
            ImageDialogFragment fragment = (ImageDialogFragment) fm.findFragmentByTag(Constants.DOC_IMAGE_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new ImageDialogFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DOC_IMAGE_PATH, document.getFile());
            fragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.childFragmentContainer, fragment, Constants.DOC_IMAGE_FRAGMENT_TAG).addToBackStack(null).commit();
            return true;
        }
        return false;
    }

    @OnClick({R.id.doc1ExpDate, R.id.doc2ExpDate, R.id.doc3ExpDate, R.id.doc4ExpDate, R.id.doc5ExpDate, R.id.doc6ExpDate, R.id.doc8ExpDate})
    void onExpDateClick(View view) {
        positionToUpdate = getClickedExpDatePosition(view);
        Calendar dateAndTime = Calendar.getInstance();
        dateAndTime.add(Calendar.DAY_OF_YEAR, 1);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(dateAndTime.getTime().getTime());
        dialog.show();
    }

    @OnClick({R.id.doc1SaveBtn, R.id.doc2SaveBtn, R.id.doc3SaveBtn, R.id.doc4SaveBtn, R.id.doc5SaveBtn, R.id.doc6SaveBtn, R.id.doc7SaveBtn, R.id.doc8SaveBtn})
    void onSaveClick(View view) {
        int clickedPosition = getClickedSaveBtnPosition(view);
        Document document = documentsList[clickedPosition];
        if (document != null && !TextUtils.isEmpty(document.getFile()) && (!TextUtils.isEmpty(document.getExpiryDate()) || !TextUtils.isEmpty(document.getPlateNumber()))) {
            document.setDocumentType(clickedPosition + 1);
            presenter.createOrUpdateDocument(document);
        } else if (document == null) {
            Toast.makeText(getActivity(), R.string.enter_all_fields, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(document.getFile())) {
            Toast.makeText(getActivity(), getString(R.string.select_image_empty), Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(document.getPlateNumber()) && clickedPosition == Constants.DOCUMENT_PLATE_NUMBER_POSITION) {
            Toast.makeText(getActivity(), R.string.plate_number_empty, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.ext_date_empty, Toast.LENGTH_SHORT).show();
        }
    }

    @OnTextChanged(value = R.id.doc7PlateNumber, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTextChangedPlateNumber(CharSequence text) {
        Document document = documentsList[Constants.DOCUMENT_PLATE_NUMBER_POSITION];
        if (document == null) {
            document = new Document();
            documentsList[Constants.DOCUMENT_PLATE_NUMBER_POSITION] = document;
        } else if (document != null && !TextUtils.isEmpty(document.getPlateNumber())) {
            document.setPlateNumberUpdated(true);
        }
        document.setPlateNumber(text.toString());
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
                    updateImage(CommonUtils.getImagePath(data.getData(), getContext()));
                } else {
                    positionToUpdate = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    updateImage(getFileForProfileFromCamera().getAbsolutePath());
                } else {
                    positionToUpdate = Constants.DEFAULT_UNSELECTED_POSITION;
                }
                break;
        }
    }

    private void updateImage(String filePath) {
        Document document = documentsList[positionToUpdate];
        if (document == null) {
            document = new Document();
            documentsList[positionToUpdate] = document;
        } else if (document != null && !TextUtils.isEmpty(document.getFile())) {
            document.setImageUpdated(true);
        }
        document.setFile(filePath);
        Picasso.with(getContext()).load(new File(filePath)).fit().centerCrop().into(docImages.get(positionToUpdate));
        positionToUpdate = Constants.DEFAULT_UNSELECTED_POSITION;
    }

    private File getFileForProfileFromCamera() {
        return new File(Environment.getExternalStorageDirectory() + File.separator + "doc" + positionToUpdate + ".png"); //TODO rename file?
    }

    private int getClickedImagePosition(View view) {
        int clickedPosition = Constants.DEFAULT_UNSELECTED_POSITION;
        for (ImageView imageButton : docImages) {
            if (imageButton.getId() == view.getId()) {
                clickedPosition = docImages.indexOf(imageButton);
                break;
            }
        }
        return clickedPosition;
    }

    private int getClickedExpDatePosition(View view) {
        int clickedPosition = Constants.DEFAULT_UNSELECTED_POSITION;
        for (EditText editText : docFields) {
            if (editText.getId() == view.getId()) {
                clickedPosition = docFields.indexOf(editText);
                break;
            }
        }
        return clickedPosition;
    }

    private int getClickedSaveBtnPosition(View view) {
        int clickedPosition = Constants.DEFAULT_UNSELECTED_POSITION;
        for (Button button : docSaveBtns) {
            if (button.getId() == view.getId()) {
                clickedPosition = docSaveBtns.indexOf(button);
                break;
            }
        }
        return clickedPosition;
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
    public void onDocumentsLoaded(List<Document> documents) {
        for (int i = 0; i < documents.size(); i++) {
            int docType = documents.get(i).getDocumentType();
            documentsList[docType - 1] = documents.get(i);
        }
        initDocumentsInUi();
    }

    @Override
    public void onDocumentCreatedOrUpdated(Document document) {
        documentsList[document.getDocumentType() - 1] = document;
    }

    private void initDocumentsInUi() {
        for (int i = 0; i < documentsList.length; i++) {
            Document document = documentsList[i];
            if (document != null) {
                Picasso.with(getActivity()).load(document.getFile()).fit().centerCrop().into(docImages.get(i));
                if (i == Constants.DOCUMENT_PLATE_NUMBER_POSITION) {
                    docFields.get(i).setText(document.getPlateNumber());
                } else {
                    docFields.get(i).setText(document.getExpiryDate());
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (positionToUpdate != Constants.DEFAULT_UNSELECTED_POSITION) {
            Document document = documentsList[positionToUpdate];
            if (document == null) {
                document = new Document();
                documentsList[positionToUpdate] = document;
            } else if (document != null && !TextUtils.isEmpty(document.getExpiryDate())) {
                document.setExpiryDateUpdated(true);
            }
            document.setExpiryDate(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth)); //TODO
            docFields.get(positionToUpdate).setText(document.getExpiryDate());
            positionToUpdate = Constants.DEFAULT_UNSELECTED_POSITION;
        }
    }
}
