echo ">>>>> TEST POST METHOD OF TestJobServlet (processing with data Subtyped)<<<<<"
content=$(curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{"jobFileName":"TestJob_09.job","data":"JOB = [ name: \"TestJob_09\" ]\n\nFETCHACTION={  \ndef data = [\n        \"D\" : [\n                [\n                        \"Message\" : \"Group D!\",\n                        \"Id\" : \"D01\"\n                ]\n        ],\n        \"M\" : [\n                [\n                        \"Message\" : \"Group M!\",\n                        \"Id\" : \"M01\"\n                ]\n        ]\n\t]\n\treturn data\n}\n\nDEST = parameters.dest\n","action":"run"}' 'http://localhost:13111/TestJobServlet' -v)
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
