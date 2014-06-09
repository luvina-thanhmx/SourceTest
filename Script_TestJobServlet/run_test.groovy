public class TestJobServlet{
	public static void main(String [] args){
		String dirFolderTest = System.getProperty("user.dir");
		File dir = new File(dirFolderTest);
		List<String> listCmd = new LinkedList<String>();
		def output = ""
		try {
			def pathToWiperdog = ""
			def caseTest = ""
			args.eachWithIndex {item, index ->
				// get path to wiperdog
				if ((index < (args.size() - 1)) && (item == "-p") && (args[index+1] != null)) {
					pathToWiperdog = args[index+1].replaceAll("\"", "").replaceAll("\'", "").trim()
				}
				// get case want to test
				if ((index < (args.size() - 1)) && (item == "-c") && (args[index+1] != null)) {
					caseTest = args[index+1].replaceAll("\"", "").replaceAll("\'", "").trim()
				}
			}
			// check value of -c parameter
			if(caseTest == null || caseTest == "" || !caseTest.contains("Case")) {
				println "Incorrect parameters. Please check again value of -c."
				return
			}

			def fromFolder
			// path to folder wiperdog test
			def toFolder = pathToWiperdog + "var/job/"
			// get list job for test
			def listFileTest = (new File(dirFolderTest + "/JobTest/" + caseTest + "/")).listFiles()
			def listFileRemove
			// remove job exist in var/job
			if (caseTest == "Case1") {
				// remove all file in var/job/
				listFileRemove = (new File(toFolder)).listFiles()
			} else {
				listFileRemove = listFileTest
			}

			listFileRemove.each{
				File fileTest = new File(toFolder + it.getName().toString())
				if(fileTest.exists()){
					fileTest.delete()
				}
			}
			sleep(2000)

			// copy job file to var/job
			if(caseTest != "Case3") {
				listFileTest.each{
					fromFolder = it.toString()
					listCmd = new LinkedList<String>();
					listCmd.add("/bin/cp")
					listCmd.add(fromFolder)
					listCmd.add(toFolder)
					// get output when run command
					runProcClosure(listCmd, dir, true)
				}
				println "Create job for test!!!"
				sleep(2000)
			}
			
			// run testcase
			def toCaseTest = "Script_Test/Test_" + caseTest + ".sh"
			listCmd = new LinkedList<String>();
			listCmd.add("/bin/bash")
			listCmd.add(toCaseTest)
			
			println "processing..."
			output = runProcClosure(listCmd, dir, true)
			sleep(5000)
			// check result data
			checkResultData(caseTest, output)

		}catch(Exception ex){
			ex.printStackTrace()
		}
	}

	public static void checkResultData(String caseTest, String output) {
		if (caseTest == "Case1") {
			// Test get data for create menu tree
			if (output.contains("Get menu tree successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case2") {
			// Test get data of job
			if (output.contains("Get data of Job successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case3") {
			// Test create job file
			if (output.contains("Save to job file successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case4") {
			// Test job connect to SQLServer + QUERY
			if (output.contains("Process job connect to SQLServer successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case5") {
			// Test job connect to MYSQL
			if (output.contains("Process job connect to MYSQL successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case6") {
			// Test job connect to POSTGRES
			if (output.contains("Process job connect to Postgres successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case7") {
			// Test job connect to OS
			if (output.contains("Process job monitoring OS successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case8") {
			// Test job processing with GROUPKEY + ACCUMULATE + FINALLY
			if (output.contains("Process job successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case9") {
			// Test job processing with data Subtyped
			if (output.contains("Process job successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case10") {
			// Test job processing with COMMAND
			if (output.contains("Process job successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		} else if (caseTest == "Case11") {
			// Test get data in log when job have error
			if (output.contains("Process job successfully!!!")) {
				println "====Test $caseTest successfully!!!===="
			} else {
				println "====Test $caseTest failure!!!===="
			}
		}
	}
	
	/**
	 * run command with ProcessBuider
	 * @param listCmd list command
	 * @param dir directory of project
	 * @param waitFor 
	 * @return
	 */
	public static String runProcClosure(listCmd,dir,waitFor){
		def output = [:]
		ProcessBuilder builder = new ProcessBuilder(listCmd);
		builder.redirectErrorStream(true);
		builder.directory(dir);
		Process p = builder.start();
		if(waitFor){
			output['exitVal'] = p.waitFor()
		}
		InputStream procOut  = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(procOut))
		def line = null
		StringBuffer stdin = new StringBuffer()
		while((line = br.readLine()) != null){
			stdin.append(line + "\n")
		}
		output["message"] = stdin.toString()
		return output["message"]
	}
}
