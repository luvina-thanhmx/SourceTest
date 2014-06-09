echo ">>>>> TEST POST METHOD OF TestJobServlet (process job connect to SQLServer)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"SQL_Server.TestJob_04.job","data":"JOB = [name: \"SQL_Server.TestJob_04\"]\n\nQUERY = \"\"\"\n\t\tSELECT @@VERSION;  \n\"\"\"\n\nSENDTYPE = \"Store\"\nRESOURCEID = \"Sr/MsDbVer\"\nDBTYPE = \"@MSSQL\"\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Process job connect to SQLServer successfully!!!"
else
	echo "Process job connect to SQLServer failure!!!"
fi
