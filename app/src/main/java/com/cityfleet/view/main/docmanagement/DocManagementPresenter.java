package com.cityfleet.view.main.docmanagement;

import android.text.TextUtils;
import android.util.Log;

import com.cityfleet.model.Document;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 22.03.16.
 */
public class DocManagementPresenter {
    private DocManagementView view;
    private NetworkManager networkManager;

    public DocManagementPresenter(DocManagementView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void createOrUpdateDocument(Document document) {
        if (networkManager.isConnectedOrConnecting()) {
            boolean needToCreate = true;
            RequestBody fileBody = null, expiryDateBody = null, plateNumberBody = null, documentTypeBody = null;
            if (document.getId() == 0 && !document.isExpiryDateUpdated() && !document.isImageUpdated() && !document.isPlateNumberUpdated()) {
                needToCreate = true;
            } else if (document.getId() != 0 && !document.isExpiryDateUpdated() && !document.isImageUpdated() && !document.isPlateNumberUpdated()) {
                return;
            } else {
                needToCreate = false;
            }
            view.startLoading();
            if (needToCreate || document.isImageUpdated()) {
                File file = new File(document.getFile());
                fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            }
            if ((needToCreate && !TextUtils.isEmpty(document.getPlateNumber())) || document.isPlateNumberUpdated()) {
                plateNumberBody = RequestBody.create(MediaType.parse("multipart/form-data"), document.getPlateNumber());
            }
            if ((needToCreate && !TextUtils.isEmpty(document.getExpiryDate())) || document.isExpiryDateUpdated()) {
                expiryDateBody = RequestBody.create(MediaType.parse("multipart/form-data"), document.getExpiryDate());
            }
            if (needToCreate) {
                documentTypeBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(document.getDocumentType()));
            }
            Call<Document> call;
            if (needToCreate) {
                call = networkManager.getNetworkClient().createDocument(fileBody, expiryDateBody, plateNumberBody, documentTypeBody);
            } else {
                call = networkManager.getNetworkClient().updateDocument(document.getId(), fileBody, expiryDateBody, plateNumberBody, documentTypeBody);
            }
            call.enqueue(createDocumentCallback);
        } else {
            view.onNetworkError();
        }
    }


    public void getAllDocuments() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Document>> call = networkManager.getNetworkClient().getDocuments();
            call.enqueue(getDocumetsCallback);
        } else {
            view.onNetworkError();
        }
    }

    private Callback<Document> createDocumentCallback = new Callback<Document>() {
        @Override
        public void onResponse(Call<Document> call, Response<Document> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onDocumentCreatedOrUpdated(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Document> call, Throwable t) {
            Log.e(DocManagementPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    private Callback<List<Document>> getDocumetsCallback = new Callback<List<Document>>() {
        @Override
        public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onDocumentsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<Document>> call, Throwable t) {
            Log.e(DocManagementPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface DocManagementView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onDocumentsLoaded(List<Document> documents);

        void onDocumentCreatedOrUpdated(Document document);
    }
}
