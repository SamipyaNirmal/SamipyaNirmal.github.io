package com.example.stocks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.sql.Timestamp;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    ArrayList<NewsItem> items;

    public NewsAdapter(Context context,ArrayList<NewsItem> l)
    {
        this.context=context;
        items=l;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.source.setText(items.get(position).source);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long curr=timestamp.getTime();
        long diff=(curr-items.get(position).time)/1000;
        long min=diff/60;
        long hours=min/60;
        int days=(int)hours/24;
        if(min<60)
        {
            holder.date.setText(min+ " mins ago");
        }
        else if(hours<24)
        {
            holder.date.setText(hours+ " hours ago");
        }
        else
            {
                holder.date.setText(days+ " days ago");
            }
        holder.title.setText(items.get(position).title);
        holder.title.setTextColor(context.getResources().getColor(R.color.black));
        Glide.with(context).load(items.get(position).imageUrl).override(600,1000).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(items.get(position).url));
                context.startActivity(browserIntent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Log.d(TAG, "onLongClick: ");
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                ImageButton twitter=(ImageButton)dialog.findViewById(R.id.twitter);
                ImageButton chrome=(ImageButton) dialog.findViewById(R.id.chrome);
                ImageView dialogImage=(ImageView)dialog.findViewById(R.id.dialogImage);
                TextView dialogTitle=(TextView)dialog.findViewById(R.id.dialogTitle);
                Glide.with(context).load(Uri.parse(items.get(position).imageUrl)).into(dialogImage);
                dialogTitle.setText(items.get(position).title);
                dialog.show();
                twitter.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse("https://twitter.com/intent/tweet/?text=Check out this Link: " + items.get(position).url+"&hashtags=CSCI571StockApp"));
                        context.startActivity(browserIntent);
                    }
                });
                chrome.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
//                        Log.d(TAG, "onClick: here");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(items.get(position).url));
                        context.startActivity(browserIntent);
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.d("NewsAdapter", "getItemCount: "+items.size());
        return items.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView source;
        TextView date;
        TextView title;
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            source=(TextView)itemView.findViewById(R.id.source);
            date=(TextView)itemView.findViewById(R.id.date);
            title=(TextView)itemView.findViewById(R.id.newsTitle);
            image=(ImageView)itemView.findViewById(R.id.newsImage);
        }
    }
}
