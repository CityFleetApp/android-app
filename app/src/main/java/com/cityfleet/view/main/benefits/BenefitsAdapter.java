package com.cityfleet.view.main.benefits;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.Benefit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 12.03.16.
 */
public class BenefitsAdapter extends RecyclerView.Adapter<BenefitsAdapter.ViewHolder> {
    private List<Benefit> benefitList = new ArrayList<Benefit>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.benefitName)
        TextView benefitName;
        @Bind(R.id.benefitImage)
        ImageView benefitImage;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public BenefitsAdapter(Context context) {
        this.context = context;
    }

    public void setBenefitList(List<Benefit> benefitList) {
        this.benefitList.clear();
        this.benefitList.addAll(benefitList);
    }

    public Benefit getBenefit(int position){
        return benefitList.get(position);
    }
    @Override
    public BenefitsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.benefit_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Benefit benefit = benefitList.get(position);
        if (!TextUtils.isEmpty(benefit.getImage())) {
            Picasso.with(context).load(benefit.getImage()).into(holder.benefitImage);
        }
        holder.benefitName.setText(benefit.getName());
    }

    @Override
    public int getItemCount() {
        return benefitList.size();
    }
}