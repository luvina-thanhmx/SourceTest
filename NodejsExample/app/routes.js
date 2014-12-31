// app/routes.js
module.exports = function(app, passport) {

	// =====================================
	// HOME PAGE (with login links) ========
	// =====================================
	app.get('/', function(req, res) {
		res.render('index.ejs'); // load the index.ejs file
	});

	// =====================================
	// LOGIN ===============================
	// =====================================
	// show the login form
	app.get('/login', function(req, res) {

		// render the page and pass in any flash data if it exists
		res.render('login.ejs', { message: req.flash('loginMessage') });
	});

	// process the login form: after user login > user views at-a-glance screen
	app.post('/login', passport.authenticate('local-login', {
		successRedirect : '/at-a-glance', // redirect to the secure profile section
		failureRedirect : '/login', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));

	// =====================================
	// SIGNUP ==============================
	// =====================================
	// show the signup form
	app.get('/signup', function(req, res) {

		// render the page and pass in any flash data if it exists
		res.render('signup.ejs', { message: req.flash('signupMessage') });
	});

	app.get('/getDataMongo', function(req, res) {
		var data;
		var mongo = require('mongodb'),
		  Server = mongo.Server,
		  Db = mongo.Db;
		 
		var server = new Server('127.0.0.1', 27017, {auto_reconnect: true});
		var db = new Db('wiperdog', server, {safe:false});
		var collection = db.collection("test");
		var test = new String()
		//test = collection
		console.log("======================================")
		console.log(test)
		res.writeHead(200, {'Content-Type': 'text/plain'});
		res.end(JSON.stringify(collection));
	});
	
	// process the signup form
	app.post('/signup', passport.authenticate('local-signup', {
		successRedirect : '/profile', // redirect to the secure profile section
		failureRedirect : '/signup', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));

	// =====================================
	// PROFILE SECTION =========================
	// =====================================
	// we will want this protected so you have to be logged in to visit
	// we will use route middleware to verify this (the isLoggedIn function)
	app.get('/at-a-glance', isLoggedIn, function(req, res) {
		res.render('at-a-glance.ejs', {
			user : req.user // get the user out of session and pass to template
		});
	});
	
	app.get('/profile', isLoggedIn, function(req, res) {
		res.render('profile.ejs', {
			user : req.user // get the user out of session and pass to template
		});
	});

	app.get('/view-chart', isLoggedIn, function(req, res) {
		res.render('view-chart.ejs', {
			user : req.user // get the user out of session and pass to template
		});
	});
	
	app.get('/view-chart-weekly-monthly-yearly', isLoggedIn, function(req, res) {
		res.render('view-chart-weekly-monthly-yearly.ejs', {
			user : req.user // get the user out of session and pass to template
		});
	});
	
	app.get('/databases', isLoggedIn, function(req, res) {
		res.render('databases.ejs', {
			user : req.user // get the user out of session and pass to template
		});
	});
	// =====================================
	// LOGOUT ==============================
	// =====================================
	app.get('/logout', function(req, res) {
		req.logout();
		res.redirect('/');
	});
};

// route middleware to make sure
function isLoggedIn(req, res, next) {

	// if user is authenticated in the session, carry on
	if (req.isAuthenticated())
		return next();

	// if they aren't redirect them to the home page
	res.redirect('/');
}
