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

//function submitSymbol(symbol){
////    var symbol = "aapl";
//    if(symbol==""){
//        alert("Please enter a symbol");
//        return;
//    }
//    console.log(symbol);
//
////    document.getElementById('symbol').value=symbol;
//    var url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+symbol;
//    console.log(url);
//
//    var xmlhttp;
//    if(window.XMLHttpRequest) {
//                    xmlhttp = new XMLHttpRequest();
//                } else {
//                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
//                }
//      xmlhttp.onreadystatechange = function() {
//                    if (this.readyState == 4 && this.status == 200) {
//                        try{
//                            jsonObj = JSON.parse(xmlhttp.responseText);
//                             // alert(jsonObj);
//                            generateTable(jsonObj);
//                            console.log(jsonObj);
//                        }catch(e){
//                            alert("JSON File Syntax Error");
//                            console.log(jsonObj);
//                            console.log(e);
//                            return null;
//                        }
//                    }
//                };
//      xmlhttp.open("GET", url, true);
//      xmlhttp.send();
//}
function submitSymbol(symbol){
    if(symbol==""){
        alert("Please enter a symbol");
        return;
    }
    console.log(symbol);
//
////    document.getElementById('symbol').value=symbol;
    var url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+symbol;
    $.ajax({
        tryCount : 0,
        retryLimit : 3,
        url: url,
        success: function(response){
                                    jsonObj = JSON.parse(response);
                                    console.log("return "+jsonObj);
                                     // alert(jsonObj);
                                    generateTable(jsonObj);
                                    console.log(jsonObj);
//            jsonObjIndicator[index] = response;
            console.log("return "+jsonObj);
//            console.log(jsonObjIndicator[index]);
        },
          error: function(jqXHR, status, err){
          // 响应失败的回调函数
          console.log("error "+err);
           this.tryCount++;
           if (this.tryCount <= this.retryLimit) {
               //try again
               $.ajax(this);
               return;
           }
           return;
          },
        async: true
    });
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
/*
 *Generate Stock Information Table
 *@para jsonObj JSON Object 
 */ 
function generateTable(jsonObj){
    if(jsonObj.hasOwnProperty('Error Message')){

    }else{
        var meta = jsonObj['Meta Data'];
        var array_values = jsonObj['Time Series (Daily)'];
        symbol = meta['2. Symbol'];
        ReDate = meta['3. Last Refreshed'];
  
        var count =0;
        for(var key in array_values){
            if(count==0){
                open = array_values[key]['1. open'];
                close = array_values[key]['4. close'];
                day_range = array_values[key]['3. low']+"-"+array_values[key]['2. high'];
                volume = array_values[key]['5. volume'];
            }
            if(count == 1){
                pre_close = array_values[key]['4. close'];
            }
            var temp_date = key.substring(5).replace(/-/g, "\/");
            if(temp_date.length>=6) temp_date = temp_date.substr(0,5);
            date.push(temp_date);            
            data1.push(parseFloat(array_values[key]['4. close']));
            data2.push(parseFloat(array_values[key]['5. volume']));
            max = Math.max(parseFloat(array_values[key]['4. close']),max);
            min = Math.min(parseFloat(array_values[key]['4. close']),min);
            volume_max = Math.max(parseFloat(array_values[key]['5. volume']),max);
            if(count==126){
                break;
            }
            count++;
        }

        data1.reverse();
        data2.reverse();
        date.reverse();
        console.log(data1);
        
        var change = (close - pre_close).toFixed(2);
        var change_per = (change/pre_close*100).toFixed(2);
        drawAreaAndVolume();
    }
}

function drawPrice(){
    drawAreaAndVolume(ReDate, symbol, date,data1,data2,min,max,volume_max);
}
function fetchFB(indicator){
        var index = getIndicator(indicator);
        console.log("index: "+index);

                var number;
                if(index == 3) number = 2;
                else if(index == 7||index == 8) number = 3;
                else number = 1;

//                var dataStr = generateFBChart(index, indicator, number);

        var chart=$("#container").highcharts();
        var dataStr = chart.userOptions;

        optionsStr = JSON.stringify(dataStr);
        console.log(optionsStr);
//            var optionsStr = JSON.stringify(dataStr);
        var Url = 'http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/fb';
        var exportUrl = 'http://export.highcharts.com/';
        var requestrul = Url + "?options=" + encodeURIComponent(optionsStr);
    //    var requestrul = dataString;
        console.log(requestrul);
        var returnurl = '';
        $.ajax({
                tryCount : 0,
                retryLimit : 3,
                url: requestrul,
                async: false,
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                responseType: 'text',
                success: function(response){
                    console.log(response);
                    console.log(exportUrl + response);
                    returnurl = exportUrl + response;
//                    return returnurl;
                },
                  error: function(jqXHR, status, err){
                  // 响应失败的回调函数
                  console.log("error "+err+ " " +status+" "+jqXHR.readyState+" "+jqXHR.status);
                   this.tryCount++;
                   if (this.tryCount <= this.retryLimit) {
                       //try again
                       $.ajax(this);
                       return;
                   }
                   return;
                  },

            });
        return returnurl;
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
function fetchOneIndicator(symbol, indicator){
    var index = getIndicator(indicator);
    console.log("index: "+index);
    var number;
    if(index == 3) number = 2;
    else if(index == 7||index == 8) number = 3;
    else number = 1;
    $.ajax({
        tryCount : 0,
        retryLimit : 3,
        url: 'http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/indicator?indicator=' + indicator + '&symbol=' + symbol + '&number='+number,
        success: function(response){
            jsonObjIndicator[index] = response;
            console.log("return "+indicator);
//            console.log(jsonObjIndicator[index]);
        },
          error: function(jqXHR, status, err){
          // 响应失败的回调函数
          console.log("error "+err);
           this.tryCount++;
           if (this.tryCount <= this.retryLimit) {
               //try again
               $.ajax(this);
               return;
           }
           return;
          },
        async: true
    });
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
        generateFBChart(index,indicator,number);
    }

}
function drawAreaAndVolume(){
    document.getElementById('container').style.visibility='visible';
    console.log(date);
    console.log(data1);
    console.log(data2);
    var chartTitle = symbol+" Stock Price and Volume";
    var myChart = Highcharts.chart('container', {
        chart: {
            zoomType: 'x'
        },
        
        title: {
            text: chartTitle
        },
        subtitle: {
        useHTML:true,
        text: "<a style=' text-decoration: none' target='_blank' href='https://www.alphavantage.co/' >Source: Alpha Vantage</a>" 
        },
        xAxis: {
            tickInterval:10,
        },
        yAxis: [{
            title: {
                text: 'Stock Price'
            },
            
         "min":min*0.5,
        },{
          "title": {
                "text": 'Volume'
            },
          
          "opposite": true,
          "max": volume_max*8
        }],
        
        series: [{
            name: symbol,
            type: 'area',
            threshold: null,
            lineWidth: 1,
            lineColor: 'red',
            data: data1,
            color: '#ff0000',
            fillOpacity: 0.5,
            "marker":{
            "enabled":false
            },
            tooltip: {
            valueDecimals: 2
        }

        }, {
            name: symbol+' Volume',
            type: 'column',
            color: '#ffffff',
            yAxis: 1,
            data: data2
        }],
        legend: {
        },
    });
}

function fetchFBPrice(){
    var chartTitle = symbol+" Stock Price and Volume";
    var myChart = Highcharts.chart('container', {
        chart: {
            zoomType: 'x'
        },
        title: {
            text: chartTitle
        },
        subtitle: {
        useHTML:true,
        text: "<a style=' text-decoration: none' target='_blank' href='https://www.alphavantage.co/' >Source: Alpha Vantage</a>"
        },
        xAxis: {
            tickInterval:10,
        },
        yAxis: [{
            title: {
                text: 'Stock Price'
            },
         "min":min*0.5,
        },{
          "title": {
                "text": 'Volume'
            },

          "opposite": true,
          "max": volume_max*8
        }],
        series: [{
            name: symbol,
            type: 'area',
            threshold: null,
            lineWidth: 1,
            lineColor: 'red',
            data: data1,
            color: '#ff0000',
            fillOpacity: 0.5,
            "marker":{
            "enabled":false
            },
            tooltip: {
            valueDecimals: 2
        }
        }, {
            name: symbol+' Volume',
            type: 'column',
            color: '#ffffff',
            yAxis: 1,
            data: data2
        }],
    });
    console.log(myChart);
    return myChart.userOptions;
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
                name: symbol + ' '+ key_array[i],
                data: data_array[i],
                marker:{

            }
            });
        }
}

