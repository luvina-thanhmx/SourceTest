echo ">>>>> TEST GET METHOD OF TestJobServlet<<<<<"
content=$(curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:13111/TestJobServlet?jobFileName=TestJob_02.job')
echo "Result response data after GET request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"
if [[ $content =~ .*'{"jobContent":"JOB = [name: \"TestJob_02\"]\n\nFETCHACTION = {\n\tdef resultData = \"Message test for Case02!!!\"\n\treturn resultData\n}\n\nSENDTYPE = \"Store\"\nRESOURCEID = \"Sr/MsDbVer\"\nDBTYPE = \"@MSSQL\"\nDEST = parameters.dest\n"}'.* ]]
then
	echo "Get data of Job successfully!!!"
else
	echo "Get data of Job failure!!!"
fi
