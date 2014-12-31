
var mongo = require('mongodb'),
  Server = mongo.Server,
  Db = mongo.Db;
 
var server = new Server('127.0.0.1', 27017, {auto_reconnect: true});
var db = new Db('wiperdog', server, {safe:false});
var collection = db.collection("test");
console.log("aaaaaaaaaaaaaaaaaaaaaaaaaaa123")
console.log(collection)

/*
var cluster = require('cluster');
var numCPUs = require('os').cpus().length;
var k = 0;
db.open(function(err, db) {
    var collection = db.collection("product");
	
    if (cluster.isMaster) {
        // Fork workers.
 
        for (var i = 0; i < numCPUs; i++) {
            cluster.fork();
        }
        cluster.on('exit', function(worker, code, signal) {
            console.log('worker ' + worker.process.pid + ' died');
        });
		k++;
    } else {
        setInterval(function(){
            var ran_name = Math.floor((Math.random()*68686868)+6);
            var d = new Date();
            var doc1 = {
                'name': 'insert product ' + ran_name,
                'categories': [],
                'review_count': 0,
                'reviews':[],
                'hit': 0,
                'ts': d.getTime(),
                'total_rate' : k,
                'total_point' : 0
            };
			k++;
            collection.insert(doc1, {safe:true}, function(err, result) {
                console.log(result);
            });
        }, 10);
    }
});*/