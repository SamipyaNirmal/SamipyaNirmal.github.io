package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class FavoritesHeaderViewHolder  extends RecyclerView.ViewHolder{
    View rootView;
    TextView header;
    FavoritesHeaderViewHolder(@NonNull View view) {
        super(view);
        rootView = view;
        header=(TextView)view.findViewById(R.id.favHeader);
    }
}
