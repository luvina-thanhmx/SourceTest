package org.wiperdog.test.jobdsl

import static org.junit.Assert.*
import static org.ops4j.pax.exam.CoreOptions.*
import groovy.lang.GroovyClassLoader;

import javax.inject.Inject

import static org.junit.Assert.*
import static org.ops4j.pax.exam.CoreOptions.*

import org.junit.Test
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.ops4j.pax.exam.Configuration
import org.ops4j.pax.exam.Option
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy
import org.ops4j.pax.exam.spi.reactors.PerMethod
import org.ops4j.pax.exam.spi.reactors.PerClass
import org.junit.runner.JUnitCore
import org.osgi.service.cm.ManagedService
import org.codehaus.groovy.tools.RootLoader

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JobDsl_ProcessInstances_Test {
	public static final String PATH_TO_JOBCLASS     = "src/groovy/GroovyScheduledJob.groovy"
	public static final String PATH_TO_JOBDSLCLASS  = "src/groovy/JobDsl.groovy"

	String path = System.getProperty("user.dir")
	def jf
	Class jobExecutableCls
	Class jobDslCls
	def shell
	def binding
	def jobDslInst
	def groovyScheduleJobObj

	public JobDsl_ProcessInstances_Test() {
	}

	@Inject
	private org.osgi.framework.BundleContext context;

	@Configuration
	public Option[] config() {
		return options(
		cleanCaches(true),
		frameworkStartLevel(6),
		// felix log level
		systemProperty("felix.log.level").value("4"), // 4 = DEBUG
		// setup properties for fileinstall bundle.
		systemProperty("felix.home").value(path),
		systemProperty("org.quartz.scheduler.skipUpdateCheck").value("true"),
		systemProperty("org.quartz.threadPool.threadCount").value("20"),
		systemProperty("org.quartz.threadPool.class").value("org.quartz.simpl.SimpleThreadPool"),
		systemProperty("org.quartz.threadPool.threadPriority").value("5"),
		systemProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread").value("true"),
		// Pax-exam make this test code into OSGi bundle at runtime, so
		// we need "groovy-all" bundle to use this groovy test code.
		mavenBundle("org.codehaus.groovy", "groovy-all", "2.2.1").startLevel(2),
		mavenBundle("commons-collections", "commons-collections", "3.2.1").startLevel(2),
		mavenBundle("commons-beanutils", "commons-beanutils", "1.8.0").startLevel(2),
		mavenBundle("commons-digester", "commons-digester", "2.0").startLevel(2),
		wrappedBundle(mavenBundle("c3p0", "c3p0", "0.9.1.2").startLevel(3)),
		mavenBundle("org.wiperdog", "org.wiperdog.rshell.api", "0.1.0").startLevel(3),
		mavenBundle("org.quartz-scheduler", "quartz", "2.2.1").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.configloader", "0.1.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.directorywatcher", "0.1.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.rshell.api", "0.1.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.scriptsupport.groovyrunner", "0.2.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.jobmanager", "0.2.3-SNAPSHOT").startLevel(3),
		junitBundles()
		);
	}

	@Before
	public void setup() throws Exception {
		jf = context.getService(context.getServiceReference("org.wiperdog.jobmanager.JobFacade"));
		URL [] scriptpath123 = [
			new File(path + "/src/groovy").toURI().toURL()
		]
		// Load class using the inherit class loader from parent class loader
		ClassLoaderUtil lc = new ClassLoaderUtil();
		lc.addURL(scriptpath123);
		println "***** Start loading reference groovy classes"
		try{
			jobExecutableCls = lc.getCls(PATH_TO_JOBCLASS)
			jobDslCls = lc.getCls(PATH_TO_JOBDSLCLASS)
			//lc.getCls(PATH_TO_JOBEXCLASS)
			//-- Setting Groovy shell
			binding = new Binding()
			binding.setVariable("felix_home", path)
			RootLoader rootloader = new RootLoader(scriptpath123, lc.getClzzLoader())
			shell = new GroovyShell(rootloader,binding)
			jobDslInst = jobDslCls.newInstance(shell, jf, context)
		}catch(Exception e){
			println "***** ERROR: " + e
		}
		println "***** Complete setup phase!"
	}

	@After
	public void shutdown() throws Exception {
		jf = null
		jobExecutableCls = null
		jobDslCls = null
		shell = null
		binding = null
		jobDslInst = null
		groovyScheduleJobObj = null
	}

	//=============================Test add new Instances==================================
	/**
	 * Add new instances (exists: instances | not exists: job, jobclass, trigger)
	 * Instances has define the schedule.
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 * 		mapInstancesWaitJob: add to map instances file wait job
	 */
	//@Test
	public void processAddInstances_01_1() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_01_1.instances")
		shell = new GroovyShell()
		def listInstances = []
		def mapInstFileListInsts_Expected = [:]
		def mapJobListInstances_Expected = [:]
		def mapInstancesWaitJob_Expected = [:]
		// create mapInstFileListInsts Expected
		mapInstFileListInsts_Expected['testJob_01_1.instances'] = []

		// create mapJobListInstances Expected
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_01_1'] = listInstances

		// create mapInstancesWaitJob Expected
		mapInstancesWaitJob_Expected['testJob_01_1'] = instanceFile

		// Run process instances
		jobDslInst.processInstances(instanceFile)

		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
	}

	/**
	 * Add new instances (file exists: instances | file not exists: job, jobclass, trigger)
	 * Instances has not define the schedule.
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 * 		mapInstancesWaitJob: add to map instances file wait job
	 */
	//@Test
	public void processAddInstances_01_2() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_01_2.instances")
		shell = new GroovyShell()
		def listInstances = []
		def mapInstFileListInsts_Expected = [:]
		def mapJobListInstances_Expected = [:]
		def mapInstancesWaitJob_Expected = [:]
		// create mapInstFileListInsts Expected
		mapInstFileListInsts_Expected['testJob_01_2.instances'] = []

		// create mapJobListInstances Expected
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_01_2'] = listInstances
		// create mapInstancesWaitJob Expected
		mapInstancesWaitJob_Expected['testJob_01_2'] = instanceFile

		// Run process instances
		jobDslInst.processInstances(instanceFile)
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
	}

	/**
	 * Add new instances (file exists: instances, job | file not exists: jobclass, trigger)
	 * Instances has define the schedule.
	 * Job has define jobclass
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		lstJobWaitJobClass: add job to list wait jobclass
	 *		lstTriggerWaitJob: add data of job instances to list trigger wait job
	 * 		lstTriggerWaitAll: add data of job instances to list trigger wait all
	 */
	//@Test
	public void processAddInstances_02_1() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_1.instances")
		File jobFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_1.job")
		shell = new GroovyShell()
		jobDslInst.mapJobJobFile['testJob_02_1'] = jobFile
		jobDslInst.mapJobInCls["CLASS_A"] = []
		jobDslInst.mapJobInCls["CLASS_A"].add("testJob_02_1")
		// create mapInstFileListInsts Expected
		def mapInstFileListInsts_Expected = [:]
		def listInsts = []
		listInsts.add("testJob_02_1_inst_1")
		mapInstFileListInsts_Expected['testJob_02_1.instances'] = listInsts

		// create mapJobListInstances Expected
		def mapJobListInstances_Expected = [:]
		def listInstances = []
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_02_1'] = listInstances

		// create mapJobInCls Expected
		def mapJobInCls_Expected = [:]
		def listJobs = []
		listJobs.add("testJob_02_1")
		listJobs.add("testJob_02_1_inst_1")
		mapJobInCls_Expected['CLASS_A'] = listJobs

		// create lstJobWaitJobClass Expected
		def lstJobWaitJobClass_Expected = []
		def mapJobWaitJobClass = [:]
		mapJobWaitJobClass['jobClass'] = "CLASS_A"
		mapJobWaitJobClass['jobName'] = "testJob_02_1"
		lstJobWaitJobClass_Expected.add(mapJobWaitJobClass)
		jobDslInst.lstJobWaitJobClass.add(mapJobWaitJobClass)
		def mapJobWaitJobInstClass = [:]
		mapJobWaitJobInstClass['jobClass'] = "CLASS_A"
		mapJobWaitJobInstClass['jobName'] = "testJob_02_1_inst_1"
		lstJobWaitJobClass_Expected.add(mapJobWaitJobInstClass)

		// create lstTriggerWaitJob Expected
		def lstTriggerWaitJob_Expected = []
		def mapTriggerWaitJob = [:]
		mapTriggerWaitJob['trigger'] = jf.createTrigger("testJob_02_1_inst_1", 0, 10000)
		mapTriggerWaitJob['jobName'] = "testJob_02_1_inst_1"
		lstTriggerWaitJob_Expected.add(mapTriggerWaitJob)

		// create lstTriggerWaitAll Expected
		def lstTriggerWaitAll_Expected = []
		def mapTriggerWaitAll = [:]
		mapTriggerWaitAll['trigger'] = jf.createTrigger("testJob_02_1_inst_1", 0, 10000)
		mapTriggerWaitAll['jobName'] = "testJob_02_1_inst_1"
		mapTriggerWaitAll['jobClass'] = "CLASS_A"
		lstTriggerWaitAll_Expected.add(mapTriggerWaitAll)

		// Run process instances
		jobDslInst.processInstances(instanceFile)
		// Assert mapInstFileListInsts
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		// Assert mapJobListInstances
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		// Assert mapJobInCls
		assertEquals(mapJobInCls_Expected, jobDslInst.mapJobInCls)
		// Assert lstJobWaitJobClass
		assertEquals(lstJobWaitJobClass_Expected, jobDslInst.lstJobWaitJobClass)
		// Assert lstTriggerWaitJob
		assertEquals(lstTriggerWaitJob_Expected, jobDslInst.lstTriggerWaitJob)
		// Assert lstTriggerWaitAll
		assertEquals(lstTriggerWaitAll_Expected, jobDslInst.lstTriggerWaitAll)
	}

	/**
	 * Add new instances (file exists: instances, job | file not exists: jobclass, trigger)
	 * Instances has define the schedule.
	 * Job has not define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: can not create schedule of job because the trigger file not exists
	 *		jobfacade: create schedule for instances (only instance_1 has created schedule)
	 * 		lstTriggerWaitJob: can not contains trigger wait instances
	 *		lstJobWaitJobClass: can not contains job wait jobclass
	 */
	//@Test
	public void processAddInstances_02_2() throws Exception {
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_2.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_02_2.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_02_2_inst1.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_02_2_inst1.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_02_2", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// run test processInstances
		jobDslInst.processInstances(new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_2.instances"))

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		tmpMap1.putAt("instancesName", "inst1")
		tmpMap1.putAt("schedule", "10i")
		paramsMap.putAt("hostid","params_inst01")
		tmpMap1.putAt("params", paramsMap)
		listInstance.add(tmpMap1)

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_02_2",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		listInstance = ["testJob_02_2_inst1"]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_02_2.instances",listInstance)

		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		// check size of list trigger wait job is 0
		assertTrue(jobDslInst.lstTriggerWaitJob.size() == 0)

		// check size of list trigger wait all is 0
		assertTrue(jobDslInst.lstTriggerWaitAll.size() == 0)

		// check size of map default of job is 0
		assertTrue(jobDslInst.mapJobDefaultSchedule.size() == 0)

		sleep(2000)
		// check instances_1 of job has created schedule success
		assertTrue(PersistentData_Instance1.exists() && !PersistentData_Instance1.getText().isEmpty())
		assertTrue(lastExecution_Instance1.exists() && !lastExecution_Instance1.getText().isEmpty())

		// check job can not create schedule
		assertTrue(!lastExecution_Job.exists())
	}

	/**
	 * Add new instances (file exists: instances, job | file not exists: jobclass, trigger)
	 * Instances has not define the schedule.
	 * Job has define jobclass
	 *
	 * Expected:
	 *		add all instance name of job corresponding to instances file into mapInstFileListInsts
	 *		add data of instance corresponding to job into mapJobListInstances
	 *		add instance corresponding to jobclass into mapJobInCls
	 */
	//@Test
	public void processAddInstances_02_3() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_3.instances")
		File jobFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_3.job")
		shell = new GroovyShell()
		jobDslInst.mapJobJobFile['testJob_02_3'] = jobFile

		// create mapInstFileListInsts Expected
		def mapInstFileListInsts_Expected = [:]
		def listInsts = []
		listInsts.add("testJob_02_3_inst_1")
		mapInstFileListInsts_Expected['testJob_02_3.instances'] = listInsts

		// create mapJobListInstances Expected
		def mapJobListInstances_Expected = [:]
		def listInstances = []
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_02_3'] = listInstances

		// create map class corresponding to job Expected
		def mapJobInCls_Expected = [:]
		mapJobInCls_Expected["CLASS_A"] = listInsts

		// Run process instances
		jobDslInst.processInstances(instanceFile)

		// Assert mapInstFileListInsts
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		// Assert mapJobListInstances
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		// Assert mapJobInCls
		assertEquals(mapJobInCls_Expected, jobDslInst.mapJobInCls)
	}

	/**
	 * Add new instances (file exists: instances, job | file not exists: jobclass, trigger)
	 * Instances has not define the schedule.
	 * Job has not define jobclass
	 *
	 * Expected:
	 *		add all instance name of job corresponding to instances file into mapInstFileListInsts
	 *		add data of instance corresponding to job into mapJobListInstances
	 *		mapJobInCls is empty
	 */
	//@Test
	public void processAddInstances_02_4() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_4.instances")
		File jobFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_02_4.job")
		shell = new GroovyShell()
		jobDslInst.mapJobJobFile['testJob_02_4'] = jobFile

		// create mapInstFileListInsts Expected
		def mapInstFileListInsts_Expected = [:]
		def listInsts = []
		listInsts.add("testJob_02_4_inst_1")
		mapInstFileListInsts_Expected['testJob_02_4.instances'] = listInsts

		// create mapJobListInstances Expected
		def mapJobListInstances_Expected = [:]
		def listInstances = []
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_02_4'] = listInstances

		// Run process instances
		jobDslInst.processInstances(instanceFile)

		// Assert mapInstFileListInsts
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		// Assert mapJobListInstances
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		// Assert mapJobInCls
		assertTrue(jobDslInst.mapJobInCls.size() == 0)
	}

	/**
	 * Add new instances (file exists: instances, job, jobclass | file not exists: trigger)
	 * Instances file contains 2 instance of job:
	 * 		- instances_1 has not defined schedule.
	 * 		- instances_2 has defined schedule corresponding.
	 * Job has define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: can not create schedule of job because the trigger file not exists
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		jobfacade: create schedule for instances corresponding to schedule defined (only instance_2 has created schedule)
	 * 		lstJobWaitJobClass: can not contains job wait jobclass
	 * 		lstTriggerWaitJob: can not contains trigger wait instances
	 * 		lstTriggerWaitAll: can not contains trigger wait all
	 */
	//@Test
	public void processAddInstances_03() throws Exception {
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_03.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_03.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_03_inst1.txt")
		def PersistentData_Instance2 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_03_inst2.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_03_inst1.txt")
		def lastExecution_Instance2 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_03_inst2.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap tmpMap2 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_03", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// create jobclass for instances and job
		jobDslInst.jobfacade.createJobClass("class_A")

		// run test processInstances
		boolean ret = jobDslInst.processInstances(new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_03.instances"))

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		tmpMap1.putAt("instancesName", "inst1")
		tmpMap1.putAt("schedule", null)
		paramsMap.putAt("hostid","params_inst01")
		tmpMap1.putAt("params", paramsMap)
		listInstance.add(tmpMap1)

		tmpMap2.putAt("instancesName", "inst2")
		tmpMap2.putAt("schedule", "10")
		paramsMap = new LinkedHashMap()
		paramsMap.putAt("hostid","params_inst02")
		tmpMap2.putAt("params", paramsMap)
		listInstance.add(tmpMap2)

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_03",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		listInstance = [
			"testJob_03_inst1",
			"testJob_03_inst2"
		]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_03.instances",listInstance)
		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("class_A",listInstance)
		// check instances of job corresponding to jobclass
		assertEquals(tmpMap, jobDslInst.mapJobInCls)

		// check map default of schedule is empty
		assertTrue(jobDslInst.mapJobDefaultSchedule.size() == 0)

		// check size of list job wait class is 0
		assertTrue(jobDslInst.lstJobWaitJobClass.size() == 0)

		// check size of list trigger wait job is 0
		assertTrue(jobDslInst.lstTriggerWaitJob.size() == 0)

		// check size of list trigger wait all is 0
		assertTrue(jobDslInst.lstTriggerWaitAll.size() == 0)

		sleep(2000)
		// check instances_2 of job has created schedule success
		assertTrue(PersistentData_Instance2.exists() && !PersistentData_Instance2.getText().isEmpty())
		assertTrue(lastExecution_Instance2.exists() && !lastExecution_Instance2.getText().isEmpty())

		// check instances_1 of job can not create schedule
		assertTrue(!PersistentData_Instance1.exists())
		assertTrue(!lastExecution_Instance1.exists())

		// check job can not create schedule
		assertTrue(!lastExecution_Job.exists())
	}

	/**
	 * Add new instances (file exists: instances, job, jobclass, trigger)
	 * Instances file contains 2 instance of job:
	 * 		- instances_1 has define schedule and has not define params.
	 * 		- instances_2 has define schedule and params.
	 * Job has define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: create schedule for job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		jobfacade: create schedule for all instances with schedule corresponding to instances file
	 * 		lstJobWaitJobClass: can not contains job wait jobclass
	 * 		lstTriggerWaitJob: can not contains trigger wait instances
	 * 		lstTriggerWaitAll: can not contains trigger wait all
	 */
	//@Test
	public void processAddInstances_04_1() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_04_1.instances")
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_04_1.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_1.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_04_1_inst1.txt")
		def PersistentData_Instance2 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_04_1_inst2.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_1_inst1.txt")
		def lastExecution_Instance2 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_1_inst2.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap tmpMap2 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_04_1", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// create map default of schedule corresponding to trigger file of job
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_1", "10")
		jobDslInst.mapJobDefaultSchedule = tmpMap

		// create jobclass for instances and job
		jobDslInst.jobfacade.createJobClass("class_A")

		// run test processInstances
		jobDslInst.processInstances(instanceFile)

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		// get list instances from instances file
		shell = new GroovyShell()
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstance.add(mapInstances)
		}

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_1",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		listInstance = [
			"testJob_04_1_inst1",
			"testJob_04_1_inst2"
		]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_1.instances",listInstance)
		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("class_A",listInstance)
		// check instances of job corresponding to jobclass
		assertEquals(tmpMap, jobDslInst.mapJobInCls)

		// check size of list job wait class is 0
		assertTrue(jobDslInst.lstJobWaitJobClass.size() == 0)

		// check size of list trigger wait job is 0
		assertTrue(jobDslInst.lstTriggerWaitJob.size() == 0)

		// check size of list trigger wait all is 0
		assertTrue(jobDslInst.lstTriggerWaitAll.size() == 0)
		sleep(2000)
		// check instances of job create schedule success
		assertTrue(PersistentData_Instance1.exists() && !PersistentData_Instance1.getText().isEmpty())
		assertTrue(PersistentData_Instance2.exists() && !PersistentData_Instance2.getText().isEmpty())
		assertTrue(lastExecution_Instance1.exists() && !lastExecution_Instance1.getText().isEmpty())
		assertTrue(lastExecution_Instance2.exists() && !lastExecution_Instance2.getText().isEmpty())
		// check job can not create schedule because this test is not call processJob()
		assertTrue(!lastExecution_Job.exists())
	}

	/**
	 * Add new instances (file exists: instances, job, jobclass, trigger)
	 * Instances file contains 2 instance of job:
	 * 		- instances_1 has not define schedule and params.
	 * 		- instances_2 has not define schedule but has define params.
	 * Job has define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: create schedule for job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		jobfacade: create schedule for all instances with "default schedule"
	 * 		lstJobWaitJobClass: can not contains job wait jobclass
	 * 		lstTriggerWaitJob: can not contains trigger wait instances
	 * 		lstTriggerWaitAll: can not contains trigger wait all
	 */
	//@Test
	public void processAddInstances_04_2() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_04_2.instances")
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_04_2.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_2.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_04_2_inst1.txt")
		def PersistentData_Instance2 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_04_2_inst2.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_2_inst1.txt")
		def lastExecution_Instance2 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_04_2_inst2.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap tmpMap2 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_04_2", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// create map default of schedule corresponding to trigger file of job
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_2", "10")
		jobDslInst.mapJobDefaultSchedule = tmpMap

		// create jobclass for instances and job
		jobDslInst.jobfacade.createJobClass("class_A")

		// run test processInstances
		jobDslInst.processInstances(instanceFile)

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		// get list instances from instances file
		shell = new GroovyShell()
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstance.add(mapInstances)
		}

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_2",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		listInstance = [
			"testJob_04_2_inst1",
			"testJob_04_2_inst2"
		]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_04_2.instances",listInstance)
		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		tmpMap = new LinkedHashMap()
		tmpMap.putAt("class_A",listInstance)
		// check instances of job corresponding to jobclass
		assertEquals(tmpMap, jobDslInst.mapJobInCls)

		// check size of list job wait class is 0
		assertTrue(jobDslInst.lstJobWaitJobClass.size() == 0)

		// check size of list trigger wait job is 0
		assertTrue(jobDslInst.lstTriggerWaitJob.size() == 0)

		// check size of list trigger wait all is 0
		assertTrue(jobDslInst.lstTriggerWaitAll.size() == 0)
		sleep(2000)
		// check instances of job create schedule success
		assertTrue(PersistentData_Instance1.exists() && !PersistentData_Instance1.getText().isEmpty())
		assertTrue(PersistentData_Instance2.exists() && !PersistentData_Instance2.getText().isEmpty())
		assertTrue(lastExecution_Instance1.exists() && !lastExecution_Instance1.getText().isEmpty())
		assertTrue(lastExecution_Instance2.exists() && !lastExecution_Instance2.getText().isEmpty())
		// check job can not create schedule because this test is not call processJob()
		assertTrue(!lastExecution_Job.exists())
	}

	/**
	 * Add new instances (file exists: instances, job, trigger | file not exists: jobclass)
	 * Instances file contains 2 instance of job:
	 * 		- instances_1 has define schedule.
	 * 		- instances_2 has not define schedule.
	 * Job has define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: cannot create schedule for job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		jobfacade: cannot create schedule for all instances
	 * 		lstJobWaitJobClass: add job to list wait jobclass
	 * 		lstTriggerWaitJob: add data of job instances to list trigger wait job
	 * 		lstTriggerWaitAll: add data of job instances to list trigger wait all
	 */
	//@Test
	public void processAddInstances_05_1() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_05_1.instances")
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_05_1.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_1.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_05_1_inst1.txt")
		def PersistentData_Instance2 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_05_1_inst2.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_1_inst1.txt")
		def lastExecution_Instance2 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_1_inst2.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap tmpMap2 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_05_1", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// create map default of schedule corresponding to trigger file of job
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_1", "10")
		jobDslInst.mapJobDefaultSchedule = tmpMap

		// run test processInstances
		jobDslInst.processInstances(instanceFile)

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		// get list instances from instances file
		shell = new GroovyShell()
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstance.add(mapInstances)
		}

		// create mapJobListInstances Expected
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_1",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		// create mapInstFileListInsts Expected
		listInstance = [
			"testJob_05_1_inst1",
			"testJob_05_1_inst2"
		]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_1.instances",listInstance)
		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		// create mapJobInCls Expected
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("CLASS_A",listInstance)
		// check instances of job corresponding to jobclass
		assertEquals(tmpMap, jobDslInst.mapJobInCls)

		// create lstJobWaitJobClass Expected
		def lstJobWaitJobClass_Expected = []
		listInstance.each {
			def mapJobWaitJobInstClass = [:]
			mapJobWaitJobInstClass['jobClass'] = "CLASS_A"
			mapJobWaitJobInstClass['jobName'] = it
			lstJobWaitJobClass_Expected.add(mapJobWaitJobInstClass)
		}

		// create lstTriggerWaitJob Expected
		def lstTriggerWaitJob_Expected = []
		listInstance.each {
			def mapTriggerWaitJob = [:]
			mapTriggerWaitJob['trigger'] = jf.createTrigger(it, 0, 10000)
			mapTriggerWaitJob['jobName'] = it
			lstTriggerWaitJob_Expected.add(mapTriggerWaitJob)
		}

		// create lstTriggerWaitAll Expected
		def lstTriggerWaitAll_Expected = []
		listInstance.each {
			def mapTriggerWaitAll = [:]
			mapTriggerWaitAll['trigger'] = jf.createTrigger(it, 0, 10000)
			mapTriggerWaitAll['jobName'] = it
			mapTriggerWaitAll['jobClass'] = "CLASS_A"
			lstTriggerWaitAll_Expected.add(mapTriggerWaitAll)
		}

		// Assert lstJobWaitJobClass
		assertEquals(lstJobWaitJobClass_Expected, jobDslInst.lstJobWaitJobClass)
		// Assert lstTriggerWaitJob
		assertEquals(lstTriggerWaitJob_Expected, jobDslInst.lstTriggerWaitJob)
		// Assert lstTriggerWaitAll
		assertEquals(lstTriggerWaitAll_Expected, jobDslInst.lstTriggerWaitAll)
	}

	/**
	 * Add new instances (file exists: instances, job, trigger | file not exists: jobclass)
	 * Instances file contains 2 instance of job:
	 * 		- instances_1 has define schedule.
	 * 		- instances_2 has not define schedule.
	 * Job has not define jobclass.
	 *
	 * Expected:
	 *		mapJobListInstances: add to map instances corresponding to job
	 *		mapInstancesWaitJob: can not contains instances wait job
	 *		mapInstFileListInsts: add all instance of job corresponding to instances file
	 *		jobfacade: create schedule for job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		jobfacade: create schedule for all instances
	 * 		lstJobWaitJobClass: can not contains job wait jobclass
	 * 		lstTriggerWaitJob: can not contains trigger wait instances
	 * 		lstTriggerWaitAll: can not contains trigger wait all
	 */
	//@Test
	public void processAddInstances_05_2() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_05_2.instances")
		def pathToJob = new File(path + "/src/resources/jobdsl/processInstances/addNewInstances/testJob_05_2.job")
		def lastExecution_Job = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_2.txt")
		def PersistentData_Instance1 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_05_2_inst1.txt")
		def PersistentData_Instance2 = new File(path + "/tmp/monitorjobdata/PersistentData/testJob_05_2_inst2.txt")
		def lastExecution_Instance1 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_2_inst1.txt")
		def lastExecution_Instance2 = new File(path + "/tmp/monitorjobdata/LastExecution/testJob_05_2_inst2.txt")
		def listPersistentDataFile = []
		def listLastExecutionFile = []
		// clear file before run test
		listPersistentDataFile = (new File(path + "/tmp/monitorjobdata/PersistentData/")).listFiles()
		listPersistentDataFile.each { it.delete() }
		// clear file before run test
		listLastExecutionFile = (new File(path + "/tmp/monitorjobdata/LastExecution/")).listFiles()
		listLastExecutionFile.each { it.delete() }
		sleep(2000)

		def listInstance = []
		LinkedHashMap tmpMap = new LinkedHashMap()
		LinkedHashMap tmpMap1 = new LinkedHashMap()
		LinkedHashMap tmpMap2 = new LinkedHashMap()
		LinkedHashMap paramsMap = new LinkedHashMap()

		// create map job corresponding to jobfile
		tmpMap.putAt("testJob_05_2", pathToJob)
		jobDslInst.mapJobJobFile = tmpMap

		// create map default of schedule corresponding to trigger file of job
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_2", "10")
		jobDslInst.mapJobDefaultSchedule = tmpMap

		// run test processInstances
		jobDslInst.processInstances(instanceFile)

		// check can not contains instances wait job
		assertTrue(jobDslInst.mapInstancesWaitJob.size() == 0)

		// get list instances from instances file
		shell = new GroovyShell()
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstance.add(mapInstances)
		}

		// create mapJobListInstances Expected
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_2",listInstance)
		// check map instances corresponding to job
		assertEquals(tmpMap, jobDslInst.mapJobListInstances)

		// create mapInstFileListInsts Expected
		listInstance = [
			"testJob_05_2_inst1",
			"testJob_05_2_inst2"
		]
		tmpMap = new LinkedHashMap()
		tmpMap.putAt("testJob_05_2.instances",listInstance)
		// check instances of job corresponding to instances file
		assertEquals(tmpMap, jobDslInst.mapInstFileListInsts)

		// check size of map job in class is 0
		assertTrue(jobDslInst.mapJobInCls.size() == 0)

		// check size of list job wait class is 0
		assertTrue(jobDslInst.lstJobWaitJobClass.size() == 0)

		// check size of list trigger wait job is 0
		assertTrue(jobDslInst.lstTriggerWaitJob.size() == 0)

		// check size of list trigger wait all is 0
		assertTrue(jobDslInst.lstTriggerWaitAll.size() == 0)
		sleep(2000)
		// check instances of job create schedule success
		assertTrue(PersistentData_Instance1.exists() && !PersistentData_Instance1.getText().isEmpty())
		assertTrue(PersistentData_Instance2.exists() && !PersistentData_Instance2.getText().isEmpty())
		assertTrue(lastExecution_Instance1.exists() && !lastExecution_Instance1.getText().isEmpty())
		assertTrue(lastExecution_Instance2.exists() && !lastExecution_Instance2.getText().isEmpty())
		// check job can not create schedule because this test is not call processJob()
		assertTrue(!lastExecution_Job.exists())
	}

	//=============================Test Update Instances==================================
	/**
	 * Update instances (file exists: instances | file not exists: job, trigger, jobclass)
	 * Existed 3 instances file: A, B, C.
	 * All instances file has define 2 instance:
	 * 		- instance 1 has define the schedule.
	 * 		- instance 2 has not define the schedule.
	 * 		=> 3 instances A, B, C will be wait job
	 * Then update:
	 * 		instances file A update value of instance (instance_name, schedule, params)
	 * 		instances file name B to instances file name D
	 * 		remove instances file C
	 * 			=> 	add new instances D to map wait job D
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 * 		mapInstancesWaitJob: add to map instances file wait job
	 */
	//@Test
	public void processUpdateInstances_01_1() throws Exception {
		def listInstancesFileName = ["testJob_01_A", "testJob_01_B", "testJob_01_C"]
		def mapInstFileListInsts_Expected = [:]
		def mapJobListInstances_Expected = [:]
		def mapInstancesWaitJob_Expected = [:]
		def listInstances
		shell = new GroovyShell()
		File instanceFile
		def mapInstances
		def instEval
		
		// process for each instances
		listInstancesFileName.each {tmp->
			instanceFile = new File(path + "/src/resources/jobdsl/processInstances/updateInstances/" + tmp + ".instances")
			
			// create mapInstFileListInsts Expected
			mapInstFileListInsts_Expected[tmp + '.instances'] = []
	
			// create mapJobListInstances Expected
			instEval = shell.evaluate(instanceFile)
			listInstances = []
			instEval.each {
				mapInstances = [:]
				mapInstances['instancesName'] = it.key
				mapInstances['schedule'] = it.value.schedule
				mapInstances['params'] = it.value.params
				listInstances.add(mapInstances)
			}
			mapJobListInstances_Expected[tmp] = listInstances
	
			// create mapInstancesWaitJob Expected
			mapInstancesWaitJob_Expected[tmp] = instanceFile
	
			// Run process instances
			jobDslInst.processInstances(instanceFile)
		}
		
		// assert output
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
		
		// create data for test update instances
		listInstancesFileName = ["testJob_01_A", "testJob_01_D"]
		listInstancesFileName.each {tmp->
			instanceFile = new File(path + "/src/resources/jobdsl/processInstances/updateInstances/" + tmp + ".instances")
			
			// create mapInstFileListInsts Expected
			mapInstFileListInsts_Expected[tmp + '.instances'] = []
	
			// create mapJobListInstances Expected
			instEval = shell.evaluate(instanceFile)
			listInstances = []
			instEval.each {
				mapInstances = [:]
				mapInstances['instancesName'] = it.key
				mapInstances['schedule'] = it.value.schedule
				mapInstances['params'] = it.value.params
				listInstances.add(mapInstances)
			}
			mapJobListInstances_Expected[tmp] = listInstances
	
			// create mapInstancesWaitJob Expected
			mapInstancesWaitJob_Expected[tmp] = instanceFile
	
			// Run process instances
			jobDslInst.processInstances(instanceFile)
		}
		
		// assert output
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
	}
	
	/**
	 * Update instances (file exists: instances | file not exists: job, trigger, jobclass)
	 * Existed instances A contains: instance_1, instance_2, instance_3, instance_4
	 * 		instance_1 and instance_4 has define schedule and params.
	 * 		instance_2 has define params but has not define schedule.
	 * 		instance_3 has define schedule but has not define params.
	 * 			=> instances 1, 2, 3, 4 will be wait job A
	 * Then update: 
	 * 		From instances name is instance_1 to instance_5.
	 * 		instance_2 has define schedule but has not define params.
	 * 		instance_3 has define params but has not define schedule.
	 * 		remove instance_4
	 * 			=> instances 2, 3, 5 will be wait job A
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 * 		mapInstancesWaitJob: add to map instances file wait job
	 */
	//@Test
	public void processUpdateInstances_01_2() throws Exception {
		def tmpDataTest = '''[
						"inst_1": [
							"schedule":"5i",
							"params": ["hostid": "params_inst_01"]
						],
						"inst_2": [
							"params": ["hostid": "params_inst_02"]
						],
						"inst_3": [
							"schedule":"10i"
						],
						"inst_4": [
							"schedule":"15i",
							"params": ["hostid": "params_inst_04"]
						]
					]'''
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/updateInstances/testJob_01_2.instances")
		instanceFile.setText(tmpDataTest)
		
		shell = new GroovyShell()
		def listInstances = []
		def mapInstFileListInsts_Expected = [:]
		def mapJobListInstances_Expected = [:]
		def mapInstancesWaitJob_Expected = [:]
		
		// create mapInstFileListInsts Expected
		mapInstFileListInsts_Expected['testJob_01_2.instances'] = []

		// create mapJobListInstances Expected		
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected["testJob_01_2"] = listInstances

		// create mapInstancesWaitJob Expected
		mapInstancesWaitJob_Expected["testJob_01_2"] = instanceFile

		// Run process instances
		jobDslInst.processInstances(instanceFile)
				
		// assert output
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
		
		// create data for test update instances
		tmpDataTest = '''[
						"inst_5": [
							"schedule":"5i",
							"params": ["hostid": "params_inst_01"]
						],
						"inst_2": [
							"schedule":"2i",
						],
						"inst_3": [
							"params": ["hostid": "params_inst_03"]
						]
					]'''
		instanceFile.setText(tmpDataTest)
		
		// create mapJobListInstances Expected
		instEval = shell.evaluate(instanceFile)
		listInstances = []
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected["testJob_01_2"] = listInstances
		
		// Run process instances
		jobDslInst.processInstances(instanceFile)
		
		// assert output
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		assertEquals(mapInstancesWaitJob_Expected, jobDslInst.mapInstancesWaitJob)
	}
	
	/**
	 * Update instances (file exists: instances, job | file not exists: jobclass, trigger)
	 * Existed:
	 *  3 instances file A, B, C corresponding to job A, B, C.
   	 *	4 job: A, B, C, D
   	 *	Instances has define the schedule and Job has define jobclass.
	 *      => 	all instances and job will be wait jobclass
	 * 			data of instances file A mapping to job A
	 * 			data of instances file B mapping to job B
	 *			data of instances file C mapping to job C
   	 * Then update:
	 *		instances file name A to instances file name E
	 *		instances file name B to instances file name D
	 *		instances file C update value of instance (instance_name, schedule, params)
	 *		=> 	data of instances file E will be add to map or list corresponding
	 *			new data of instances file C mapping to job C
	 *			data of instances file D mapping to job D
	 *			all data of instances file A will be remove
	 *			all data of instances file B will be remove
	 *			all job and instances C, D wait jobclass
	 *			instances file E wait job
	 *
	 * Expected:
	 * 		mapInstFileListInsts: add all instance of job corresponding to instances file
	 * 		mapJobListInstances: add to map instances corresponding to job
	 *		mapJobInCls: add instances corresponding to jobclass
	 *		lstJobWaitJobClass: add job to list wait jobclass
	 *		lstTriggerWaitJob: add data of job instances to list trigger wait job
	 * 		lstTriggerWaitAll: add data of job instances to list trigger wait all
	 */
	@Test
	public void processUpdateInstances_02_1() throws Exception {
		File instanceFile = new File(path + "/src/resources/jobdsl/processInstances/updateInstances/testJob_02_1.instances")
		File jobFile = new File(path + "/src/resources/jobdsl/processInstances/updateInstances/testJob_02_1.job")
		shell = new GroovyShell()
		jobDslInst.mapJobJobFile['testJob_02_1'] = jobFile
		jobDslInst.mapJobInCls["CLASS_A"] = []
		jobDslInst.mapJobInCls["CLASS_A"].add("testJob_02_1")
		// create mapInstFileListInsts Expected
		def mapInstFileListInsts_Expected = [:]
		def listInsts = []
		listInsts.add("testJob_02_1_inst_1")
		mapInstFileListInsts_Expected['testJob_02_1.instances'] = listInsts

		// create mapJobListInstances Expected
		def mapJobListInstances_Expected = [:]
		def listInstances = []
		def instEval = shell.evaluate(instanceFile)
		instEval.each {
			def mapInstances = [:]
			mapInstances['instancesName'] = it.key
			mapInstances['schedule'] = it.value.schedule
			mapInstances['params'] = it.value.params
			listInstances.add(mapInstances)
		}
		mapJobListInstances_Expected['testJob_02_1'] = listInstances

		// create mapJobInCls Expected
		def mapJobInCls_Expected = [:]
		def listJobs = []
		listJobs.add("testJob_02_1")
		listJobs.add("testJob_02_1_inst_1")
		mapJobInCls_Expected['CLASS_A'] = listJobs

		// create lstJobWaitJobClass Expected
		def lstJobWaitJobClass_Expected = []
		def mapJobWaitJobClass = [:]
		mapJobWaitJobClass['jobClass'] = "CLASS_A"
		mapJobWaitJobClass['jobName'] = "testJob_02_1"
		lstJobWaitJobClass_Expected.add(mapJobWaitJobClass)
		jobDslInst.lstJobWaitJobClass.add(mapJobWaitJobClass)
		def mapJobWaitJobInstClass = [:]
		mapJobWaitJobInstClass['jobClass'] = "CLASS_A"
		mapJobWaitJobInstClass['jobName'] = "testJob_02_1_inst_1"
		lstJobWaitJobClass_Expected.add(mapJobWaitJobInstClass)

		// create lstTriggerWaitJob Expected
		def lstTriggerWaitJob_Expected = []
		def mapTriggerWaitJob = [:]
		mapTriggerWaitJob['trigger'] = jf.createTrigger("testJob_02_1_inst_1", 0, 10000)
		mapTriggerWaitJob['jobName'] = "testJob_02_1_inst_1"
		lstTriggerWaitJob_Expected.add(mapTriggerWaitJob)

		// create lstTriggerWaitAll Expected
		def lstTriggerWaitAll_Expected = []
		def mapTriggerWaitAll = [:]
		mapTriggerWaitAll['trigger'] = jf.createTrigger("testJob_02_1_inst_1", 0, 10000)
		mapTriggerWaitAll['jobName'] = "testJob_02_1_inst_1"
		mapTriggerWaitAll['jobClass'] = "CLASS_A"
		lstTriggerWaitAll_Expected.add(mapTriggerWaitAll)

		// Run process instances
		jobDslInst.processInstances(instanceFile)
		// Assert mapInstFileListInsts
		assertEquals(mapInstFileListInsts_Expected, jobDslInst.mapInstFileListInsts)
		// Assert mapJobListInstances
		assertEquals(mapJobListInstances_Expected, jobDslInst.mapJobListInstances)
		// Assert mapJobInCls
		assertEquals(mapJobInCls_Expected, jobDslInst.mapJobInCls)
		// Assert lstJobWaitJobClass
		assertEquals(lstJobWaitJobClass_Expected, jobDslInst.lstJobWaitJobClass)
		// Assert lstTriggerWaitJob
		assertEquals(lstTriggerWaitJob_Expected, jobDslInst.lstTriggerWaitJob)
		// Assert lstTriggerWaitAll
		assertEquals(lstTriggerWaitAll_Expected, jobDslInst.lstTriggerWaitAll)
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}