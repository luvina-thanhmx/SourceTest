//$(function () {	
	var ranges = [
            [1246406400000, 14.3, 27.7],
            [1246492800000, 14.5, 27.8],
            [1246579200000, 15.5, 29.6],
            [1246665600000, 16.7, 30.7],
            [1246752000000, 16.5, 25.0],
            [1246838400000, 17.8, 25.7],
            [1246924800000, 13.5, 24.8],
            [1247011200000, 10.5, 21.4],
            [1247097600000, 9.2, 23.8],
            [1247184000000, 11.6, 21.8],
            [1247270400000, 10.7, 23.7],
            [1247356800000, 11.0, 23.3],
            [1247443200000, 11.6, 23.7],
            [1247529600000, 11.8, 20.7],
            [1247616000000, 12.6, 22.4],
            [1247702400000, 13.6, 19.6],
            [1247788800000, 11.4, 22.6],
            [1247875200000, 13.2, 25.0],
            [1247961600000, 14.2, 21.6],
            [1248048000000, 13.1, 17.1],
            [1248134400000, 12.2, 15.5],
            [1248220800000, 12.0, 20.8],
            [1248307200000, 12.0, 17.1],
            [1248393600000, 12.7, 18.3],
            [1248480000000, 12.4, 19.4],
            [1248566400000, 12.6, 19.9],
            [1248652800000, 11.9, 20.2],
            [1248739200000, 11.0, 19.3],
            [1248825600000, 10.8, 17.8],
            [1248912000000, 11.8, 18.5],
            [1248998400000, 10.8, 16.1]
        ],
        averages = [
            [1246406400000, 21.5],
            [1246492800000, 22.1],
            [1246579200000, 23],
            [1246665600000, 23.8],
            [1246752000000, 21.4],
            [1246838400000, 21.3],
            [1246924800000, 18.3],
            [1247011200000, 15.4],
            [1247097600000, 16.4],
            [1247184000000, 17.7],
            [1247270400000, 17.5],
            [1247356800000, 17.6],
            [1247443200000, 17.7],
            [1247529600000, 16.8],
            [1247616000000, 17.7],
            [1247702400000, 16.3],
            [1247788800000, 17.8],
            [1247875200000, 18.1],
            [1247961600000, 17.2],
            [1248048000000, 14.4],
            [1248134400000, 13.7],
            [1248220800000, 15.7],
            [1248307200000, 14.6],
            [1248393600000, 15.3],
            [1248480000000, 15.3],
            [1248566400000, 15.8],
            [1248652800000, 15.2],
            [1248739200000, 14.8],
            [1248825600000, 14.4],
            [1248912000000, 15],
            [1248998400000, 13.6]
        ];
	
    //$('#containerByDay').highcharts(new highchartsBuilder(mySeries1));
	// Retrieve

	var major = '@MYSQL'
	var minor = 'MySQL.Performance.QueryCache'
	$.ajax({
		type: "GET",
		url: "http://192.168.84.102:8089/monitor_data_new/" + major + "/" + minor,
		async: false,
		success: function(result) {
			if (result != null && result != []) {
				for(var i=0; i<result.length; i++) {
					if (result[i]["category"] != null && result[i]["series"] != null && result[i]["type"] != null && result[i]["type"] == "line") {
						//draw line chart
						prepareContainerLine(result[i]["param"])
						drawLineChart(minor, result[i]["type"], result[i]["category"], result[i]["series"], result[i]["tooltip"], result[i]["param"], result[i]["mapSeverityValue"])
						getListNode(result[i]["series"])
					} else if (result[i]["type"] = "pie") {
						// draw pie chart
						//drawPieChart(minor, result[i])
					}
				}
			}
		},
		error: function(xhr, status, error) {
			console.log(xhr, status, error)
			alert("Failed to get data from Wiperdog !")
		}
	});

	//prepare container to draw chart
    function prepareContainerLine(param){
       $("#containerByDay").append("<div id = 'lineChart' style = 'clear:both' ></div>")
       $("#lineChart").append("<div id='wrapperLineChart' style='clear:both'></div>")
       $("#wrapperLineChart").append("<div class='displayBlockLine' id='containerline" + param + "'></div>")
    }

	var lineChart
	function drawLineChart(minor, type, category, series, tooltip, param, mapSeverityValue) {
        Highcharts.setOptions({
            // This is for all plots, change Date axis to local timezone
            global: {
                useUTC: false
            }
        });

        lineChart = new Highcharts.Chart({
            chart: {
				type:'area',
				renderTo:'containerline' + param,
				style: {
					fontFamily: 'Arial', // default font
					fontSize: '5px'
				},
				"height": 150,
				"animation": false,
				"marginRight": 0,
				"marginLeft": 0,
				"marginTop": 10
            },
            tooltip: {
                formatter: function() {
                    var dateAtPoint = this.x
                    var tooltipForSeries = tooltip[this.series.name]
                    var dateStr = new Date(dateAtPoint).toString()
                    var tooltipStr = "<b>" + dateStr.substr(0, dateStr.indexOf("GMT")) + "</b><br/>"
                    $.each(category, function(index, value) {

                        if (dateAtPoint == value) {
                            var dataTooltip = tooltipForSeries[index]
                            $.each(dataTooltip, function(index, value) {
                                if (value["p"] == param) {
                                    tooltipStr += "<i>" + value["p"] + ":" + value["v"] + "</i><br/>"
                                } else {
                                    tooltipStr += value["p"] + ":" + value["v"] + "<br/>"
                                }
                            })

                        }
                    })
                    return tooltipStr
                }
            },
            title: {
				text: 'Daily Chart'
			},
			credits: {
				enabled: false
			},
			exporting: {
				enabled: false
			},
            legend: {
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 50,
                layout: "vertical",
                floating: true,
				enabled: true
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: { // don't display the dummy year
                    // second: '%Y-%m-%d<br/>%H:%M:%S',
                    // minute: '%Y-%m-%d<br/>%H:%M',
                    // hour: '%Y-%m-%d<br/>%H:%M',
                    day: '%Y<br/>%m-%d',
                    week: '%Y<br/>%m-%d',
                    month: '%Y-%m',
                    year: '%Y'
                },
				labels: {
					style: {
						fontSize:'3px'
					}
				}
            },
            yAxis: {
                title: {
                    text: null
                },
				min: 0,
				max: 35,
				labels: {
					"align": "left",
					"x": 0,
					"y": -2,
					"style": {
						"textShadow": "0 0 1px #fff"
					}
				},
                plotLines: [{
                    id: 'normal',
                    color: 'green',
                    dashStyle: 'ShortDash',
                    width: 2,
                    value: 10,//mapSeverityValue["avg"] + 2*mapSeverityValue["alpha"],
                    zIndex: 0,
                    label: {
                        //text: 'Normal value'
                    }
                }, {
                    id: 'warning',
                    color: 'orange',
                    dashStyle: 'ShortDash',
                    width: 2,
                    value: 20,//mapSeverityValue["avg"] + 3*mapSeverityValue["alpha"],
                    zIndex: 0,
                    label: {
                        //text: 'Warning value'
                    }
                }, {
                    id: 'dangerous',
                    color: 'red',
                    dashStyle: 'ShortDash',
                    width: 2,
                    value: 30,//mapSeverityValue["avg"] + 4*mapSeverityValue["alpha"],
                    zIndex: 0,
                    label: {
                        //text: 'Dangerous value'
                    }
                }]
            },
            series: series
        });
    }
	
	var listNode
	function getListNode(data) {
		listNode = []
		for(var i=0; i<data.length; i++) {
			listNode.push(data[i].name)
		}
	}
	
	function getChartData() {
		var chartData = {}
		chartData["chart"] = lineChart
		chartData["listNode"] = listNode		
		return chartData;
	}

	$('#weekly-check').change(function() {
		if ($('#weekly-check').is(':checked')) {
			$('#containerWeekly').show();
			var weeklyChart = new Highcharts.Chart({
				chart: {
					renderTo:'containerWeekly',
					zoomType:'x',
					style: {
						fontFamily: 'Arial', // default font
						fontSize: '5px'
					},
					"height": 150
				},
				title: {
					text: 'Weekly Chart'
				},
				xAxis: {
					type: 'datetime'
				},
				yAxis: {
					title: {
						text: null
					},
					min: 0,
					max: 35,
					labels: {
						"align": "left",
						"x": 0,
						"y": -2,
						"style": {
							"textShadow": "0 0 1px #fff"
						}
					}
				},
				credits: {
					enabled: false
				},
				exporting: {
					enabled: false
				},
				tooltip: {
					crosshairs: true,
					shared: true,
					valueSuffix: '°C'
				},
				legend: {
				},
				series: [{
					name: 'Temperature',
					cursor:'pointer',
					data: averages,
					zIndex: 1,
					marker: {
						fillColor: 'white',
						lineWidth: 2,
						lineColor: Highcharts.getOptions().colors[0]
					}
				}, {
					name: 'Range',
					data: ranges,
					type: 'arearange',
					lineWidth: 0,
					linkedTo: ':previous',
					color: Highcharts.getOptions().colors[0],
					fillOpacity: 0.3,
					zIndex: 0
				}]
			});
		} else { 
			$('#containerWeekly').hide(); 
		}
	});
	
	$.ajax({type: "GET",
		async: false,
		url: "json/joblist.json",
		dataType: "json",
		success: function(jsonData){
			var jobDropList = jsonData.jobDropList;
			var msg = "";
			for (i=0; i < jobDropList.length; i++){
				// job group name or job name?
				if (jobDropList[i].header) {
					// The first is a header
					if (i==0) {
						$("#dropdown-joblist").append("<li class='dropdown-header' role='presentation'>" + jobDropList[i].itemName + "</li>");
					} else {
						$("#dropdown-joblist").append("<li class='divider' role='presentation'></li>");
						$("#dropdown-joblist").append("<li class='dropdown-header' role='presentation'>" + jobDropList[i].itemName + "</li>");
					}
				} else {
					$("#dropdown-joblist").append("<li><a href='#'>" + jobDropList[i].itemName + "</a></li>");
				}
			}
		}
	})
	
	$.ajax({
		type: "GET",
		url: "http://localhost:8888/getDataMongo",
		async: false,
		success: function(result) {
			alert("aaaaaaaaaaaa")
			var obj = jQuery.parseJSON(result);
			alert(obj)
		},
		error: function(xhr, status, error) {
			console.log(xhr, status, error)
			alert("Failed to get data from Wiperdog !")
		}
	});

//});