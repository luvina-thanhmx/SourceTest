var dataView;
var grid;
var data = [];
var selectedRowIds = [];

var columns = [
	{id:"sel", name:"#", field:"num", behavior:"select", cssClass:"cell-selection", width:40, cannotTriggerInsert:true, resizable:false, unselectable:true },
	{id:"istIid", name:"IST_IID", field:"istIid", width:100, minWidth:100, cssClass:"cell-title", editor:TextCellEditor, validator:requiredFieldValidator, setValueHandler:updateItem, sortable:true},
	{id:"lastest", name:"Lastest", field:"lastest", editor:TextCellEditor, setValueHandler:updateItem, sortable:true},
	{id:"min", name:"MIN", field:"min", editor:TextCellEditor, setValueHandler:updateItem, sortable:true},
	{id:"max", name:"MAX", field:"max", editor:TextCellEditor, setValueHandler:updateItem, sortable:true},
	/*{id:"max", name:"MAX", field:"max", width:80, resizable:false, formatter:GraphicalPercentCompleteCellFormatter, editor:PercentCompleteCellEditor, setValueHandler:updateItem, sortable:true},*/
	{id:"sv1", name:"SV1", field:"sv1", minWidth:60, editor:DateCellEditor, setValueHandler:updateItem, sortable:true},
	{id:"sv2", name:"SV2", field:"sv2", minWidth:60, editor:DateCellEditor, setValueHandler:updateItem, sortable:true},
	{id:"sv3", name:"SV3", field:"sv3", minWidth:60, editor:DateCellEditor, setValueHandler:updateItem, sortable:true},
	{id:"udp", name:"UDP", field:"udp", width:80, minWidth:20, maxWidth:80, editor:DateCellEditor, setValueHandler:updateItem, sortable:true}
	/*{id:"udp", name:"UDP", field:"udp", width:80, minWidth:20, maxWidth:80, cssClass:"cell-effort-driven", formatter:BoolCellFormatter, editor:YesNoCheckboxCellEditor, setValueHandler:updateItem, cannotTriggerInsert:true, sortable:true}*/
];
		
var options = {
	editable: false,
	enableAddRow: true,
	enableCellNavigation: true,
	asyncEditorLoading: true,
	forceFitColumns: false,
	secondaryHeaderRowHeight: 25
};

var sortcol = "istIid";
var sortdir = 1;
//var percentCompleteThreshold = 0;
var searchString = "";

function requiredFieldValidator(value) {
	if (value == null || value == undefined || !value.length)
		return {valid:false, msg:"This is a required field"};
	else
		return {valid:true, msg:null};
}

function myFilter(item) {
	//if (item["percentComplete"] < percentCompleteThreshold)
	//	return false;

	if (searchString != "" && item["istIid"].indexOf(searchString) == -1)
		return false;

	return true;
}

function percentCompleteSort(a,b) {
	return a["sv3"] - b["sv3"];
}

function comparer(a,b) {
	var x = a[sortcol], y = b[sortcol];
	return (x == y ? 0 : (x > y ? 1 : -1));
}

function updateItem(value,columnDef,item) {
	item[columnDef.field] = value;
	dataView.updateItem(item.id,item);
}

function addItem(columnDef,value) {
	var item = {"id": "new_" + (Math.round(Math.random()*10000)), "istIid":"New Instance", "lastest":20, "min":10, "max":50, "sv1":55, "sv2":66, "sv3":77, "udp":"11:08AM"};
	item[columnDef.field] = value;
	dataView.addItem(item);
}


function toggleFilterRow() {
	if ($(grid.getSecondaryHeaderRow()).is(":visible"))
		grid.hideSecondaryHeaderRow();
	else
		grid.showSecondaryHeaderRow();
}


$(".grid-header .ui-icon")
	.addClass("ui-state-default ui-corner-all")
	.mouseover(function(e) {
		$(e.target).addClass("ui-state-hover")
	})
	.mouseout(function(e) {
		$(e.target).removeClass("ui-state-hover")
	});

var instancesDetailData
$.ajax({type: "GET",
	async: false,
	url: "json/instances_detail.json",
	dataType: "json",
	success: function(result){
		instancesDetailData = result
	}
})
	
