package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    Context context;
    List<FavoritesAdapter> list;

    public FavoritesRecyclerAdapter(Context context,List<FavoritesAdapter> list)
    {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemHolder, int position) {
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
        itemHolder.lastPrice.setText("" + favoritesAdapter.currentPrice);
        if (favoritesAdapter.change > 0)
        {
            itemHolder.change.setText("" + favoritesAdapter.change);
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.green));
            itemHolder.trending.setImageResource(R.drawable.ic_twotone_trending_up_24);
        }
        else if (favoritesAdapter.change < 0)
        {
            itemHolder.change.setText("" + Math.abs(favoritesAdapter.change));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.darkRed));
            itemHolder.trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        else
        {
            itemHolder.change.setText("" + favoritesAdapter.change);
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

