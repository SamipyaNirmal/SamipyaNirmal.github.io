package com.example.stocks;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    Context context;
    List<PortfolioAdapter> list;

    public PortfolioRecyclerAdapter(Context context,List<PortfolioAdapter> list)
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
        PortfolioAdapter portfolioAdapter = list.get(position);
        itemHolder.ticker.setText(portfolioAdapter.ticker);
        itemHolder.companyName.setText(""+portfolioAdapter.quantity+" shares");
        itemHolder.lastPrice.setText("" + portfolioAdapter.currentPrice);
        if (portfolioAdapter.change > 0)
        {
            itemHolder.change.setText("" + portfolioAdapter.change);
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.green));
            itemHolder.trending.setImageResource(R.drawable.ic_twotone_trending_up_24);
        }
        else if (portfolioAdapter.change < 0)
        {
            itemHolder.change.setText("" + Math.abs(portfolioAdapter.change));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.darkRed));
            itemHolder.trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        else
        {
            itemHolder.change.setText("" + portfolioAdapter.change);
            itemHolder.trending.setVisibility(View.INVISIBLE);
        }
        itemHolder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next=new Intent(context,Details.class);
                next.putExtra("ticker", portfolioAdapter.ticker);
                context.startActivity(next);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
