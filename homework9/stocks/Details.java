package com.example.stocks;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Details extends AppCompatActivity {
    private Menu menu;
    private static final String TAG = "Details: ";
    RequestQueue queue;
    String ticker;
    double currentPrice;
    int lineCount;
    TextView symbolTicker,company,lastPrice,change;
    ExpandableHeightGridView gridView;
    TextView description,tradingInfo;
    DecimalFormat formatter = new DecimalFormat("#,##0.00");
    DecimalFormat marketformatter = new DecimalFormat("#,##0.0000");
    int flag=0;
    Button trade;
    ProgressBar progressBar;
    WebView webView;
    boolean resp1=false;
    boolean resp2=false;
    NestedScrollView scrollView;
    TextView fetch;
    boolean dataLoaded=false;
    ImageView firstNewsImage,dialogImage;
    TextView firstSource,firstDate,firstTitle,dialogTitle,show;
    RelativeLayout rl;
    ArrayList<NewsItem> newsList;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static final String mypreference = "StockPrefrences";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson=new Gson();
    boolean fav=false,portfolioPresent=false;
    double updatedQuantity;
    String companyName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queue= Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar=(ProgressBar)findViewById(R.id.ProgressBar);
        scrollView=(NestedScrollView)findViewById(R.id.scroll);
        fetch=(TextView)findViewById(R.id.fetch);
        progressBar.setVisibility(View.VISIBLE);
        fetch.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        lineCount=100;
        Window window=this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.splash));
        window.setNavigationBarColor(getResources().getColor(R.color.black));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        Intent intent=getIntent();
        this.menu=menu;
        ticker=intent.getStringExtra("ticker");
        ticker=ticker.trim();
        recyclerView = (RecyclerView) findViewById(R.id.newsList);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        gridView=(ExpandableHeightGridView) findViewById(R.id.GridView);
        gridView.setExpanded(true);
        symbolTicker=(TextView) findViewById(R.id.ticker);
        company=(TextView) findViewById(R.id.companyName);
        lastPrice=(TextView) findViewById(R.id.lastPrice);
        change=(TextView) findViewById(R.id.change);
        description=(TextView)findViewById(R.id.description);
        show=(TextView) findViewById(R.id.show);
        webView=(WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.getSettings().setDomStorageEnabled(true);
        flag=0;
        show.setText("Show more...");
        trade=(Button)findViewById(R.id.trade);
        firstNewsImage=(ImageView)findViewById(R.id.firstNewsImage);
        firstDate=(TextView)findViewById(R.id.firstDate);
        firstSource=(TextView)findViewById(R.id.firstSource);
        firstTitle=(TextView)findViewById(R.id.firstTitle);
        rl=(RelativeLayout)findViewById(R.id.firstNews);
        tradingInfo=(TextView)findViewById(R.id.tradingInfo);
        makeApiCalls();
        getChartsData();
        if(sharedPreferences.contains("favorites"))
        {
            String json=sharedPreferences.getString("favorites","");
            Type type=new TypeToken<List<Favorites>>(){}.getType();
            List<Favorites> favorites=gson.fromJson(json,type);
            for(Favorites s:favorites)
            {
//                Log.d(TAG, ticker.trim() +" "+s.ticker.trim());
                if(s.ticker.trim().equals(ticker.trim()))
                {
                    fav=true;
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
                }
            }
            if(!fav)
            {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
            }
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_favorite:
                String json=sharedPreferences.getString("favorites","");
                Type type=new TypeToken<List<Favorites>>(){}.getType();
                List<Favorites> favorites=gson.fromJson(json,type);
                if(!fav)
                {
                    if(favorites!=null)
                    {
                        Favorites obj=new Favorites();
                        obj.ticker=ticker;
                        obj.companyName=companyName;
                        favorites.add(obj);
                    }
                    else
                    {
                        favorites=new ArrayList<>();
                        Favorites obj=new Favorites();
                        obj.ticker=ticker;
                        obj.companyName=companyName;
                        favorites.add(obj);
                    }
                    String rjson=gson.toJson(favorites);
                    editor.putString("favorites",rjson);
                    editor.commit();
                    fav=true;
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
                    Toast.makeText(this,'"'+ticker+'"'+ " was added to favorites",Toast.LENGTH_LONG).show();
                }
                else
                {
                    for(Favorites f:favorites)
                    {
                        if(f.ticker.trim().equals(ticker))
                        {
                            favorites.remove(f);
                            fav=false;
                            break;
                        }
                    }
                    String rjson=gson.toJson(favorites);
                    editor.putString("favorites",rjson);
                    editor.commit();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
                    Toast.makeText(this,'"'+ticker+'"'+ " was removed from favorites",Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void makeApiCalls()
    {
        String detailsUrl = "https://homework9cloud.wl.r.appspot.com/details/" + ticker;
        JsonObjectRequest jsonDetailsRequest=new JsonObjectRequest(Request.Method.GET, detailsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                    {
                        if(!response.getString("lastPrice").equals("null"))
                        {
                            currentPrice=response.getDouble("lastPrice");
                            lastPrice.setText("$" + formatter.format(response.getDouble("lastPrice")));
                        }
                            companyName=response.getString("companyName");
                            resp1=true;
                            symbolTicker.setText(response.getString("ticker"));
                            company.setText(response.getString("companyName"));
                            Double c=Double.parseDouble(response.getString("change"));
                            if(c>0)
                            {
                                change.setText("$"+formatter.format(Math.abs(c)));
                                change.setTextColor(getResources().getColor(R.color.green));
                            }
                            else if(c<0)
                            {
                                change.setText("-$"+formatter.format(Math.abs(c)));
                                change.setTextColor(getResources().getColor(R.color.red));
                            }
                            else
                            {
                                change.setText("$"+formatter.format(Math.abs(c)));
                            }
                    }
                catch (Exception e)
                {
                            e.printStackTrace();
                }
                getPortfolioData();
            }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error response", error.toString());
                    }
                });
                jsonDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonDetailsRequest);
                String summaryUrl = "https://homework9cloud.wl.r.appspot.com/summary/" + ticker;
                JsonObjectRequest jsonSummaryRequest=new JsonObjectRequest(Request.Method.GET, summaryUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        fetch.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        try
                        {
                            resp2=true;
                            List<String> list=new ArrayList<>();
                            list.add("Current price: "+formatter.format(response.getDouble("last")));
                            if(response.getString("low")!=null) {
                                Double low = Double.parseDouble(response.getString("low"));
                                list.add("low: "+formatter.format(low));
                            }
                            else
                            {
                                list.add("low: "+0.00);
                            }
                            try
                            {
                                Double bid = Double.parseDouble(response.getString("bidPrice"));
                                list.add("bid Price: " + formatter.format(bid));
                            }
                            catch(NumberFormatException e)
                            {
//                                Log.d(TAG, "bid price"+0.00);
                                list.add("bid Price: " + formatter.format(0));
                            }
                            if(response.getString("open")!=null) {
                                Double open = Double.parseDouble(response.getString("open"));
                                list.add("OpenPrice: " + formatter.format(open));
                            }
                            else
                            {
                                list.add("OpenPrice: " + 0.00);
                            }
                            try {
                                Double mid = Double.parseDouble(response.getString("mid"));
                                list.add("mid: " + formatter.format(mid));
                            }
                            catch (NumberFormatException e)
                            {
                                list.add("mid: " + formatter.format(0)) ;
                            }
                            if(response.getString("high")!=null) {
                                Double high = Double.parseDouble(response.getString("high"));
                                list.add("High: " + formatter.format(high));
                            }
                            else
                            {
                                list.add("High: " + 0.00);
                            }
                            long volume=Long.parseLong(response.getString("volume"));
                            list.add("Volume: "+formatter.format(volume));
                            StatsAdapter s=new StatsAdapter(Details.this,list);
                            gridView.setAdapter(s);
                            description.setText(response.getString("description"));
                            description.post(new Runnable() {
                                @Override
                                public void run() {
                                    lineCount=description.getLineCount();
                                    if(lineCount==1 || lineCount==2)
                                    {
                                        show.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        description.setEllipsize(TextUtils.TruncateAt.END);
                                        description.setMaxLines(2);
                                    }
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error response", error.toString());
                    }
                });
                jsonSummaryRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonSummaryRequest);
        String newsUrl = "https://homework9cloud.wl.r.appspot.com/news/" + ticker;
        JsonArrayRequest jsonNewsRequest=new JsonArrayRequest(Request.Method.GET, newsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try
                {
                    if(response.length()>0) {
                        JSONObject row = response.getJSONObject(0);
                        String url = row.getString("url");
                        url = url.replace("\\", "");
                        String urlToImage = row.getString("urlToImage");
                        urlToImage = urlToImage.replace("\\", "");
                        long posted = row.getLong("publishedAt");
                        String title = row.getString("title");
                        JSONObject sourceObj = row.getJSONObject("source");
                        String source = sourceObj.getString("name");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long curr = timestamp.getTime();
                        long diff = (curr - posted) / 1000;
                        long min = diff / 60;
                        long hours = min / 60;
                        int days = (int) hours / 24;
                        if (min < 60)
                            firstDate.setText(min + " mins ago");
                        else if (hours < 24) {
                            firstDate.setText(hours + " hours ago");
                        } else {
                            firstDate.setText(days + " days ago");
                        }
//                    Log.d(TAG, "onResponse: "+days);
                        firstSource.setText(source);
                        firstTitle.setText(title);
                        Glide.with(Details.this).load(urlToImage).into(firstNewsImage);
                        String finalUrl = url;
                        rl.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(finalUrl));
                                startActivity(browserIntent);
                            }
                        });
                        String finalUrlToImage = urlToImage;
                        rl.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
//                            Log.d(TAG, "onLongClick: ");
                                final Dialog dialog = new Dialog(Details.this);
                                dialog.setContentView(R.layout.dialog);
                                dialogImage = (ImageView) dialog.findViewById(R.id.dialogImage);
                                dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
                                ImageButton twitter = (ImageButton) dialog.findViewById(R.id.twitter);
                                ImageButton chrome = (ImageButton) dialog.findViewById(R.id.chrome);
                                Glide.with(Details.this).load(finalUrlToImage).into(dialogImage);
                                dialogTitle.setText(title);
                                dialog.show();
                                twitter.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse("https://twitter.com/intent/tweet/?text=Check out this Link: " + finalUrl + "&hashtags=CSCI571StockApp"));
                                        startActivity(browserIntent);
                                    }
                                });
                                chrome.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Log.d(TAG, "onClick: here");
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse(finalUrl));
                                        startActivity(browserIntent);
                                    }
                                });
                                return true;
                            }
                        });
                    }
                    newsList=new ArrayList<NewsItem>();
                    for (int i = 1; i < response.length(); i++) {
                        NewsItem obj=new NewsItem();
                        JSONObject r = response.getJSONObject(i);
//                        Log.d(TAG, "onResponse: "+r.toString());
                        String newsUrl=r.getString("url");
                        newsUrl = newsUrl.replace("\\","");
                        String newsurlToImage=r.getString("urlToImage");
                        newsurlToImage=newsurlToImage.replace("\\","");
                        long newsposted=r.getLong("publishedAt");
                        String newstitle=r.getString("title");
                        JSONObject newssourceObj=r.getJSONObject("source");
                        String newssource=newssourceObj.getString("name");
                        obj.imageUrl=newsurlToImage;
                        obj.source=newssource;
                        obj.time=newsposted;
                        obj.title=newstitle;
                        obj.url=newsUrl;
                        newsList.add(obj);
                    }
                    recyclerView.setLayoutManager(linearLayoutManager);
                    NewsAdapter newsAdapter=new NewsAdapter(Details.this,newsList);
                    recyclerView.setAdapter(newsAdapter);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error response", error.toString());
            }
        });
        jsonNewsRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonNewsRequest);
    }
    public void getPortfolioData()
     {
        if(sharedPreferences.contains("portfolio"))
        {
            String json = sharedPreferences.getString("portfolio", "");
            Type type = new TypeToken< List<Portfolio>>() {}.getType();
            List<Portfolio> portfolio= gson.fromJson(json, type);
            for(Portfolio p:portfolio)
            {
//                Log.d(TAG, "getPortfolioData: "+p.ticker+" "+p.quantity);
                if(p.ticker.trim().equals(ticker))
                {
                    portfolioPresent=true;
                    double market=p.quantity*Double.parseDouble(""+currentPrice);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tradingInfo.setText(Html.fromHtml("<div>Shares owned: "+marketformatter.format(p.quantity)+"</div><div>Market Value: $"+formatter.format(market)+"</div>", Html.FROM_HTML_MODE_COMPACT));
                    }
                    else {
                        tradingInfo.setText(Html.fromHtml("<div>Shares owned: "+marketformatter.format(p.quantity)+"</div><div>Market Value: $"+formatter.format(market)+"</div>"));
                    }
                }
            }
            if(!portfolioPresent)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tradingInfo.setText(Html.fromHtml("<div>You have 0 shares of "+ticker+"</div><div>Start trading!</div>", Html.FROM_HTML_MODE_COMPACT));
                }
                else {
                    tradingInfo.setText(Html.fromHtml("<div>You have 0 shares of "+ticker+"</div><div>Start trading!</div>"));
                }
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tradingInfo.setText(Html.fromHtml("<div>You have 0 shares of "+ticker+"</div><div>Start trading!</div>", Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                tradingInfo.setText(Html.fromHtml("<div>You have 0 shares of "+ticker+"</div><div>Start trading!</div>"));
            }
        }
    }
    public void tradeSheet(View view)
    {
        final boolean[] clicked = {false};
        final Dialog dialog = new Dialog(Details.this);
        dialog.setContentView(R.layout.trading_dialog);
        TextView heading=(TextView)dialog.findViewById(R.id.heading);
        heading.setText("Trade "+company.getText()+" shares");
        EditText quantity=(EditText)dialog.findViewById(R.id.quantity);
        TextView calculation=(TextView)dialog.findViewById(R.id.calculation);
        int q = 0;
        calculation.setText(q +" x $"+currentPrice+"/share = $"+(double)Math.round(q *currentPrice));
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!quantity.getText().toString().equals("") && quantity.getText().toString().matches("^[a-zA-Z]*$"))
                {
                    Toast.makeText(Details.this,"Please enter valid amount",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!quantity.getText().toString().equals(""))
                {
                    updatedQuantity=Double.parseDouble(quantity.getText().toString());
                    calculation.setText(updatedQuantity +" x $"+currentPrice+"/share = $"+formatter.format(updatedQuantity *currentPrice));
                }
            }
        });
        TextView remaining=(TextView)dialog.findViewById(R.id.remainingAmount);
        Button buy=(Button)dialog.findViewById(R.id.buy);
        Button sell=(Button)dialog.findViewById(R.id.sell);
        double remainingAmount=sharedPreferences.getFloat("remainingAmount",20000.00f);
        remaining.setText("$"+formatter.format(remainingAmount)+" available to buy "+ticker);
        dialog.show();
        buy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean success=false;
                String json=sharedPreferences.getString("portfolio","");
                float remaining=sharedPreferences.getFloat("remainingAmount",20000.00f);
                Type type=new TypeToken<List<Portfolio>>(){}.getType();
                List<Portfolio> portfolio=gson.fromJson(json,type);
                if(portfolio==null)
                    portfolio=new ArrayList<Portfolio>();
                if(quantity.getText().toString().equals(""))
                {
                    Toast.makeText(Details.this,"Please enter valid amount",Toast.LENGTH_LONG).show();
                    return;
                }
                for(Portfolio p:portfolio)
                {
                    if(p.ticker.trim().equals(ticker))
                    {
//                        Log.d(TAG, "updated quant"+p.quantity);
                        float cost=(float) (updatedQuantity*currentPrice);
                        if(cost>remaining)
                        {
                            success=false;
                            Toast.makeText(Details.this,"Not enough money to buy",Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(updatedQuantity==0)
                        {
                            success=false;
                            Toast.makeText(Details.this,"Cannot buy less than 0 shares",Toast.LENGTH_LONG).show();
                            break;
                        }
                        success=true;
                        double quant=p.quantity;
                        quant+=updatedQuantity;
                        p.quantity=quant;
                        remaining=remaining-(float) (updatedQuantity*currentPrice);
                        break;
                    }
                }
                if(!portfolioPresent)
                {
                    Portfolio obj=new Portfolio();
                    obj.ticker=ticker;
                    obj.quantity=updatedQuantity;
                    float cost=(float) (updatedQuantity*currentPrice);
                    if(updatedQuantity==0)
                    {
                        success=false;
                        Toast.makeText(Details.this,"Cannot buy less than 0 shares",Toast.LENGTH_LONG).show();
                    }
                    else if(cost>remaining)
                    {
                        success=false;
                        Toast.makeText(Details.this,"Not enough money to buy",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
//                        Log.d(TAG, "buy success");
                        success=true;
                        portfolio.add(obj);
                        remaining=remaining-(float)(updatedQuantity*currentPrice);
                    }
                }
                if(success)
                {
//                    Log.d(TAG, "final buy success");
                    dialog.dismiss();
                    String rjson=gson.toJson(portfolio);
                    editor.putString("portfolio",rjson);
//                    remaining=(float)(Math.round(remaining)*100.00f)/100.00f;
                    editor.putFloat("remainingAmount",remaining);
                    editor.commit();
                    getPortfolioData();
                    final Dialog buyDialog = new Dialog(Details.this);
                    buyDialog.setContentView(R.layout.success_dialog);
                    dialog.dismiss();
                    buyDialog.show();
                    Button done=(Button)buyDialog.findViewById(R.id.done);
                    TextView message=(TextView)buyDialog.findViewById(R.id.stocksmessage);
                    message.setText("You have successfully bought "+updatedQuantity+" shares of "+ticker);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buyDialog.dismiss();
                        }
                    });
                }
            }
        });  //buying operation
        sell.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean success=true;
                if(quantity.getText().toString().equals(""))
                {
                    Toast.makeText(Details.this,"Please enter valid amount",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!portfolioPresent)
                {
                    success=false;
                    Toast.makeText(Details.this,"Cannot sell shares",Toast.LENGTH_LONG).show();
                }
                String json=sharedPreferences.getString("portfolio","");
                float remaining=sharedPreferences.getFloat("remainingAmount",20000.00f);
                Type type=new TypeToken<List<Portfolio>>(){}.getType();
                List<Portfolio> portfolio=gson.fromJson(json,type);
                if(portfolio==null)
                    portfolio=new ArrayList<Portfolio>();
                for(Portfolio p:portfolio)
                {
                    if(p.ticker.trim().equals(ticker))
                    {
                        double quant=p.quantity;
                        if(updatedQuantity>quant)
                        {
                            success=false;
                            Toast.makeText(Details.this,"Not enough shares to sell",Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(updatedQuantity==0)
                        {
                            success=false;
                            Toast.makeText(Details.this,"Cannot sell less than 0 shares",Toast.LENGTH_LONG).show();
                            break;
                        }
                        quant-=updatedQuantity;
                        p.quantity=quant;
                        if(p.quantity==0)
                        {
                            portfolio.remove(p);
                            portfolioPresent=false;
                        }
                        remaining=remaining+(float)(updatedQuantity*currentPrice);
                        break;
                    }
                }
                if(success)
                {
                    dialog.dismiss();
                    String rjson=gson.toJson(portfolio);
                    editor.putString("portfolio",rjson);
//                    remaining=(float)Math.round(remaining)*100/100;
                    editor.putFloat("remainingAmount",remaining);
                    editor.commit();
                    getPortfolioData();
                    final Dialog sellDialog = new Dialog(Details.this);
                    sellDialog.setContentView(R.layout.success_dialog);
                    dialog.dismiss();
                    sellDialog.show();
                    Button done=(Button)sellDialog.findViewById(R.id.done);
                    TextView message=(TextView)sellDialog.findViewById(R.id.stocksmessage);
                    message.setText("You have successfully sold "+updatedQuantity+" shares of "+ticker);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sellDialog.dismiss();
                        }
                    });
                }
            }
        });//selling operation
    }
    public void getChartsData()
    {
        webView.loadUrl("file:///android_asset/charts.html");
        webView.setWebViewClient(new WebViewClient()
        {          @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.equals("file:///android_asset/charts.html")) {
                            webView.loadUrl("javascript:getCharts('"+ticker+"');");
                        }
                    }
        });
    }
    public void showMore(View view)
    {
        if(flag==0)
        {
            flag=1;
            show.setText("show less");
            description.setEllipsize(null);
            description.setMaxLines(Integer.MAX_VALUE);
        }
        else
        {
            flag=0;
            show.setText("Show more...");
            description.setEllipsize(TextUtils.TruncateAt.END);
            description.setMaxLines(2);
        }
    }

}