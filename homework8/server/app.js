const express=require('express');
const app=express();
const fetch=require('node-fetch');
var cors = require('cors')
app.use(cors())
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
const tiingo_apikey="896ad81fe0ae14d3f0897bd96bda61d4568bb164";
// const tiingo_apikey="768706fca2de3289cbbcb0b97b20c85b212c14e7";
const news_apikey="e5af7fd6f69342038d5ae955da5e0436";

app.get('/autocomplete/:ticker',function(req,res){
    const url="https://api.tiingo.com/tiingo/utilities/search?query="+req.params.ticker+"&token="+tiingo_apikey;
    let o1 = fetch(url)
      .then(resp => resp.json());
    o1.then(json=>{
        r=[];
        var i=0;
        var l=Math.min(json.length,10);
        for(i=0;i<l;i++)
        {
            if(json[i]['name']!=null)
            r.push({'ticker':json[i]['ticker'],'name':json[i]['name']});
        }
        res.send(r);
    });
});

app.get('/details/:ticker',async function(req,res)
{
    const ticker=req.params.ticker;
    //console.log(ticker);
    const companyDescriptionUrl="https://api.tiingo.com/tiingo/daily/"+ticker+"?token="+tiingo_apikey;
    let o1 = fetch( companyDescriptionUrl)
      .then(resp => resp.json());
    const latestStockPriceUrl="https://api.tiingo.com/iex/?tickers="+ticker+"&token="+tiingo_apikey;
    let o2=await fetch(latestStockPriceUrl)
    .then(resp=>resp.json())
    .then(json=>{
        return json;
    });
    Promise.all([o1,o2])
   .then(json => {
       const d=new Date(json[1][0]['timestamp']).getTime();
       //console.log((new Date().getTime()-d)/1000);
        resp = {
            'ticker': json[0]['ticker'],
            'companyName': json[0]['name'],
            'exchangeCode': json[0]['exchangeCode'],
            'lastPrice': json[1][0]['last'],
            'change': (json[1][0]['last'] - json[1][0]['prevClose']).toFixed(2),
            'changePercent': (((json[1][0]['last'] - json[1][0]['prevClose'])/json[1][0]['prevClose'])*100).toFixed(2),
            'marketStatus': ((new Date().getTime()-d)/1000)<=60 ? "Open" : "Close",
            //'marketStatus':"Close",
            'lastTimestamp': json[1][0]['lastSaleTimestamp'],
            'currentTimestamp':new Date().toString()
        }
        res.send(resp);
    }).catch(error => {
    console.error(error.message);
    res.send({});
    }); 
});
app.get('/summary/:ticker',async function(req,res){

    const ticker=req.params.ticker;
    const companyDescriptionUrl="https://api.tiingo.com/tiingo/daily/"+ticker+"?token="+tiingo_apikey;
    let o1 = fetch( companyDescriptionUrl)
      .then(resp => resp.json());
    const latestStockPriceUrl="https://api.tiingo.com/iex/?tickers="+ticker+"&token="+tiingo_apikey;
    let o2=await fetch(latestStockPriceUrl)
    .then(resp=>resp.json())
    .then(json=>{
        return json;
    });
    Promise.all([o1,o2])
   .then(json => {
       const d=new Date(json[1][0]['timestamp']).getTime();
       var c;
       if((json[1][0]['last'] - json[1][0]['prevClose'])<0)
           c="red";
       else if((json[1][0]['last'] - json[1][0]['prevClose'])>0)
           c="green";
       else
           c-"black";
       //console.log((new Date().getTime()-d)/1000);
        resp= {
            'high': json[1][0]['high'],
            'low': json[1][0]['low'],
            'open': json[1][0]['open'],
            'prev': json[1][0]['prevClose'],
            'volume': json[1][0]['volume'],
            'mid': json[1][0]['mid'],
            'askPrice': json[1][0]['askPrice'],
            'askSize': json[1][0]['askSize'],
            'bidPrice': json[1][0]['bidPrice'],
            'bidSize': json[1][0]['bidSize'],
            'description': json[0]['description'],
            'startDate': json[0]['startDate'],
            'marketStatus': ((new Date().getTime()-d)/1000)<=60 ? "Open" : "Close",
            'color':c
        }
       // resp= {
       //     'high': json[1][0]['high'],
       //     'low': json[1][0]['low'],
       //     'open': json[1][0]['open'],
       //     'prev': json[1][0]['prevClose'],
       //     'volume': json[1][0]['volume'],
       //     'mid': 1600,
       //     'askPrice': 1200,
       //     'askSize': 670,
       //     'bidPrice': 1200,
       //     'bidSize': 750,
       //     'description': json[0]['description'],
       //     'startDate': json[0]['startDate'],
       //     'marketStatus': "Open"
       // }
       res.send(resp);
    })
   .catch(error => { 
    console.error(error.message);
       res.send({});
    }); 
});
app.get('/dailycharts/:ticker',async function(req,res){
    let ddate;
    const latestStockPriceUrl="https://api.tiingo.com/iex/?tickers="+req.params.ticker+"&token="+tiingo_apikey;
    let o2= await fetch(latestStockPriceUrl)
    .then(resp=>resp.json())
    .then(json=>{
        ddate=json[0]['lastSaleTimestamp'].substr(0, 10);
        return json;
    }).catch(error => {
            console.error(error.message);
            res.send({});
        });
    //console.log(ddate);
    const dailyChartDataUrl="https://api.tiingo.com/iex/"+req.params.ticker+"/prices?startDate="+ddate+"&resampleFreq=4Min&token="+tiingo_apikey;
    let o3=fetch(dailyChartDataUrl)
    .then(resp=>resp.json()).then(json=> {
            r=[];
            var i=0;
            for(i=0;i<json.length;i++)
            {
                var d=new Date(json[i]['date']);
                var curr=d.getTime();
                r.push([curr,json[i]['close']]);
            }
            res.send(r);
        }).catch(error => {
            console.error(error.message);
            res.send({});
        });
});
app.get('/news/:ticker',function(req,res){

    const newsUrl="https://newsapi.org/v2/everything?apiKey="+news_apikey+"&q="+req.params.ticker;
    let o5=fetch(newsUrl)
    .then(resp=>resp.json()).then(json=>
        {
            resp=[];
            var i=0;
            for(i=0;i<json['articles'].length;i++)
            {
                //console.log(json['articles'][i]['urlToImage']);
                if(json['articles'][i]['source']['name']!=null && json['articles'][i]['urlToImage']!=null && json['articles'][i]['title']!=null && json['articles'][i]['publishedAt']!=null && json['articles'][i]['description']!=null && json['articles'][i]['url']!=null)
                {
                    resp.push(json['articles'][i]);
                   // console.log(json['articles'][i]);
                }
            }
            res.send(resp);
        })
        .catch(error => {
            console.error(error.message);
            res.send({});
        });
});
app.get('/charts/:ticker',function(req,res) {
    var d = new Date();
    d = (d.getFullYear() - 2) + '-' + (d.getMonth() + 1) + '-' + d.getDate();
    const historicalDataUrl = "https://api.tiingo.com/tiingo/daily/" + req.params.ticker + "/prices?startDate=" + d + "&resampleFreq=daily&token=" + tiingo_apikey;
    let o3 = fetch(historicalDataUrl)
        .then(resp => resp.json()).then(json =>
        {
            r=[];
            var i=0;
            for(i=0;i<json.length;i++)
            {
                var d=new Date(json[i]['date']);
                var curr=d.getTime();
                r.push([curr,json[i]['open'],json[i]['high'],json[i]['low'],json[i]['close'],json[i]['volume']]);
            }
            res.send(r);
        })
        .catch(error => {
            console.error(error.message);
            res.send({});
        });
});
app.get('/portfolio/:ticker',async function(req,res){

    const ticker=req.params.ticker;
    //console.log("endpoint hit "+ticker);
    const latestStockPriceUrl="https://api.tiingo.com/iex/?tickers="+ticker+"&token="+tiingo_apikey;
    let o2=await fetch(latestStockPriceUrl)
        .then(resp=>resp.json())
        .then(json=>{
            resp=[];
            var i;
            for( i=0;i<json.length;i++)
            {
                resp.push([json[i]['ticker'],json[i]['last']]);
            }
            res.send(resp);
        }).catch(error => {
            console.error(error.message);
            res.send({});
        });
});
app.get('/watchlist/:ticker',async function(req,res){

    const ticker=req.params.ticker;
    //console.log("endpoint hit "+ticker);
    const latestStockPriceUrl="https://api.tiingo.com/iex/?tickers="+ticker+"&token="+tiingo_apikey;
    let o2=await fetch(latestStockPriceUrl)
        .then(resp=>resp.json())
        .then(json=>{
            resp=[];
            var i;
            for( i=0;i<json.length;i++)
            {
                change=(json[i]['last']-json[i]['prevClose']).toFixed(2);
                changePercent=((change/json[i]['prevClose'])*100).toFixed(2);
                resp.push([json[i]['ticker'],''+json[i]['last'],change,changePercent]);
            }
            res.send(resp);
        }).catch(error => {
            console.error(error.message);
            res.send({});
        });
});

const port=process.env.PORT|| 3000;
app.listen(port,()=>console.log(`listening to ${port}`));

