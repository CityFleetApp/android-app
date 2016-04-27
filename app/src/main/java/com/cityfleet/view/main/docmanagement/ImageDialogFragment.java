package com.cityfleet.view.main.docmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cityfleet.R;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 23.03.16.
 */
public class ImageDialogFragment extends BaseFragment {
    @Bind(R.id.image)
    ImageView image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.detail_image_fragment, null);
        ButterKnife.bind(this, v);
        String filePath = getArguments().getString(Constants.DOC_IMAGE_PATH);
        if (filePath.startsWith(Constants.HTTP_SCHEME)) {
            Picasso.with(getActivity()).load(filePath).into(image);
        } else {
            Picasso.with(getActivity()).load(new File(filePath)).into(image);
        }
        return v;
    }

    @OnClick(R.id.imageCont)
    public void onImageContainerClick() {
        getParentFragment().getChildFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
