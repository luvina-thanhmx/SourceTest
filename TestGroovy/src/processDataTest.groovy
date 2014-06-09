def result =	[
		[fetchAt:1,
			data:[
				[DBName:'A', FileName:'file1', FileSizePageInt:14],
				[DBName:'A', FileName:'file2', FileSizePageInt:44],
				[DBName:'B', FileName:'file1', FileSizePageInt:1],
				[DBName:'B', FileName:'file2', FileSizePageInt:22],
				[DBName:'C', FileName:'file1', FileSizePageInt:3],
				[DBName:'C', FileName:'file2', FileSizePageInt:35]
					]
				],
		[fetchAt:2,
			data:[
				[DBName:'A', FileName:'file1', FileSizePageInt:15],
				[DBName:'A', FileName:'file2', FileSizePageInt:45],
				[DBName:'B', FileName:'file1', FileSizePageInt:2],
				[DBName:'B', FileName:'file2', FileSizePageInt:23],
				[DBName:'C', FileName:'file1', FileSizePageInt:4],
				[DBName:'C', FileName:'file2', FileSizePageInt:36]
					]
				],
		[fetchAt:3,
			data:[
				[DBName:'A', FileName:'file1', FileSizePageInt:16],
				[DBName:'A', FileName:'file2', FileSizePageInt:46],
				[DBName:'B', FileName:'file1', FileSizePageInt:3],
				[DBName:'B', FileName:'file2', FileSizePageInt:24],
				[DBName:'C', FileName:'file1', FileSizePageInt:5],
				[DBName:'C', FileName:'file2', FileSizePageInt:37]
					]
				],
		[fetchAt:4,
			data:[
				[DBName:'A', FileName:'file1', FileSizePageInt:17],
				[DBName:'A', FileName:'file2', FileSizePageInt:47],
				[DBName:'B', FileName:'file1', FileSizePageInt:4],
				[DBName:'B', FileName:'file2', FileSizePageInt:25],
				[DBName:'C', FileName:'file1', FileSizePageInt:6],
				[DBName:'C', FileName:'file2', FileSizePageInt:38]
					]
				]
	]
def KEYEXPR = ['DBName','FileName']
def test= [:] //[A.file1:[14,15], A.file2:[44,45],...]
def lstKey = []
result[0].data.each{dat->
	def tmp = []
	KEYEXPR.each{
		tmp.add(dat[it])
	}
	lstKey.add(tmp)
}
lstKey.each{keySet->
	def key = "" 
	keySet.each{
		key += it + "."
	}
	key = key.substring(0, key.length()-1)
	test[key] = []
	result['data'].each{data->
		data.each{dat->
			def isData = true
			for(int i = 0; i< KEYEXPR.size; i++){
				isData = isData && (dat[KEYEXPR[i]] == keySet[i])
			}
			if(isData){
				test[key].add(dat.FileSizePageInt)
			}
		}
	}
}
println "test:" + test
def finalData = []
test.each{
	def tmp = [:]
	tmp['name'] = it.key
	tmp['data'] = it.value
	finalData.add(tmp)
}
println "finalData: " + finalData

def xAxis = result['fetchAt']
println "xAxis: " + xAxis

//def test = []

//result['data'].each{data->
////	println data
//	data.each{dataMap->
////		println dataMap
//		def tmp = [:]
//		tmp['name'] = dataMap['DBName'] + "." + dataMap['FileName']
//		tmp['series'] = dataMap['FileSizePageInt']
////		println tmp
//		test.add(tmp)
//		println test.groupBy{it.name}
//	}
//}


def temp = "sfsdlf.dsfsd"

println temp.replace(".", "@")

println temp.replaceAll("\\.", "@")

import java.text.Normalizer.Form;

/*
 Linux 2.6.18-194.el5 (vmRHEL55x64)      11/15/2013
 avg-cpu:  %user   %nice %system %iowait  %steal   %idle
 0.53    0.00    0.59    1.24    0.00   97.64
 Device:            tps   Blk_read/s   Blk_wrtn/s   Blk_read   Blk_wrtn
 hda               4.81       181.42        51.94    1141648     326866
 hda1              0.02         0.40         0.00       2534         10
 hda2              4.79       180.97        51.94    1138786     326856
 dm-0             14.58       180.72        51.94    1137242     326856
 dm-1              0.02         0.14         0.00        896          0
 */


/*
def output = """\n\nNode,DiskBytesPersec,DiskReadBytesPersec,DiskReadsPersec,DiskTransfersPersec,DiskWriteBytesPersec,DiskWritesPersec,Name,SplitIOPerSec\n\n10.0.1.60,16352461824,6051695104,373725,1053446,10300766720,679721,0 C: G: D: E:,59154\n\n10.0.1.60,16352461824,6051695104,373725,1053446,10300766720,679721,_Total,59154\n"""
 def resultData = [:]
 //data remote
 def listColHeader = new ArrayList<String>()
 def listRowHeader = new ArrayList<String>()
 def checkColHeader = false
 def listRows = []
 output.eachLine{line->
 def row = line.trim()
 if(row.size() > 0){
 listRows.add(row)
 }
 }
 listRows.remove(0)
 //split data avg-cpu
 def tmpAvgCPU = [:]
 def avgCPU = [:]
 def tmpAvg = listRows[0].trim().split(":")
 def rowHeaderAvg = tmpAvg[1].trim().split(" ").findAll{!it.trim().equals("")}
 def rowDataAvg = listRows[1].trim().split(" ").findAll{!it.trim().equals("")}
 for(int i=0; i< rowHeaderAvg.size(); i++){
 tmpAvgCPU[rowHeaderAvg[i]] = Float.parseFloat(rowDataAvg[i])
 }
 avgCPU[tmpAvg[0]] = tmpAvgCPU
 //split data Device
 def tmpHeaderDev = listRows[2].trim().split(":")
 def rowHeaderDev = tmpHeaderDev[1].trim().split(" ").findAll{!it.trim().equals("")}
 def rowDataDev
 def dataDev = [:]
 def tmpDataDev
 for(int j=3;j < listRows.size(); j++){
 rowDataDev = listRows[j].trim().split(" ").findAll{!it.trim().equals("")}
 tmpDataDev = [:]
 for(int k=0; k<rowHeaderDev.size(); k++){
 println "sadf: " + rowDataDev[k+1].getClass()
 int number = rowDataDev[k+1]
 tmpDataDev[rowHeaderDev[k]] = number
 }
 dataDev[rowDataDev[0]] = tmpDataDev
 }
 avgCPU[tmpHeaderDev[0]] = dataDev
 println avgCPU
 */

/*output.eachLine{ line->
 if(!checkColHeader){
 listColHeader = line.trim().split(" ").findAll{!it.trim().equals("")}
 checkColHeader = true
 } else {
 def row = line.trim().split(":")
 println "row: "+row
 def rowData = row[1].trim().split(" ").findAll{!it.trim().equals("")}
 println "rowData: "+ rowData
 def tmpMapVal = [:]
 def tmpMapKey = [:]
 for(int i=0 ; i< listColHeader.size() ; i++ ){
 if(rowData.size() > i){
 tmpMapVal[listColHeader[i]] = rowData.get(i).trim()
 } else {
 tmpMapVal[listColHeader[i]] = ""
 }
 }
 println "tmpMapVal: " + tmpMapVal
 tmpMapKey[row[0].trim()] = tmpMapVal
 resultData.add(tmpMapKey)
 }
 }
 println resultData*/