/*def finalData = [
		[
			Instance:"instance1", schedule:"5i", param1:"aaa", param2:"bbb", param3:"", param4:""
		],
		[
			Instance:"instance2", schedule:"5", param1:"", param2:"", param3:"ccc", param4:"ddd"
		],
		[
			Instance:"instance3", schedule:"10", param1:"eee", param2:"", param3:"", param4:"fff"
		] 
	]
*/
def firstData = [
	[
		Instance:"instance1", schedule:"5i", params:["param1":"aaa"]
	],
	[
		Instance:"instance2", schedule:"5", params:["param3":"ccc", "param4":"ddd"]
	],
	[
		Instance:"instance3", schedule:"10", params:["param1":"eee", "param4":"fff"]
	],
	[
		Instance:"instance4", schedule:"1", params:["param5":"ggg", "param6":"hhh", "param1":"iii"]
	]
]
def listKeyParams = []
firstData.each{fd->
	fd.params.each{
		if(!listKeyParams.contains(it.key)){
			listKeyParams.add(it.key)
		}
	}
}
/*def tempParams
def mapKeyParams = [:]
listKey.each{
	tempParams = [:]
	tempParams[it] = ""
	mapKeyParams += tempParams
}
println "mapKeyParams: " + mapKeyParams*/
def mapData
def mapParam
def listData = []
firstData.each{dat->
	mapData = [:]
	mapParam = [:]
	mapData['Instance'] = dat.Instance
	mapData['schedule'] = dat.schedule
	mapParam = dat.params
	listKeyParams.each{lk->
		println "mapParam: "+ mapParam.size()
		if(mapParam.size() != 0){
			mapParam.each{mp->
				if(lk != mp.key){
					mapData[lk] = ""
				}			
			}
		} else {
			mapData[lk] = ""
		}
	}
	mapData = mapData + mapParam
	listData.add(mapData)
}
def listKeyCSV = ["Instance", "schedule"]
listKeyCSV += listKeyParams
def listValueCSV
def listDataCSV = []
listDataCSV.add(listKeyCSV)
listData.each{ld->
	listValueCSV = []
	ld.each{
		listValueCSV.add(it.value)
	}
	listDataCSV.add(listValueCSV)
}
def tmpStr = ""
listDataCSV.each{csv->
	csv.each{
		tmpStr += it + ","
	}
	tmpStr = tmpStr.substring(0, tmpStr.lastIndexOf(","))
	tmpStr += "\n"
}
println "tmpStr: \n" + tmpStr
/*
def data = [instance1:[schedule:1, params:["param1":"bbb", "param2":"aaaa"]]]
def tmpMap
def tmpListData = []
data.each{
	tmpMap = [:]
	tmpMap['Instance'] = it.key
	tmpMap += it.value
	tmpListData.add(tmpMap)
}
println tmpListData
*/