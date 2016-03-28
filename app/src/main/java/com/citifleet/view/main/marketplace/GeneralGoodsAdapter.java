package com.citifleet.view.main.marketplace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.GeneralGood;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 24.03.16.
 */
public class GeneralGoodsAdapter extends RecyclerView.Adapter<GeneralGoodsAdapter.ViewHolder> {
    private List<GeneralGood> list = new ArrayList<GeneralGood>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.goodsImage)
        ImageView goodsImage;
        @Bind(R.id.goodsPrice)
        TextView goodsPrice;
        @Bind(R.id.goodsTitle)
        TextView goodsTitle;
        @Bind(R.id.typeLbl)
        TextView typeLbl;
        @Bind(R.id.detailsText)
        TextView detailsText;
        @Bind(R.id.goodsDetailsBtn)
        LinearLayout goodsDetailsBtn;
        @Bind(R.id.arrowImage)
        ImageView arrowImage;

        boolean isDetailsExpanded = false;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public GeneralGoodsAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<GeneralGood> generalGoodList) {
        this.list.clear();
        this.list.addAll(generalGoodList);
    }

    @Override
    public GeneralGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_goods_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GeneralGood generalGood = list.get(position);
        holder.goodsImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.goodsImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    holder.goodsImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int[] dimensions = generalGood.getDimensions();
                int width = dimensions[0];
                int height = dimensions[1];
                int viewHeight = holder.goodsImage.getWidth() * height / width;
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.goodsImage.getLayoutParams();
                lp.height = viewHeight;
                holder.goodsImage.setLayoutParams(lp);
            }
        });
        if (generalGood.getPhotos() != null && !generalGood.getPhotos().isEmpty() && !TextUtils.isEmpty(generalGood.getPhotos().get(0))) {
            Picasso.with(context).load(generalGood.getPhotos().get(0)).into(holder.goodsImage);
        }

        holder.goodsPrice.setText(generalGood.getPrice());
        holder.goodsTitle.setText(generalGood.getItem());
        holder.typeLbl.setText(generalGood.getCondition());
        holder.detailsText.setText(generalGood.getDescription());

        if (holder.isDetailsExpanded) {
            holder.detailsText.setVisibility(View.VISIBLE);
            holder.arrowImage.setSelected(true);
        } else {
            holder.detailsText.setVisibility(View.GONE);
            holder.arrowImage.setSelected(false);
        }
        holder.goodsDetailsBtn.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
