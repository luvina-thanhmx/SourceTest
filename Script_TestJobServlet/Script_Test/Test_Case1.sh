echo ">>>>> TEST GET METHOD OF TestJobServlet<<<<<"
content=$(curl -i -H "Accept: application/json" -H "Content-Type: application/json" 'http://localhost:13111/TestJobServlet')
echo "Result response data after GET request:"
echo "--------------------------------------------"
echo $content
echo "--------------------------------------------"
if [[ $content =~ .*'<li>Database_Structure<ul><li><a>MySQL.Database_Structure.Test01.job</a></li></ul></li>'.* ]] && [[ $content =~ .*'<li>Others<ul><li><a>MySQL.Database.job</a></li></ul></li>'.* ]] && [[ $content =~ .*'<li>Database_Area<ul><li><a>SQL_Server.Database_Area.Test01.job</a></li></ul></li>'.* ]] && [[ $content =~ .*'<li>Performance<ul><li><a>Postgres.Performance.Test01.job</a></li></ul></li>'.* ]] && [[ $content =~ .*'<li>Others<ul><li><a>TestJob_01.job</a></li></ul></li>'.* ]]
then
	echo "Get menu tree successfully!!!"
else
	echo "Get menu tree failure!!!"
fi
