import groovy.sql.Sql

try{
	def connection = Sql.newInstance("jdbc:sqlserver://10.0.0.184:1433", "sa", "insight", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
	println "connection: " + connection
} catch(Exception ex){
	println ex
	//println org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex)
}
