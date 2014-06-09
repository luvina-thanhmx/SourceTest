import java.nio.charset.Charset

public class ProcessGenJob {
	//public static final felix_home = getFelixHome()
	public static final felix_home = System.getProperty("user.dir")
	public static String jobName = ""
	static final String CHARSET = 'utf-8'
	/**
	 * main: 
	 * @param args: 
	 */
	public static void main(String[] args) throws Exception {
		// -n jobName -f fetchaction -q query -c command -d dbexec
		args = (String[])[
			"-n",
			"TestJob01",
			"-f",
			"test 123",
			"-q",
			"aaa",
			"-c",
			"",
			"-e",
			""
		]
		if(args.length > 1 && args[0] == "-n") {
			jobName = args[1]	
		}
		// list key for create job
		def listKey = []
		def mapData = [:]
		for(int i = 0; i < args.length; i+=2){
			listKey.add(args[i])
			mapData[args[i]] = args[i+1]
		}
		// KEY_COMMON: FETCHACTION, QUERY, QUERY_VARIABLE, COMMAND, FORMAT, DBEXEC, DBEXEC_VARIABLE, 
		// CLASS_NAME, GROUP_KEY, ACCUMULATE, FINALLY, KEYEXPR, SENDTYPE, RESOURCEID, MONITORINGTYPE, DBTYPE, DEST
		// list key common for job
		def listKeyCommon = ["-n", "-f", "-q", "-c", "-d", "-qv", "-cd", "-ft", "-dv"]
		 if (writeDataToJobFile(listKey, mapData)) {
		 println ">>>>>>>>>> CREATE JOB SUCCESS <<<<<<<<<<"
		 } else {
		 println ">>>>>>>>>> CAN NOT CREATE JOB, PLEASE PUT AGAIN <<<<<<<<<<"
		 }
	}

	public static boolean writeDataToJobFile(listKey, mapData){
		def jobStr = ""
		// Process Job File
		String filePath = felix_home + "/var/job/"
		String fileName = "${jobName}.job"
		jobStr += "JOB = [name: \"${jobName}\"]\n"
		if (new File(filePath + fileName).exists()) {
			// job file exist
			def strFile = new File(filePath + fileName).text
			println strFile 
			if(listKey.contains("-f")) {
				jobStr += "FETCHACTION = {\n\t/*code FETCHACTION here*/\n}\n"
			}
		} else {
			// job file does not exist
			// GROUPKEY
			jobStr += "//GROUPKEY = []\n"
			// FETCHACTION
			if(listKey.contains("-f")) {
				if(mapData["-f"] != null && mapData["-f"] != "") {
					jobStr += 'FETCHACTION = {\n\t' + mapData["-f"] + '\n}\n'
				} else {
					jobStr += "FETCHACTION = {\n\t/*code FETCHACTION here*/\n}\n"
				}
			} else {
				jobStr += "//FETCHACTION = {\n\t/*code FETCHACTION here*/\n}\n"
			}
			// QUERY
			if(listKey.contains("-q")) {
				if(mapData["-q"] != null && mapData["-q"] != "") {
					jobStr += 'QUERY = "' + mapData["-q"] + '"\n'
				} else {
					jobStr += 'QUERY = /*code QUERY here*/\n'
				}
			} else {
				jobStr += '//QUERY = /*code QUERY here*/\n'
			}
			// COMMAND
			if(listKey.contains("-c")) {
				if(mapData["-c"] != null && mapData["-c"] != "") {
					jobStr += "COMMAND = " + mapData["-c"] + "\n"
				} else {
					jobStr += "COMMAND = /*code COMMAND here*/\n"
				}
			} else {
				jobStr += "//COMMAND = /*code COMMAND here*/\n"
			}
			// DBEXEC
			if(listKey.contains("-d")) {
				if(mapData["-d"] != null && mapData["-d"] != "") {
					jobStr += "DBEXEC = " + mapData["-d"] + "\n"
				} else {
					jobStr += "DBEXEC = /*code DBEXEC here*/\n"
				}
			} else {
				jobStr += "//DBEXEC = /*code DBEXEC here*/\n"
			}
			// QUERY_VARIABLE
			jobStr += '//QUERY_VARIABLE = ""\n'
			// FORMAT
			jobStr += '//FORMAT = ""\n'
			// DBEXEC_VARIABLE
			jobStr += '//DBEXEC_VARIABLE = ""\n'
			// ACCUMULATE
			jobStr += "//ACCUMULATE = {\n\t/*code ACCUMULATE here*/\n//}\n"
			// FINALLY
			jobStr += "//FINALLY = {\n\t/*code FINALLY here*/\n//}\n"
			// KEYEXPR
			jobStr += "//KEYEXPR = [:]\n"
			// SENDTYPE
			jobStr += '//SENDTYPE = ""\n'
			// RESOURCEID
			jobStr += '//RESOURCEID = ""\n'
			// MONITORINGTYPE
			jobStr += '//MONITORINGTYPE = ""\n'
			// DBTYPE
			jobStr += '//DBTYPE = ""\n'
			// DEST
			jobStr += '//DEST = \n'
		}
		
				
		// Set Job's String into file
		if (!writeToFile(filePath, fileName, jobStr)) {
			return false
		} else {
			return true
		}
	}

	/**
	 * Write data to file with CHARSET encode
	 * @param paramFile
	 * @return true/false
	 */
	public static boolean writeToFile(filePath, fileName, data) {
		try {
			def dataFile = new File(filePath, fileName);
			dataFile.write(data, CHARSET)
			return true
		} catch (Exception ex) {
			println "[ERROR]writeToFile: " + ex
			return false
		}
	}

	/**
	 * getFelixHome: get felix home directory
	 * @return felix_home
	 */
	public static String getFelixHome(){
		def felix_home = System.getProperty("felix.home")
		if ((felix_home == null) || (felix_home == "")) {
			File currentDir = new File(System.getProperty("bin_home"))
			felix_home = currentDir.getParent()
		}
		return felix_home
	}
}