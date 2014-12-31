function output(type) 
{
	prepareOutpuData();
	var theForm=document.getElementById("formOutput");
	if (type == "print")
	{
		features ='width=800,height=600,toolbar=no,location=no,directories=no,menubar=no,scrollbars=no,copyhistory=no,resizable=yes';
		pop = window.open('about:blank',"wnd",features);
		theForm.target="wnd";
	} else {
		theForm.target="_self";
	}
	theForm.action="output.php?Type="+type;
	theForm.submit();
}

function prepareOutpuData()
{
	var headerRows = "";
	for (var i = 0; i < columns.length; i++) {
		headerRows += columns[i].name + "|"; 			
	}
	document.getElementById("outputHeaders").value = headerRows;
	
	selectedRows = [];
	for (var i = 0; i < dataView.rows.length; i++) {
		var row = "";
		row += dataView.rows[i].num + "|"; 
		row += dataView.rows[i].title + "|"; 
		row += dataView.rows[i].duration + "|"; 
		row += dataView.rows[i].percentComplete + "|"; 
		row += dataView.rows[i].start + "|"; 
		row += dataView.rows[i].finish + "|"; 
		row += dataView.rows[i].effortDriven + "|"; 
		selectedRows[i] = row;
	}		
	document.getElementById("outputItems").value = selectedRows;
}

