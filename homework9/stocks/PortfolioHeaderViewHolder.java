package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {
    View rootView;
    TextView netWorth,portHeader;
    PortfolioHeaderViewHolder(@NonNull View view) {
        super(view);
        rootView = view;
        netWorth=view.findViewById(R.id.netWorth);
        portHeader=view.findViewById(R.id.portHeader);
    }
}
