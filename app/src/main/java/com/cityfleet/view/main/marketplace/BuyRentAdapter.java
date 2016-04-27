package com.cityfleet.view.main.marketplace;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.Car;
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
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClicked(Car car, View view);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

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
        @Bind(R.id.detailsText)
        TextView detailsText;
        @Bind(R.id.carDetailsBtn)
        LinearLayout carDetailsBtn;
        @Bind(R.id.arrowImage)
        ImageView arrowImage;
        @Bind(R.id.container)
        RelativeLayout container;
        boolean isDetailsExpanded = false;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public BuyRentAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Car> carList) {
        this.list.addAll(carList);
    }

    @Override
    public BuyRentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketplace_rent_sale_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Car car = list.get(position);
        final int[] dimensions = car.getDimensions();
        if (dimensions != null) {
            holder.carImage.post(new Runnable() {
                @Override
                public void run() {
                    int width = dimensions[0];
                    int height = dimensions[1];
                    int viewHeight = holder.carImage.getWidth() * height / width;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.carImage.getLayoutParams();
                    lp.height = viewHeight;
                    holder.carImage.setLayoutParams(lp);
                }
            });
        }
        if (car.getPhotos() != null && !car.getPhotos().isEmpty() && !TextUtils.isEmpty(car.getPhotos().get(0).getUrl())) {
            Picasso.with(context).load(car.getPhotos().get(0).getUrl()).fit().centerCrop().into(holder.carImage);
        }
        holder.carPrice.setText(context.getString(R.string.dollar_price, car.getPrice()));
        holder.carTitle.setText(car.getYear() + " " + car.getMake() + " " + car.getModel());
        holder.colorLbl.setText(car.getColor());
        holder.fuelLbl.setText(car.getFuel());
        holder.seatsLbl.setText(String.valueOf(car.getSeats()));
        holder.typeLbl.setText(car.getType());
        holder.modelLbl.setText(car.getModel());
        holder.detailsText.setText(car.getDescription());
        if (holder.isDetailsExpanded) {
            holder.detailsText.setVisibility(View.VISIBLE);
            holder.arrowImage.setSelected(true);
        } else {
            holder.detailsText.setVisibility(View.GONE);
            holder.arrowImage.setSelected(false);
        }
        holder.carDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isDetailsExpanded) {
                    holder.detailsText.setVisibility(View.GONE);
                    holder.arrowImage.setSelected(false);
                    holder.isDetailsExpanded = false;
                } else {
                    holder.detailsText.setVisibility(View.VISIBLE);
                    holder.arrowImage.setSelected(true);
                    holder.isDetailsExpanded = true;
                }

            }
        });
        if (clickListener != null) {
            holder.carImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(car, holder.container);
                }
            });
        }
        ViewCompat.setTransitionName(holder.container, String.valueOf(position) + "_container");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
