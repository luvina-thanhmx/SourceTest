echo ">>>>> TEST POST METHOD OF TestJobServlet (processing with COMMAND, FORMAT)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"TestJob_10.job","data":"JOB = [name:\"TestJob_10\"]\nCOMMAND = \"echo 123 abc def\"\nFORMAT = [ match: /([0-9]+)[ \\t]+([^ ]+)[ \\t]+(.+)/, 1: \"id\", 2: \"name\", 3: \"desc\" ]\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
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