$(function()
{
	// prepare the data
	var listNodeName = getChartData()["listNode"]
	var d
	for (var i=0; i<instancesDetailData.length; i++) {
		d = (data[i] = {});
		
		d["id"] = "id_" + i;
		d["num"] = i;
		//d["istIid"] = instancesDetailData[i]["IstIid"];
		d["istIid"] = listNodeName[i];
		d["lastest"] = instancesDetailData[i]["Lastest"];
		d["min"] = instancesDetailData[i]["MIN"];
		d["max"] = instancesDetailData[i]["MAX"];
		d["sv1"] = instancesDetailData[i]["SV1"];
		d["sv2"] = instancesDetailData[i]["SV2"];
		d["sv3"] = instancesDetailData[i]["SV3"];
		d["udp"] = instancesDetailData[i]["UDP"];
	}

	dataView = new GridNic.Data.DataView();
	grid = new GridNic.Grid($("#myGrid"), dataView.rows, columns, options);
	var pager = new GridNic.Controls.Pager(dataView, grid, $("#pager"), columns);
	var columnpicker = new GridNic.Controls.ColumnPicker(columns, grid, options);


	// move the filter panel defined in a hidden div into an inline secondary grid header row
	$("#inlineFilterPanel")
		.appendTo(grid.getSecondaryHeaderRow())
		.show();


	grid.onAddNewRow = addItem;

	grid.onKeyDown = function(e) {
		// select all rows on ctrl-a
		if (e.which != 65 || !e.ctrlKey)
			return false;

		var rows = [];
		selectedRowIds = [];

		for (var i = 0; i < dataView.rows.length; i++) {
			rows.push(i);
			selectedRowIds.push(dataView.rows[i].id);
		}

		grid.setSelectedRows(rows);

		return true;
	};

	grid.onSelectedRowsChanged = function() {
		selectedRowIds = [];
		var rows = grid.getSelectedRows();
		for (var i = 0, l = rows.length; i < l; i++) {
			selectedRowIds.push(rows[i].id);
		}
	};

	grid.onSort = function(sortCol, sortAsc) {
		sortdir = sortAsc ? 1 : -1;
		sortcol = sortCol.field;
		dataView.fastSort(sortcol,sortAsc);
	};

	dataView.onRowCountChanged.subscribe(function(args) {
		grid.updateRowCount();
	});

	dataView.onRowsChanged.subscribe(function(rows) {
		grid.removeRows(rows);
		grid.render();

		if (selectedRowIds.length > 0)
		{
			// since how the original data maps onto rows has changed,
			// the selected rows in the grid need to be updated
			var rows = [];
			for (var i = 0; i < selectedRowIds.length; i++)
			{
				var idx = dataView.getRowById(selectedRowIds[i]);
				if (idx != undefined)
					rows.push(idx);
			}

			grid.setSelectedRows(rows);
		}
	});

	dataView.onPagingInfoChanged.subscribe(function(pagingInfo) {
		var isLastPage = pagingInfo.pageSize*(pagingInfo.pageNum+1)-1 >= pagingInfo.totalRows;
		grid.setOptions({enableAddRow:isLastPage||pagingInfo.pageSize==0});
	});

	var h_runfilters = null;
	/*
	$("#pcSlider").slider({
		"range":	"min",
		"slide":	function(event,ui) {
			GridNic.GlobalEditorLock.cancelCurrentEdit();

			if (percentCompleteThreshold != ui.value)
			{
				window.clearTimeout(h_runfilters);
				h_runfilters = window.setTimeout(dataView.refresh, 10);
				percentCompleteThreshold = ui.value;
			}
		}
	});*/

	$("#txtSearch").keyup(function(e) {
		GridNic.GlobalEditorLock.cancelCurrentEdit();

		// clear on Esc
		if (e.which == 27)
			this.value = "";

		searchString = this.value;
		dataView.refresh();
	});

	$("#btnSelectRows").click(function() {
		if (!GridNic.GlobalEditorLock.commitCurrentEdit()) { return; }

		var rows = [];
		selectedRowIds = [];

		for (var i=0; i<10 && i<dataView.rows.length; i++) {
			rows.push(i);
			selectedRowIds.push(dataView.rows[i].id);
		}

		grid.setSelectedRows(rows);
	});

	dataView.beginUpdate();
	dataView.setItems(data);
	dataView.setFilter(myFilter);
	//dataView.setPagingOptions({pageSize:25});
	dataView.endUpdate();

	$("#gridContainer").resizable();
})