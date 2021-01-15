package com.example.stocks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    final View rootView;
    final TextView ticker;
    final TextView companyName;
    final TextView lastPrice;
    final ImageView trending;
    final TextView change;
    final ImageView link;

    ItemViewHolder(@NonNull View view) {
        super(view);
        rootView = view;
        ticker = view.findViewById(R.id.tickerTextView);
        companyName = view.findViewById(R.id.companyNameTextView);
        lastPrice = view.findViewById(R.id.lastPriceTextView);
        change = view.findViewById(R.id.changeTextView);
        trending = view.findViewById(R.id.trendingImageView);
        link=view.findViewById(R.id.linkButton);
    }
}
