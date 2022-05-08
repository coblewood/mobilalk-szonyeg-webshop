package com.example.szonyegwebshop;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class SzonyegItemAdapter extends RecyclerView.Adapter<SzonyegItemAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = SzonyegItemAdapter.class.getName();
    private ArrayList<Szonyeg> szonyegItemsData;
    private ArrayList<Szonyeg> szonyegItemsDataAll;
    private Context context;
    private int lastPosition = -1;

    SzonyegItemAdapter(Context context, ArrayList<Szonyeg> itemsData){
        this.szonyegItemsData = itemsData;
        this.szonyegItemsDataAll = itemsData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SzonyegItemAdapter.ViewHolder holder, int position) {
        Szonyeg currentItem = szonyegItemsData.get(position);
        holder.bindTO(currentItem);
        if (holder.getAdapterPosition() > lastPosition){
            Random r = new Random();
            int random = r.nextInt(10);
            Animation animation;
            if (random > 5){
                animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            }
            holder.itemView.startAnimation(animation);


            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return szonyegItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Szonyeg> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if (charSequence == null || charSequence.length() == 0){
                results.count = szonyegItemsDataAll.size();
                results.values = szonyegItemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Szonyeg item : szonyegItemsDataAll){
                    if (item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            results.count = filteredList.size();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            szonyegItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitleText;
        private TextView mDescriptionText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.itemTitle);
            mDescriptionText = itemView.findViewById(R.id.itemDescription);
            mPriceText = itemView.findViewById(R.id.itemPrice);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bindTO(Szonyeg currentItem) {
            mTitleText.setText(currentItem.getName());
            mDescriptionText.setText(currentItem.getDescription());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRating());
            Glide.with(context).load(currentItem.getImage()).into(mItemImage);

            itemView.findViewById(R.id.addToCart).setOnClickListener(view -> ((ShopActivity)context).updateAlertIcon(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ShopActivity)context).deleteItem(currentItem));
        }
    }
}




