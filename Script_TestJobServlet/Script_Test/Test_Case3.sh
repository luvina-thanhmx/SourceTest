echo ">>>>> TEST POST METHOD OF TestJobServlet (save to jobfile)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"TestJob_03.job","data":"JOB = [name: \"TestJob_03\"]\n\nFETCHACTION = {\n\tdef resultData = \"Message test for Case03!!!\"\n\treturn resultData\n}\n\nSENDTYPE = \"Store\"\nRESOURCEID = \"As/MsDbFree\"\nMONITORINGTYPE = \"@DB\"\nDBTYPE = \"@MYSQL\"\nDEST = parameters.dest\n","action":"save"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Save to job file successfully!!!"
else
	echo "Save to job file failure!!!"
fi
