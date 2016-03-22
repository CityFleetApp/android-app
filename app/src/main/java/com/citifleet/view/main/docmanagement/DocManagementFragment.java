package com.citifleet.view.main.docmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 22.03.16.
 */
public class DocManagementFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind({R.id.doc1Image, R.id.doc2Image, R.id.doc3Image, R.id.doc4Image, R.id.doc5Image, R.id.doc6Image, R.id.doc7Image, R.id.doc8Image})
    List<ImageView> docImages;
    @Bind({R.id.doc1Title, R.id.doc2Title, R.id.doc3Title, R.id.doc4Title, R.id.doc5Title, R.id.doc6Title, R.id.doc7Title, R.id.doc8Title})
    List<TextView> docTitles;
    @Bind({R.id.doc1ExpDate, R.id.doc2ExpDate, R.id.doc3ExpDate, R.id.doc4ExpDate, R.id.doc5ExpDate, R.id.doc6ExpDate, R.id.doc7ExpDate, R.id.doc8ExpDate})
    List<TextView> docFields;
    @Bind({R.id.doc1SaveBtn, R.id.doc2SaveBtn, R.id.doc3ExpDate, R.id.doc4ExpDate, R.id.doc5ExpDate, R.id.doc6ExpDate, R.id.doc7ExpDate, R.id.doc8ExpDate})
    List<TextView> docSaveBtns;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.docmanagement_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.doc_management);
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
}
