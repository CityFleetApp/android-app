package com.citifleet.view.main.marketplace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.JobOffer;
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
 * Created by vika on 28.03.16.
 */
public class JobOffersAdapter extends RecyclerView.Adapter<JobOffersAdapter.ViewHolder> {
    private SimpleDateFormat intputDateTimeFormatter = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT, Locale.ENGLISH);
    private SimpleDateFormat outputTimeFormatter = new SimpleDateFormat(Constants.OUTPUT_TIME_FORMAT, Locale.ENGLISH);
    private SimpleDateFormat outputDateFormatter = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT, Locale.ENGLISH);

    private List<JobOffer> list = new ArrayList<JobOffer>();
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(JobOffer item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.timePrice)
        TextView timePrice;
        @Bind(R.id.description)
        TextView description;
        @Bind(R.id.covered)
        TextView covered;
        @Bind(R.id.date)
        TextView date;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public JobOffersAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setList(List<JobOffer> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    @Override
    public JobOffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_offer_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JobOffer jobOffer = list.get(position);
        String timePrice = "";
        String time = "";
        String date = "";
        try {
            Date datetime = intputDateTimeFormatter.parse(jobOffer.getDateTime());
            time = outputTimeFormatter.format(datetime);
            date = outputDateFormatter.format(datetime);
            timePrice = context.getString(R.string.time_price, time, jobOffer.getFare());
        } catch (ParseException e) {
            Log.e(JobOffersAdapter.class.getName(), e.getMessage());
        }
        holder.timePrice.setText(timePrice);
        holder.date.setText(date);
        holder.description.setText(jobOffer.getInstructions());
        holder.covered.setText(jobOffer.getStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(jobOffer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
