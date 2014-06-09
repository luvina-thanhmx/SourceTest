echo ">>>>> TEST POST METHOD OF TestJobServlet (processing with job have error)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"TestJob_11.job","data":"JOB = [name:\"TestJob_11\"]\nFETCHACTION = {\n\t// mongodb does not defined\n\tdef result = mongodb.getDB()\n\treturn result\n}\nSENDTYPE = \"Store\"\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "failed"'.* ]] && [[ $content =~ .*'"jobData": ""'.* ]]
then
	echo "Process job successfully!!!"
else
	echo "Process job failure!!!"
fi
