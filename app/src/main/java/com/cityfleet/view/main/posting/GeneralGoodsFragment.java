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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.GeneralGood;
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
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by vika on 21.03.16.
 */
public class GeneralGoodsFragment extends BaseFragment implements PostingGeneralGoodsPresenter.PostingGeneralGoodsDetailView, MultipleImagePickerUtil.ImageResultListener {
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
    RelativeLayout progressBar;
    private Photo[] imageUrls = new Photo[Constants.POSTING_IMAGES_NUMBER];
    private List<Integer> photosToDelete = new ArrayList<Integer>();
    private PostingGeneralGoodsPresenter presenter;
    private MultipleImagePickerUtil imagePickerUtil;
    private boolean isEditMode = false;
    private GeneralGood generalGood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_goods_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.general_goods);
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.GENERAL_GOODS_TAG)) {
            isEditMode = true;
            generalGood = Parcels.unwrap(getArguments().getParcelable(Constants.GENERAL_GOODS_TAG));
            initWithEditValues();
        } else {
            generalGood = new GeneralGood();
        }
        presenter = new PostingGeneralGoodsPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        priceEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        imagePickerUtil = new MultipleImagePickerUtil(this, images, getString(R.string.general_good_posting_name), this);
        return view;
    }

    private void initWithEditValues() {
        itemEt.setText(generalGood.getItem());
        descrEt.setText(generalGood.getDescription());
        priceEt.setText(generalGood.getPrice());
        condition.setText(generalGood.getCondition());
        if (generalGood.getPhotos().size() > 0) {
            imageUrls = generalGood.getPhotos().toArray(imageUrls);
            for (int i = 0; i < generalGood.getPhotos().size(); i++) {
                Picasso.with(getContext()).load(generalGood.getPhotos().get(i).getUrl()).centerCrop().fit().into(images.get(i));
            }
        }
        final String[] conditions = getResources().getStringArray(R.array.general_goods_conditions);
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].equals(generalGood.getCondition())) {
                generalGood.setConditionId(i + 1);
                break;
            }
        }
    }

    @OnClick(R.id.postBtn)
    void onPostBtnClick() {
        if (TextUtils.isEmpty(itemEt.getText().toString()) || TextUtils.isEmpty(descrEt.getText().toString()) || TextUtils.isEmpty(priceEt.getText().toString())
                || generalGood.getConditionId() == Constants.DEFAULT_UNSELECTED_POSITION) {
            Toast.makeText(getActivity(), R.string.posting_empty, Toast.LENGTH_SHORT).show();
        } else {
            generalGood.setPrice(priceEt.getText().toString());
            generalGood.setDescription(descrEt.getText().toString());
            generalGood.setItem(itemEt.getText().toString());
            generalGood.setPhotos(Arrays.asList(imageUrls));
            presenter.createPost(generalGood, isEditMode, photosToDelete);
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
                generalGood.setConditionId(which + 1);
            }
        });
        builder.show();
    }

    @OnClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    void onImageClick(ImageView view) {
        int position = getClickedPosition(view);
        if (imageUrls[position] == null || imageUrls[position].getId() == 0) {
            imagePickerUtil.onImageClick(view);
        }
    }


    @OnLongClick({R.id.imageFirst, R.id.imageSecond, R.id.imageThird, R.id.imageFourth, R.id.imageFifth})
    boolean onLongImageClick(final View view) {
        final int clickedPosition = getClickedPosition(view);
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
