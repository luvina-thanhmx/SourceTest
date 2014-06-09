echo ">>>>> TEST POST METHOD OF TestJobServlet (process job monitoring OS)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"OS.TestJob_07.job","data":"JOB = [name:\"OS.TestJob_07\"]\n\nFETCHACTION = {\n\tdef command = \"mpstat\"\n\tdef output = procRunner.procExecute(command,false).out\n\treturn output\n}\n\nSENDTYPE = \"Store\"\nRESOURCEID = \"Linux/CPU\"\nDEST = parameters.dest\nOSINFO = parameters.osinfo\nMONITORINGTYPE = \"@OS\"\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
echo "Result response data after POST request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"

if [[ $content =~ .*'"status": "success"'.* ]] && [[ $content =~ .*'"log": ""'.* ]]
then
	echo "Process job monitoring OS successfully!!!"
else
	echo "Process job monitoring OS failure!!!"
fi
