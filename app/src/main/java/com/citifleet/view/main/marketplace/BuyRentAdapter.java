package com.citifleet.view.main.marketplace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.Car;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentAdapter extends RecyclerView.Adapter<BuyRentAdapter.ViewHolder> {
    private List<Car> list = new ArrayList<Car>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.carImage)
        ImageView carImage;
        @Bind(R.id.carPrice)
        TextView carPrice;
        @Bind(R.id.carTitle)
        TextView carTitle;
        @Bind(R.id.colorLbl)
        TextView colorLbl;
        @Bind(R.id.fuelLbl)
        TextView fuelLbl;
        @Bind(R.id.modelLbl)
        TextView modelLbl;
        @Bind(R.id.seatsLbl)
        TextView seatsLbl;
        @Bind(R.id.typeLbl)
        TextView typeLbl;
        @Bind(R.id.detailsLbl)
        TextView detailsLbl;

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
        if (!TextUtils.isEmpty(car.getPhoto())) {
            Picasso.with(context).load(car.getPhoto()).into(holder.carImage);
        }
        holder.carPrice.setText(car.getPrice());
        holder.carTitle.setText(car.getDescription());
        holder.colorLbl.setText(car.getColor());
        holder.fuelLbl.setText(car.getFuel());
        holder.seatsLbl.setText(String.valueOf(car.getSeats()));
        holder.typeLbl.setText(car.getType());
        holder.modelLbl.setText(car.getModel());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
