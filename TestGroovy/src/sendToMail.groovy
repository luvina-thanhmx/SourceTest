import groovy.json.JsonSlurper;

/*def treeMap = [
	"MySQL":["Database_Area":[], "Database_Statistic":[], "Database_Structure":[], "FaultManagement":[], "Performance":[], "Proactive_Check":[], "Others":[]],
	"Postgres":["Database_Area":[], "Database_Statistic":[], "Database_Structure":[], "FaultManagement":[], "Performance":[], "Proactive_Check":[], "Others":[]],
	"SQL_Server":["Database_Area":[], "Database_Statistic":[], "Database_Structure":[], "FaultManagement":[], "Performance":[], "Proactive_Check":[], "Others":[]],
	"OS":[]
]

def list_coll = [
	["MySQL","Database_Area","InnoDBTablespace_Free","localhost-@MYSQL-information_schema","MySQL.Database_Area.InnoDBTablespace_Free"],
	["MySQL","Database_Area","luvina","insight","thanhmx","localhost-@MYSQL","MySQL.Database.luvina.insight.thanhmx"],
	["MySQL","Database_Statistic","TestJobPieSubtypeD","null","MySQL.Database_Statistic.TestJobPieSubtypeD"],
	["OS","CPU","CPU_Windows","null","OS.CPU.CPU_Windows"],
	["OS","CPU","CPU_Windows","null-@SQLS","OS.CPU.CPU_Windows"],
	["OS","Browser","Chrome","null-@SQLS","OS.Browser.Chrome"],
	["SQL_Server","Database_Area","Database_free","localhost-@MSSQL-master","SQL_Server.Database_Area.Database_free"],
	["SQL_Server","Database_Statistic","StoreTestUnit","luvina018","SQL_Server.Database_Statistic.StoreTestUnit"]
]
// Database_Area:["A":["A1", "A2"], "B":["B1", "B2"], "C":["C1", "C2"], "D":[]]

def check_coll = { coll->
	if( coll!="system.indexes" && (coll.startsWith("MySQL") || coll.startsWith("SQL_Server") || coll.startsWith("Postgres") || coll.startsWith("OS"))){
		return true
	} else {
		return false
	}
}
def tmp_c = [:]
treeMap.each{key,value->
	value.each{
		tmp_c[it]=[]
	}
}
treeMap.each{key,value->
	list_coll.each{col->
		def size = col.size() - 1
		def flag = true
		if(key == col[0]){
			if(value instanceof Map){	
				value.each{items,val->
					println items
					if(items == col[1]){
						if(!treeMap[key][items].contains(col[size])){
							treeMap[key][items].add(col[size])
						}
						flag = false
					} 
				}
				if(flag){
					if(!treeMap[key]["Others"].contains(col[size])){
						treeMap[key]["Others"].add(col[size])
					}
				}
			}
			if(value instanceof List){
				treeMap[key].add(col[size])
			}
		}
	}
}
treeMap.each{ println it }

def listOperators(device) {
	def results = []
	if (device) {
		results += device.operators*.id
		device.children?.each { child ->
			results += listOperators(child)
		}
	}
	results.unique()
}
*/
/*
def treeMap = [
	"MySQL":["Database_Area":[], "Database_Statistic":[], "Database_Structure":[], "FaultManagement":[], "Performance":[], "Proactive_Check":[], "Others":[]],
	"OS":[]
]

def list_coll = [
	["MySQL","Database_Area","InnoDBTablespace_Free","localhost-@MYSQL-information_schema","MySQL.Database_Area.InnoDBTablespace_Free"],
	["MySQL","Database_Area","luvina","insight","Job1","localhost-@MYSQL","MySQL.Database.luvina.insight.Job1"]
]

list_coll.each{col1->
	def result = putData(col1, [:])
	println result;
}
def putData(listData, mapResult){
	if(mapResult == null){
		mapResult = [:]
	}
	def size = listData.size() - 1 
	for(int i = 0; i < size-3; i++){
		def tmptmpMap = [:]
		if(i == (size-4)){
			tmptmpMap[listData[i+1]] = []
			tmptmpMap[listData[i+1]].add(listData[size])
		} else{
			tmptmpMap[listData[i+1]] = [:]
		}
		mapResult[listData[i]] = tmptmpMap
		List cloneList = listData.clone()
		cloneList.remove(0)
		putData(cloneList, mapResult[listData[i]])
		break;
	}
	return mapResult
}

def formatData = [
	"MySQL":[
			"Database_Area":[
					"InnoDBTablespace_Free":[], 
					"luvina":[
							"insight":["thanhmx"],
							
					]
			], 
			"Database_Statistic":[], 
			"Database_Structure":[]
		],
	"OS":[]	
]
/*treeMap.each{db,val->
	def dataMap = val
	list_coll.each{data->
		def size = data.size() - 1
		if(db == data[0]){
			while(dataMap instanceof Map){
				dataMap.each{it->
					if(it instanceof Map){
						
						if(it.key == data[1] ){
									
						}
					} else {

					}
				}
			}
		}
	}
}*/

def test = "2013/09/13 19:02:25 JST"
def test1 = "2013/10/16 10:32:12 GMT+07:00"
//def date = (new Date(test1)).toString("yyyy-MM-dd HH:mm:ss") 
//def fromDate2Send = (new Date(test1)).toString('yyyy-MM-dd HH:mm:ss')
//println fromDate2Send

def dataTest = '''
[ [ { "xAxis" : { "categories" : [ "20131211142047GMT+07:00" , "20131211154008GMT+07:00"]} , "chart_name" : "Draw Area Size D (test)" , "series" : [ { "name" : "UsedIndexCumSize" , "data" : [ 1024 , 1024]}] , "unit" : "byte" , "chartItemName" : [ "test" , "wikisharespacechild"]} , { "xAxis" : { "categories" : [ "20131211142047GMT+07:00" , "20131211154008GMT+07:00"]} , "chart_name" : "Draw Area Size D (wikisharespacechild)" , "series" : [ { "name" : "UsedIndexCumSize" , "data" : [ 2097152 , 2097152]}] , "unit" : "byte" , "chartItemName" : [ "test" , "wikisharespacechild"]}]]
'''

JsonSlurper js = new JsonSlurper()
println js.parseText(dataTest)
