$(function () {

	var highchartsBuilder = function(mySeries, myLineColor){
		return {
			
			chart: {
            polar: true,
            type: 'area',
            height: 100,
            width: 120,
			spacingTop:0,
			spacingRight:0,
			spacingBottom:0,
			spacingLeft:0
        },

        title: {
            text: null
        },
		
		credits: {
            enabled: false
        },
        exporting: {
            enabled: false
        },

        pane: {
            center: ['50%', '50%'],
            size: 80
        },

        xAxis: {
            categories: ['', '', '', '',
                    '', ''],
            tickmarkPlacement: 'on',
            lineWidth: 0
        },

        yAxis: {
            max:15,
            gridLineInterpolation: 'polygon',
            tickInterval: 15/5,
			labels:{
				enabled: false
			}
        },

        tooltip: {
            shared: true,
            pointFormat: '<span style="color:{series.color}">{series.name}: <b>${point.y:,.0f}</b><br/>'
        },

        legend: {
            align: 'right',
            verticalAlign: 'top',
            y: 70,
            layout: 'vertical'
        },
		 plotOptions: {
            series: {
                lineColor: myLineColor,
                marker: {
                    radius: 3
                }
            }
        },
         series: mySeries
		
		}
	}
	
	mySeriesForDay1 = [{
            showInLegend: false,
            data: [ {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"}, {y:15,color:"green"}],
            pointPlacement: 'on',
            color:"#00CC66"
        }]	
	myLineColorForDay1 = 'green'
	
	mySeriesForDay5 = [{
            showInLegend: false,
            data: [ {y:9,color:"yellow"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"}, {y:15,color:"green"}],
            pointPlacement: 'on',
            color:"#ffff7a"
        }]	
	myLineColorForDay5 = 'yellow'
	
	mySeriesForDay18 = [{
            showInLegend: false,
            data: [ {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:6,color:"orange"}, {y:15,color:"green"}],
            pointPlacement: 'on',
            color:"#ff9e3d"
        }]		
	myLineColorForDay18 = 'orange'
	
	mySeriesForDay25 = [{
            showInLegend: false,
            data: [ {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"},  {y:15,color:"green"}, {y:3,color:"red"}],
            pointPlacement: 'on',
            color:"#ff6666"
        }]		
	myLineColorForDay25 = 'red'

    $('#container1').highcharts(new highchartsBuilder(mySeriesForDay1, myLineColorForDay1));
	$('#container5').highcharts(new highchartsBuilder(mySeriesForDay5, myLineColorForDay5));
	$('#container18').highcharts(new highchartsBuilder(mySeriesForDay18, myLineColorForDay18));
	$('#container25').highcharts(new highchartsBuilder(mySeriesForDay25, myLineColorForDay25));
	
});