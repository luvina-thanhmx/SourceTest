import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import groovy.json.JsonSlurper;

class demoMonitorMongo {
	def static slurper = new JsonSlurper()
	
	public static void main(def args) {
		MongoClient mongo = new MongoClient( "localhost" , 27017 );
		DB db = mongo.getDB("wiperdog");
		def finalData = [:]
		println "==================="
		// Server status
		/*def serverStatusStr = db.command("serverStatus").toString()
		def mapDataServerStatus = this.slurper.parseText(serverStatusStr)
		finalData['host'] = mapDataServerStatus['host']
		finalData['version'] = mapDataServerStatus['version']
		finalData['memory'] = mapDataServerStatus['mem']
		finalData['connections'] = mapDataServerStatus['connections']
		finalData['network'] = mapDataServerStatus['network']
		
		// Database info
		def test = mongo.getDatabaseNames()
		def test2 = mongo.get
		println test
		println finalData
		// Stats
		def dbStatsStr = db.command("stats").toString()
		def mapDataDbStats = this.slurper.parseText(dbStatsStr)*/
		// collStats, replSetGetStatus, ...
	}
}
