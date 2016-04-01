package com.citifleet.view.main.posting;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.ManagePostModel;
import com.citifleet.model.Notification;
import com.citifleet.model.PostingType;
import com.citifleet.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 31.03.16.
 */
public class ManagePostsAdapter extends RecyclerView.Adapter<ManagePostsAdapter.ViewHolder> {
    private List<ManagePostModel> managePostModels = new ArrayList<ManagePostModel>();
    private SimpleDateFormat intputDateTimeFormatter = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT, Locale.ENGLISH);
    private SimpleDateFormat outputDateFormatter = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT, Locale.ENGLISH);
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(ManagePostModel item);
    }

    public ManagePostsAdapter(Context context, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.postTypeImage)
        ImageView postTypeImage;
        @Bind(R.id.postTitle)
        TextView postTitle;
        @Bind(R.id.postTypeText)
        TextView postTypeText;
        @Bind(R.id.date)
        TextView date;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setManagePostModels(List<ManagePostModel> managePostModels) {
        this.managePostModels.clear();
        this.managePostModels.addAll(managePostModels);
    }

    @Override
    public ManagePostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_post_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ManagePostModel model = managePostModels.get(position);
        if (model.getPostingType().equals(PostingType.RENT_SALE_CAR.getApiName())) {
            holder.postTypeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.car));
            holder.postTypeText.setText(context.getString(R.string.vehicles_for_rent_sale));
            holder.postTitle.setText(model.getYear() + " " + model.getMake() + " " + model.getModel());
        } else if (model.getPostingType().equals(PostingType.GENERAL_GOOD.getApiName())) {
            holder.postTypeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.gear_wheel));
            holder.postTypeText.setText(context.getString(R.string.general_goods_for_sale));
            holder.postTitle.setText(model.getItem());
        } else if (model.getPostingType().equals(PostingType.JOB_OFFER.getApiName())) {
            holder.postTypeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.buss));
            holder.postTypeText.setText(context.getString(R.string.job_offer));
            holder.postTitle.setText(model.getInstructions());
        }
        String date = "";
        try {
            Date datetime = intputDateTimeFormatter.parse(model.getCreated());
            date = outputDateFormatter.format(datetime);
        } catch (ParseException e) {
            Log.e(ManagePostsAdapter.class.getName(), e.getMessage());
        }
        holder.date.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return managePostModels.size();
    }
}
