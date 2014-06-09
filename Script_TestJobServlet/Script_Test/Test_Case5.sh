echo ">>>>> TEST POST METHOD OF TestJobServlet (process job connect to MYSQL)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"MySQL.TestJob_05.job","data":"JOB = [ name: \"MySQL.TestJob_05\" ]\nQUERY = \"\"\"\n\tSHOW GRANTS;\n\"\"\"\nSENDTYPE = \"Store\"\nMONITORINGTYPE = \"@DB\"\nRESOURCEID = \"Sr/MyDbVer\"\nDBTYPE = \"@MYSQL\"\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Process job connect to MYSQL successfully!!!"
else
	echo "Process job connect to MYSQL failure!!!"
fi
