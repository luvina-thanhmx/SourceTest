echo ">>>>> TEST POST METHOD OF TestJobServlet (processing with GROUPKEY, ACCUMULATE, FINALLY)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"TestJob_08.job","data":"JOB = [name: \"TestJob_08\"]\nGROUPKEY = [\"Name\"]\nFETCHACTION = {\n\tdef resultData = [\n\t\t[\n                        \"Name\" : \"luvina\",\n                        \"Count\" : 10,\n                        \"CumCnt\" : 100\n                ],\n\t\t[\n                        \"Name\" : \"insight\",\n                        \"Count\" : 20,\n                        \"CumCnt\" : 120\n                ]\n\t]\n\treturn resultData\n}\n\nACCUMULATE = {\n\t\tif(prevOUTPUT != null && !prevOUTPUT.isEmpty()) {\n\t\t\tOUTPUT.each { rec ->\n\t\t\t\tif(prevOUTPUT[rec.Name] != null && prevOUTPUT[rec.Name] == rec.Name) {\n\t\t\t\t\trec[\"Count\"] = rec[\"CumCnt\"] - prevOUTPUT[rec.PhysicalName][\"CumCnt\"]\n\t\t\t\t} else {\n\t\t\t\t\trec[\"Count\"] = rec[\"CumCnt\"]\n\t\t\t\t}\n\t\t\t}\n\t\t} else {\n\t\t\tOUTPUT.each { rec ->\n\t\t    \t\trec[\"Count\"] = rec[\"CumCnt\"]\n\t\t\t}\n\t    \t}\n}\n\nFINALLY = {\n\tprintln \"Finally message!!!\"\n\tdef tmpData = [\n\t\t\t\"Name\" : \"thanhmx\",\n                        \"Count\" : 30,\n                        \"CumCnt\" : 300\n\t]\n\tOUTPUT.add(tmpData)\n}\n\nSENDTYPE = \"Store\"\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Process job successfully!!!"
else
	echo "Process job failure!!!"
fi
