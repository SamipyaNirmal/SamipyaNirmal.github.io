import requests,json
from flask import Flask
from datetime import *
from dateutil.relativedelta import *

app = Flask(__name__,static_folder = "static")

@app.route('/',methods=['GET'])
def index():
    return (app.send_static_file('index.html'))

@app.route('/search/<sym>', methods=['GET'])
def searchSymbol(sym):
    stockSymbol=sym
    r=requests.get('https://api.tiingo.com/tiingo/daily/'+stockSymbol+'?token=896ad81fe0ae14d3f0897bd96bda61d4568bb164').content
    return r

@app.route('/getsummary/<sym>', methods=['GET'])
def summary(sym):
    stockSymbol=sym
    r=requests.get('https://api.tiingo.com/iex/'+stockSymbol+'?token=896ad81fe0ae14d3f0897bd96bda61d4568bb164').content
    return r

@app.route('/getcharts/<sym>', methods=['GET'])
def charts(sym):
    stockSymbol=sym
    today=date.today()
    #print(today)
    startdate=today+relativedelta(months=-6)
    #print(startdate)
    r=requests.get('https://api.tiingo.com/iex/'+stockSymbol+'/prices?startDate='+str(startdate)+'&resampleFreq=12hour&columns=open,high,low,close,volume&token=896ad81fe0ae14d3f0897bd96bda61d4568bb164').content
    return r

@app.route('/getnews/<sym>', methods=['GET'])
def news(sym):
    stockSymbol=sym
    s=requests.get('https://api.tiingo.com/iex/'+stockSymbol+'?token=896ad81fe0ae14d3f0897bd96bda61d4568bb164').content
    summary=json.loads(s)
    if(summary[0]["volume"]==0):
        return json.dumps([])
    r=requests.get('https://newsapi.org/v2/everything?q='+stockSymbol+'&apiKey=e5af7fd6f69342038d5ae955da5e0436').content
    x=json.loads(r)
    count=0
    l=x["articles"]
    res=[]
    for item in l:
        if "urlToImage" in item and "url" in item and "title" in item and "publishedAt" in item:
            if item["url"] and item["urlToImage"] and item["title"] and item["publishedAt"]:
                res.append(item)
                count+=1
        if count==5:
            break
    return json.dumps(res)

if __name__=='__main__':
    app.debug=True
    app.run()


