import groovy.sql.Sql
try {
	sql = Sql.newInstance("jdbc:sqlserver://localhost:1433", "sa", "dothihuong", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
	sql.eachRow('SELECT 100', { println it })
} catch (Exception ex) {
	println ex
}