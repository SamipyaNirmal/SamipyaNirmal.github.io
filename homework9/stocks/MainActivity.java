package com.example.stocks;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RequestQueue queue;
    private static final String[] sAutocompleteColNames = new String[] {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1
    };
    public static final String mypreference = "StockPrefrences";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RecyclerView scroll;
    NestedScrollView scrollView;
    ProgressBar progressBar;
    TextView fetch;
    List<Favorites> favorites;
    List<Portfolio> portfolio;
    List<FavoritesAdapter> favAdapter;
    List<PortfolioAdapter> portAdapter;
    private SectionedRecyclerViewAdapter sectionedAdapter;
    Gson gson=new Gson();
    TextView date;
    private Handler taskHandler;
    private Runnable repeatativeTaskRunnable;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queue= Volley.newRequestQueue(this);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        TextView txt= (TextView) findViewById(R.id.footer);
        txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.tiingo.com/"));
                startActivity(browserIntent);
            }
        });
        portAdapter=new ArrayList<>();
        favAdapter=new ArrayList<>();
        progressBar=(ProgressBar)findViewById(R.id.ProgressBar);
        scroll=(RecyclerView)findViewById(R.id.recyclerview);
        fetch=(TextView)findViewById(R.id.fetch);
        scrollView=(NestedScrollView)findViewById(R.id.scrollView);
        if(!sharedPreferences.contains("portfolio") && !sharedPreferences.contains("favorites"))
        {
            progressBar.setVisibility(View.GONE);
            fetch.setVisibility(View.GONE);
            addAdapter();
            scroll.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        }
        if(!sharedPreferences.contains("remainingAmount"))
        {
            editor.putFloat("remainingAmount",20000.00f);
            editor.commit();
        }
        date=(TextView)findViewById(R.id.date);
        String pattern = "MMMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currdate = simpleDateFormat.format(new Date());
        date.setText(currdate);
        taskHandler = new android.os.Handler();
        repeatativeTaskRunnable = new Runnable() {
            public void run() {
                displayPortfolio();
                displayFavorites();
                taskHandler.postDelayed(this,15000);
            }
        };
