/*
 @author: Maolin Tu
 @Blog: http://tumaolin.com 
*/


function formReset(){
    document.getElementById("form1").reset();
    document.getElementById('whole_container').innerHTML = "";
    document.getElementById('container').innerHTML = "";
    document.getElementById('container').style.visibility='hidden';
}

function submitSymbol(){
    var symbol = "aapl";
    if(symbol==""){
        alert("Please enter a symbol");
        return;
    }
    console.log(symbol);

//    document.getElementById('symbol').value=symbol;
    var url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" +symbol+"&outputsize=full&apikey=FEY146OML7L2A34X";
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
                            generateTable(jsonObj);
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

function drawAreaAndVolume(){
    document.getElementById('container').style.visibility='visible';
    console.log(date);
    console.log(data1);
    console.log(data2);
    var chartTitle = "Stock Price (" +ReDate+ ")";
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
function generateChart(jsonObj, indicator, number){
        var meta = jsonObj['Meta Data'];
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
            categories:date,
            tickInterval:5,
            tickPositioner: function() {
            let res = [];
            for(let i = 0; i < this.categories.length; i++) {
                if(i % 5 == 0) res.push(this.categories.length-i-1);
            }
            return res;
        }
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
            layout: 'vertical',
            verticalAlign: 'middle',
            align: 'right'
            
        },
    });
        for(var i=0;i<parseInt(number);i++){
             myChart.addSeries({
                threshold: null,
                lineWidth: 1.5,
                name: symbol + ''+ key_array[i],
                data: data_array[i],
                marker:{
                enabled:true,
                radius: 2.5,
                symbol: 'square'
            }
            });
        }
}