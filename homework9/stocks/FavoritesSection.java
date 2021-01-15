package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class FavoritesSection extends Section {
    String title="FAVORITES";
    List<FavoritesAdapter> list;
    Context context;
    DecimalFormat formatter = new DecimalFormat("#,##0.00");

    FavoritesSection(@NonNull final List<FavoritesAdapter> list, Context c)
    {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.favorite_header)
                .build());
        this.list=list;
        this.context=c;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        FavoritesAdapter favoritesAdapter = list.get(position);
        itemHolder.ticker.setText(favoritesAdapter.ticker);
        if (favoritesAdapter.quantity > 0)
        {
            itemHolder.companyName.setText(""+favoritesAdapter.quantity+" shares");
        }
        else
        {
            itemHolder.companyName.setText(favoritesAdapter.companyName);
        }
        itemHolder.lastPrice.setText("" + formatter.format(favoritesAdapter.currentPrice));
//        itemHolder.change.setText("" + favoritesAdapter.change);
        if (favoritesAdapter.change > 0)
        {
            itemHolder.change.setText("" + formatter.format(favoritesAdapter.change));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.green));
            itemHolder.trending.setImageResource(R.drawable.ic_twotone_trending_up_24);
        }
        else if (favoritesAdapter.change < 0)
        {
            itemHolder.change.setText("" + formatter.format(Math.abs(favoritesAdapter.change)));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.darkRed));
            itemHolder.trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        else
        {
            itemHolder.change.setText("" + formatter.format(favoritesAdapter.change));
            itemHolder.trending.setVisibility(View.INVISIBLE);
        }
        itemHolder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next=new Intent(context,Details.class);
                next.putExtra("ticker", favoritesAdapter.ticker);
                context.startActivity(next);
            }
        });
        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next=new Intent(context,Details.class);
                next.putExtra("ticker", favoritesAdapter.ticker);
                context.startActivity(next);
            }
        });
    }
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new FavoritesHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        FavoritesHeaderViewHolder headerHolder = (FavoritesHeaderViewHolder) holder;
        headerHolder.header.setText(title);
    }

}