//        taskHandler.post(repeatativeTaskRunnable);
        scroll.setLayoutManager(new LinearLayoutManager(this));
        scroll.addItemDecoration(new DividerItemDecoration(scroll.getContext(), DividerItemDecoration.VERTICAL));
        enableSwipeToDelete();
        Window window=this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.splash));
        window.setNavigationBarColor(getResources().getColor(R.color.black));
    }

    void stopHandler()
    {
        taskHandler.removeCallbacks(repeatativeTaskRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskHandler.post(repeatativeTaskRunnable);
        progressBar.setVisibility(View.VISIBLE);
        fetch.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        if(!sharedPreferences.contains("portfolio") && !sharedPreferences.contains("favorites"))
        {
            progressBar.setVisibility(View.GONE);
            fetch.setVisibility(View.GONE);
            addAdapter();
            scroll.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final boolean[] selectFlag = {false};
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() >= 3) {
                    MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);
                    String url = "https://homework9cloud.wl.r.appspot.com/autocomplete/" + query;
                    JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
//                                    Log.d(TAG, "onResponse: "+response.toString());
                                JSONArray array= response;
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject row = array.getJSONObject(i);
                                    String ticker = row.getString("ticker");
                                    String name = row.getString("name");
                                    Object[] r = new Object[]{i, ticker + " - " + name};
                                    cursor.addRow(r);
                                }
                                searchView.getSuggestionsAdapter().changeCursor(cursor);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error response", error.toString());
                        }
                    });
                    jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(6000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(jsonArrayRequest);
                }
                else
                {
                    searchView.getSuggestionsAdapter().changeCursor(null);
                }
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (selectFlag[0])
                {
                    searchView.getSuggestionsAdapter().changeCursor(null);
                    Intent intent = new Intent(MainActivity.this, Details.class);
                    selectFlag[0]=false;
                    intent.setAction(Intent.ACTION_SEARCH);
                    String[] ticker=query.split("-");
                    intent.putExtra("ticker", ticker[0]);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(term,false);
                selectFlag[0] =true;
                return true;
            }
            @Override
            public boolean onSuggestionClick(int position)
            {
                return onSuggestionSelect(position);
            }
        });
        return true;
    }
    public void displayPortfolio()
    {
        String json=sharedPreferences.getString("portfolio","");
        Type type = new TypeToken<List<Portfolio>>(){}.getType();
        portfolio = gson.fromJson(json, type);
        String query="";
        Map<String,Integer> portfolio_index=new HashMap<>();
        int i=0;
        if(portfolio!=null)
        {
            for(Portfolio p:portfolio)
            {
                query+=p.ticker.trim()+",";
                portfolio_index.put(p.ticker.trim(),i++);
            }
            PortfolioAdapter[] port=new PortfolioAdapter[portfolio_index.size()];
            if(!query.equals(""))
            {
                String portUrl = "https://homework9cloud.wl.r.appspot.com/portfolio/" + query;
                JsonArrayRequest portfolioRequest=new JsonArrayRequest(Request.Method.GET, portUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            Log.d(TAG, "onResponse: "+response.toString());
                            for(int i=0;i<response.length();i++)
                            {
                                JSONArray row=response.getJSONArray(i);
                                PortfolioAdapter obj=new PortfolioAdapter();
                                obj.ticker=row.getString(0);
                                obj.currentPrice=row.getDouble(1);
                                obj.change=Double.parseDouble(row.getString(2));
                                String portfoliojson=sharedPreferences.getString("portfolio","");
                                Type type=new TypeToken<List<Portfolio>>(){}.getType();
                                List<Portfolio> temp=gson.fromJson(portfoliojson,type);
                                for(Portfolio p:temp)
                                {
                                    if(p.ticker.trim().equals(obj.ticker))
                                    {
                                        obj.quantity=p.quantity;
                                    }
                                }
                                port[portfolio_index.get(obj.ticker)]=(obj);
                            }
                            //setAdapter of p
                            List<PortfolioAdapter> portList=new ArrayList<>(Arrays.asList(port));
                            portAdapter=portList;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        fetch.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        addAdapter();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error response", error.toString());
                    }
                });
                portfolioRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(portfolioRequest);
            }
            else
            {
                portAdapter=new ArrayList<>();
                addAdapter();
                progressBar.setVisibility(View.GONE);
                fetch.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        }
    }
    public void displayFavorites()
    {
//        Log.d(TAG, "displayFavorites: ");
        String json=sharedPreferences.getString("favorites","");
        Type type=new TypeToken<List<Favorites>>(){}.getType();
        favorites=gson.fromJson(json,type);
        String query="";
        Map<String,Integer> fav_index=new HashMap<>();
        int i=0;
        if(favorites!=null)
        {
            for(Favorites s:favorites)
            {
                query+=s.ticker.trim()+",";
                fav_index.put(s.ticker.trim(),i++);
            }
            FavoritesAdapter[] l=new FavoritesAdapter[fav_index.size()];
            if(!query.equals(""))
            {
                String watchlistUrl = "https://homework9cloud.wl.r.appspot.com/watchlist/" + query;
                JsonArrayRequest jsonWatchlistRequest = new JsonArrayRequest(Request.Method.GET, watchlistUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONArray row = response.getJSONArray(i);
                                FavoritesAdapter obj = new FavoritesAdapter();
                                obj.ticker = row.getString(0);
                                obj.currentPrice = row.getDouble(1);
                                obj.change = Double.parseDouble(row.getString(2));
                                for (Favorites s : favorites) {
                                    if (s.ticker.trim().equals(obj.ticker)) {
                                        obj.companyName = s.companyName;
                                        break;
                                    }
                                }
                                if (sharedPreferences.contains("portfolio")) {
                                    String portfoliojson = sharedPreferences.getString("portfolio", "");
                                    Type type = new TypeToken<List<Portfolio>>() {
                                    }.getType();
                                    List<Portfolio> temp = gson.fromJson(portfoliojson, type);
                                    if (temp != null)
                                        for (Portfolio p : temp) {
                                            if (p.ticker.trim().equals(obj.ticker))
                                            {
                                                obj.quantity = p.quantity;
                                                break;
                                            }
                                        }
                                }
                                int index = fav_index.get(obj.ticker);
                                l[index] = obj;
                            }
                            //setAdapter of l
                            List<FavoritesAdapter> adapterList = new ArrayList<FavoritesAdapter>(Arrays.asList(l));
                            favAdapter = adapterList;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        fetch.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        addAdapter();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error response", error.toString());
                    }
                });
                jsonWatchlistRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonWatchlistRequest);
            }
            else
            {
                favAdapter=new ArrayList<>();
                addAdapter();
                progressBar.setVisibility(View.GONE);
                fetch.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        }
    }
    public void addAdapter()
    {
        sectionedAdapter=new SectionedRecyclerViewAdapter();
        double remainingAmount=(double) sharedPreferences.getFloat("remainingAmount",0.0f);
        sectionedAdapter.addSection(new PortfolioSection(portAdapter,remainingAmount,MainActivity.this));
        sectionedAdapter.addSection(new FavoritesSection(favAdapter,MainActivity.this));
        scroll.setAdapter(sectionedAdapter);
    }
    public void enableSwipeToDelete()
    {
        SwipeToDeleteCallback.ItemTouchHelperContract obj=new SwipeToDeleteCallback.ItemTouchHelperContract() {
            @Override
            public void onRowMoved(int fromPosition, int toPosition)
            {
                int portfolioStart=1;
                int portfolioLast=portAdapter.size();
                int favStart=portfolioLast+2;
                int favLast=favStart+favAdapter.size()-1;
//                Log.d(TAG, "from"+fromPosition+" to"+toPosition);
                if((fromPosition>=portfolioStart && fromPosition<=portfolioLast) && (toPosition>=portfolioStart && toPosition<=portfolioLast))
                {
                    String json=sharedPreferences.getString("portfolio","");
                    Type type = new TypeToken<List<Portfolio>>(){}.getType();
                    List<Portfolio> temp = gson.fromJson(json, type);
                    Collections.swap(temp,fromPosition-1,toPosition-1);
                    Collections.swap(portAdapter,fromPosition-1,toPosition-1);
                    String rjson=gson.toJson(temp);
                    editor.putString("portfolio",rjson);
                    editor.commit();
                    sectionedAdapter.notifyItemMoved(fromPosition,toPosition);
                }
                else if((fromPosition>=favStart && fromPosition<=favLast) && (toPosition>=favStart && toPosition<=favLast))
                {
                    int updatedFromPosition=fromPosition-portAdapter.size()-2;
                    int updatedToPosition=toPosition-portAdapter.size()-2;
                    String json=sharedPreferences.getString("favorites","");
                    Type type = new TypeToken<List<Favorites>>(){}.getType();
                    List<Favorites> temp = gson.fromJson(json, type);
                    Collections.swap(temp,updatedFromPosition,updatedToPosition);
                    Collections.swap(favAdapter,updatedFromPosition,updatedToPosition);
                    String rjson=gson.toJson(temp);
                    editor.putString("favorites",rjson);
                    editor.commit();
                    sectionedAdapter.notifyItemMoved(fromPosition,toPosition);
                }
            }
            @Override
            public void onRowSelected(ItemViewHolder myViewHolder) {
                myViewHolder.rootView.setBackgroundColor(Color.GRAY);
            }
            @Override
            public void onRowClear(ItemViewHolder myViewHolder) {
                myViewHolder.rootView.setBackgroundColor(Color.WHITE);
            }
        };
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this,obj) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                int portfolioPosition=portAdapter.size()+1;
                int originalPosition=position;
                position=position-portfolioPosition-1;
                if(position>=0)
                {
                    FavoritesAdapter delItem=favAdapter.get(position);
//                Log.d(TAG, "onSwiped: "+favAdapter.get(position).ticker);
                    favAdapter.remove(position);
                    String json=sharedPreferences.getString("favorites","");
                    Type type=new TypeToken<List<Favorites>>(){}.getType();
                    favorites=gson.fromJson(json,type);
                    for(Favorites f:favorites)
                    {
                        if(f.ticker.trim().equals(delItem.ticker))
                        {
                            favorites.remove(f);
                            break;
                        }
                    }
                    String rjson=gson.toJson(favorites);
                    editor.putString("favorites",rjson);
                    editor.commit();
                    sectionedAdapter.notifyItemRemoved(originalPosition);
                }
                else
                {
                    addAdapter();
                }
            }
        };
        ItemTouchHelper itc = new ItemTouchHelper(swipeToDeleteCallback);
        itc.attachToRecyclerView(scroll);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
    }
}