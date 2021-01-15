package com.example.stocks;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;


public class StatsAdapter extends BaseAdapter {

    private List<String> mlistData;
    private Context context;
    public StatsAdapter(@NonNull Context context, List<String> l) {
        super();
        this.context=context;
        mlistData = l;
    }
    @Override
    public int getCount()
    {
        return mlistData.size();
    }
    @Nullable
    @Override
    public String getItem(int position)
    {
        return mlistData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv=new TextView(context);
        tv.setText(mlistData.get(position));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        tv.setTextColor(context.getResources().getColor(R.color.black));
        return tv;
    }

    public String getObject(int position) {
        return mlistData.get(position);
    }
}
