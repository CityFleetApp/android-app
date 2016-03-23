package com.citifleet.view.main.marketplace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citifleet.R;
import com.citifleet.model.Car;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentAdapter extends RecyclerView.Adapter<BuyRentAdapter.ViewHolder> {
    private List<Car> list = new ArrayList<Car>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public BuyRentAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Car> carList) {
        this.list.clear();
        this.list.addAll(carList);
    }

    @Override
    public BuyRentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_rent_sale_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = list.get(position);
//        if (!TextUtils.isEmpty(benefit.getImage())) {
//            Picasso.with(context).load(benefit.getImage()).into(holder.benefitImage);
//        }
//        holder.benefitName.setText(benefit.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
