import java.io.IOException;
import groovy.json.*

import com.gmongo.GMongo
import com.mongodb.util.JSON
try {
	def mongo = new GMongo("153.122.22.111", 27017)
	def db = mongo.getDB("wiperdog")
	def data = "{ret:'test data'}"
	def obj = JSON.parse(data)
	def col = db.getCollection("test")
	col.insert(obj)
	mongo.close()
	println db
}catch(Exception ex){
			println org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(ex)
}