package com.example.stocks;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PortfolioSection extends Section{

    String title="PORTFOLIO";
    List<PortfolioAdapter> list;
    double remaining;
    Context context;
    DecimalFormat formatter = new DecimalFormat("#,##0.00");

    PortfolioSection(@NonNull final List<PortfolioAdapter> list,double remaining,Context context)
    {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.portfolio_header)
                .build());
        this.list=list;
        this.remaining=remaining;
        this.context=context;
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
        PortfolioAdapter portfolioAdapter = list.get(position);
        itemHolder.ticker.setText(portfolioAdapter.ticker);
        itemHolder.companyName.setText(""+portfolioAdapter.quantity+" shares");
        itemHolder.lastPrice.setText("" + formatter.format(portfolioAdapter.currentPrice));
        if (portfolioAdapter.change > 0)
        {
            itemHolder.change.setText("" + formatter.format(portfolioAdapter.change));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.green));
            itemHolder.trending.setImageResource(R.drawable.ic_twotone_trending_up_24);
        }
        else if (portfolioAdapter.change < 0)
        {
            itemHolder.change.setText("" + formatter.format(Math.abs(portfolioAdapter.change)));
            itemHolder.change.setTextColor(context.getResources().getColor(R.color.darkRed));
            itemHolder.trending.setImageResource(R.drawable.ic_baseline_trending_down_24);
        }
        else
        {
            itemHolder.change.setText("" + formatter.format(portfolioAdapter.change));
            itemHolder.trending.setVisibility(View.INVISIBLE);
        }
        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next=new Intent(context,Details.class);
                next.putExtra("ticker", portfolioAdapter.ticker);
                context.startActivity(next);
            }
        });
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
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new PortfolioHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
        headerHolder.portHeader.setText(title);
        double total=0.0;
        for(PortfolioAdapter p:list)
        {
            total+=p.currentPrice*p.quantity;
        }
        total+=remaining;
        headerHolder.netWorth.setText(""+formatter.format(total));
    }
}