function generateFBChart(index, indicator, number){
        if(index == 0) return fetchFBPrice();
        if(number == 2) return generateFBTwoChart(index, indicator, number);
        if(number == 3) return generateFBThreeChart(index, indicator, number);
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
        series: [{
            threshold: null,
            lineWidth: 1.5,
            name: symbol + ' ' + key_array[0],
            data: data_array[0],
            marker: {
              enabled: false,
            }
          }],
        legend: {

        },
    });
        console.log(myChart);
        return myChart.userOptions;
}
function generateFBThreeChart(index, indicator, number){
        if(index == 0) return fetchFBPrice();
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
          series: [{
            threshold: null,
            lineWidth: 1.5,
            name: symbol + ' ' + key_array[0],
            data: data_array[0],
            marker: {
              enabled: false,
            },
          }, {
            threshold: null,
            lineWidth: 1.5,
            name: symbol + ' ' + key_array[1],
            data: data_array[1],
            marker: {
              enabled: false,
            },
          }, {
            threshold: null,
            lineWidth: 1.5,
            name: symbol + ' ' + key_array[2],
            data: data_array[2],
            marker: {
              enabled: false,
            },
          }],
        legend: {

        },
    });

        console.log(myChart);
        return myChart.userOptions;
}
function generateFBTwoChart(index, indicator, number){
        if(index == 0) return fetchFBPrice();
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

        series: [{
                    threshold: null,
                    lineWidth: 1.5,
                    name: symbol + ' ' + key_array[0],
                    data: data_array[0],
                    marker: {
                      enabled: false,
                    },
                  }, {
                    threshold: null,
                    lineWidth: 1.5,
                    name: symbol + ' ' + key_array[1],
                    data: data_array[1],
                    marker: {
                      enabled: false,
                    },
                  }],
        legend: {

        },
    });

        console.log(myChart);
        return myChart.userOptions;
}