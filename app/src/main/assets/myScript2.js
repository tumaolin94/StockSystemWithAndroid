/*
 @author: Maolin Tu
 @Blog: http://tumaolin.com 
*/

var jsonObjIndicator = [];

function formReset(){
    document.getElementById("form1").reset();
    document.getElementById('whole_container').innerHTML = "";
    document.getElementById('container').innerHTML = "";
    document.getElementById('container').style.visibility='hidden';
}

function submitSymbol(symbol){
//    var symbol = "aapl";
    if(symbol==""){
        alert("Please enter a symbol");
        return;
    }
    console.log(symbol);

//    document.getElementById('symbol').value=symbol;
    var url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+symbol;
    console.log(url);

    var xmlhttp;
    if(window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
      xmlhttp.onreadystatechange = function() {
                    if (this.readyState == 4 && this.status == 200) {
                        try{
                            jsonObj = JSON.parse(xmlhttp.responseText);
                             // alert(jsonObj);
                            generateHighStock(jsonObj);
                            console.log(jsonObj);
                        }catch(e){
                            console.log("JSON File Syntax Error");
                            console.log(jsonObj);
                            console.log(e);
                            return null;
                        }
                    }
                };
      xmlhttp.open("GET", url, true);
      xmlhttp.send();
}

        var symbol ;
        var ReDate ;
        var open ;
        var close ;
        var pre_close ;
        var day_range ;
        var volume ;

        var data1 = [];
        var data2 = [];
        var date = [];
        var max = -1000000000;
        var min = 1000000000;
        var volume_max = -1000000000;

function testVariable(){
    if(symbol != null){
        console.log(symbol);

    }else{
         console.log("no symbol");
       }
}

var timestampData = [];
/*
 *Generate Stock Information Table
 *@para jsonObj JSON Object
 */
function generateHighStock(jsonObj){
    if(jsonObj.hasOwnProperty('Error Message')){

    }else{
        var meta = jsonObj['Meta Data'];
        var array_values = jsonObj['Time Series (Daily)'];
        symbol = meta['2. Symbol'];
        ReDate = meta['3. Last Refreshed'];
        var count =0;
        for(var key in array_values){
//            if(count==0){
//                open = array_values[key]['1. open'];
//                close = array_values[key]['4. close'];
//                day_range = array_values[key]['3. low']+"-"+array_values[key]['2. high'];
//                volume = array_values[key]['5. volume'];
//            }
//            if(count == 1){
//                pre_close = array_values[key]['4. close'];
//            }
//            var temp_date = key.substring(5).replace(/-/g, "\/");
//            if(temp_date.length>=6) temp_date = temp_date.substr(0,5);
//            date.push(temp_date);
//            data1.push(parseFloat(array_values[key]['4. close']));
//            data2.push(parseFloat(array_values[key]['5. volume']));
//            max = Math.max(parseFloat(array_values[key]['4. close']),max);
//            min = Math.min(parseFloat(array_values[key]['4. close']),min);
//            volume_max = Math.max(parseFloat(array_values[key]['5. volume']),max);
                var temp = [new Date(key).getTime(), parseFloat(array_values[key]['4. close'])];
            if(count==0) console.log(temp);
            timestampData.push(temp);
            count++;
        }
        console.log(timestampData);
        timestampData.reverse();

        console.log(timestampData);

        drawStock();
    }
}

function drawPrice(){
    drawAreaAndVolume(ReDate, symbol, date,data1,data2,min,max,volume_max);
}

function getIndicator(indicator){
    switch(indicator){
        case "Price": return 0;
        case "SMA": return 1;
        case "EMA": return 2;
        case "STOCH": return 3;
        case "RSI": return 4;
        case "ADX": return 5;
        case "CCI": return 6;
        case "BBANDS": return 7;
        case "MACD": return 8;
    }
}


function fetchAllIndicator(symbol){

    fetchOneIndicator(symbol,"SMA");
    fetchOneIndicator(symbol,"EMA");
    fetchOneIndicator(symbol,"RSI");
    fetchOneIndicator(symbol,"ADX");
    fetchOneIndicator(symbol,"CCI");
    fetchOneIndicator(symbol,"STOCH");
    fetchOneIndicator(symbol,"BBANDS");
    fetchOneIndicator(symbol,"MACD");


}

function showChart(indicator){
    var index = getIndicator(indicator);
    var number;
    if(index == 3) number = 2;
    else if(index == 7||index == 8) number = 3;
    else number = 1;
    console.log(jsonObjIndicator[index]);
    if(index == 0){
        drawAreaAndVolume();
    }else{
        generateChart(index,indicator,number);
    }

}
function drawStock(){
    document.getElementById('container').style.visibility='visible';
    console.log(timestampData);

    var chartTitle = "Stock Price (" +ReDate+ ")";
//    var myChart =
    var options = {
          chart: {
            zoomType: 'x'
          },
          title: {text: symbol + ' Stock Value'},
          subtitle: {
                  useHTML:true,
                  text: "<a style=' text-decoration: none' href='https://www.alphavantage.co/'  target='_blank' >Source: Alpha Vantage</a>"
                  },
          series: [{
            name: symbol + ' Stock Value',
            type: 'area',
            data: timestampData,
            tooltip: {
              valueDecimals: 2
            }
          }]
        };
     var myChart = Highcharts.stockChart('container', options);
}
/*function testMultiple, Draw different kind of charts*/
function drawCharts(indicator, symbol, number){
    var url = "";
    if(number=='3'){
        url = "https://www.alphavantage.co/query?function="+indicator+"&symbol="+symbol+"&interval=daily&time_period=5&series_type=close&nbdevup=3&nbdevdn=3&apikey=FEY146OML7L2A34X";
    }else if(number=='2'){
        url = "https://www.alphavantage.co/query?function="+indicator+"&symbol="+symbol+"&interval=daily&slowkmatype=1&slowdmatype=1&apikey=FEY146OML7L2A34X";
    }else if(number=='1'){
        url = "https://www.alphavantage.co/query?function="+indicator+"&symbol="+symbol+"&interval=daily&time_period=10&series_type=close&apikey=FEY146OML7L2A34X";
    }else{
        alert('You need to implement your own url!');
    }

    console.log(url);
    var xmlhttp;
        if(window.XMLHttpRequest) {
                    xmlhttp = new XMLHttpRequest();
                } else {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
        xmlhttp.onreadystatechange = function() {
                    if (this.readyState == 4 && this.status == 200) {
                        try{
                            jsonObj = JSON.parse(xmlhttp.responseText);
                             generateChart(jsonObj, indicator, number);
                            console.log(jsonObj);
                        }catch(e){
                            alert("JSON File Syntax Error");
                            console.log(jsonObj);
                            console.log(e);
                            return null;
                        }
                    }
                };
      xmlhttp.open("GET", url, true);
      xmlhttp.send();
}

/*function testMultiple, Draw different kind of charts
* @para jsonObj Object of JSON, indicator
*       indicator indicator's name
        number line number
**/
function generateChart(index, indicator, number){
        var jsonObj = JSON.parse(jsonObjIndicator[index]);
        if(!jsonObj.hasOwnProperty('Meta Data')) return;
//        console.log(jsonObj);
        var meta = jsonObj['Meta Data'];
//        console.log(meta);
        var symbol = meta['1: Symbol'];
        var fullname = meta['2: Indicator']; //full name
        var data_values = jsonObj['Technical Analysis: ' + indicator]; //full size data
        var meta_date = meta['3: Last Refreshed'];

        var date = new Array();
        var key_array = new Array();
        var data_array = new Array();
        var count = 0;
        for(var i=0;i<parseInt(number);i++){
            data_array[i] = new Array();

        }
        for(var key in data_values[meta_date]){
            console.log(key);
            key_array.push(key);
        }

        for(var key in data_values) {
                   var temp_date = key.substring(5).replace(/-/g, "\/");
                    if(temp_date.length>=6) temp_date = temp_date.substr(0,5);
                    date.push(temp_date);
                    for(var i=0;i<key_array.length;i++){
                        data_array[i].push(parseFloat(data_values[key][key_array[i]]));
                    }
                    if(count == 126) break;
                    count = count + 1;
                }

      date.reverse();

        for(var i=0;i<parseInt(number);i++){
            data_array[i].reverse();
        }
      var myChart = Highcharts.chart('container', {
        chart: {
            zoomType: 'x'
        },

        title: {
            text: fullname
        },
        subtitle: {
        useHTML:true,
        text: "<a style=' text-decoration: none' href='https://www.alphavantage.co/'  target='_blank' >Source: Alpha Vantage</a>"
        },
        xAxis: {
            tickInterval:10,

        },
        yAxis: [{
            title: {
                text: indicator
            },
            "labels":{

         },

        }],

        series: [],
        legend: {

        },
    });
        for(var i=0;i<parseInt(number);i++){
             myChart.addSeries({
                threshold: null,
                lineWidth: 1.5,
                name: symbol + ''+ key_array[i],
                data: data_array[i],
                marker:{

            }
            });
        }
}