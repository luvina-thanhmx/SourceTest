echo ">>>>> TEST POST METHOD OF TestJobServlet (process job connect to Postgres)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"Postgres.TestJob_06.job","data":"JOB = [name:\"Postgres.TestJob_06\"]\nQUERY = \"\"\" \n\t\t\tSELECT version();\n\"\"\"\nKEYEXPR = [\"Name\"]\nSENDTYPE = \"Store\"\nDBTYPE = \"@PGSQL\"\nMONITORINGTYPE = \"@DB\"\nRESOURCEID = \"Sr/PgDbVer\"\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Process job connect to Postgres successfully!!!"
else
	echo "Process job connect to Postgres failure!!!"
fi
