import java.io.IOException;
import groovy.json.*

import com.gmongo.GMongo
import com.mongodb.util.JSON

/*class ProcessRunner{

	//build list command for connect to remote host
	List<String> listCmd = null
	def osInfo = null
	ProcessRunner(){
	}
	ProcessRunner(osInfo){
		this.osInfo = osInfo
	}
	
	def buildListCmdRemote(){
		//For Windows
		this.listCmd = new ArrayList<String>()
		if(this.osInfo.host != "" && this.osInfo.host != "localhost" ) {
			if(this.osInfo.os =='win'){
				this.listCmd.add("C:\\PsTools\\PsExec.exe")
				this.listCmd.add("\\\\" +this.osInfo.host )
				this.listCmd.add("-u")
				this.listCmd.add(this.osInfo.user)
				this.listCmd.add("-p")
				this.listCmd.add(this.osInfo.pass)
			} else {
				this.listCmd.add("ssh")
				this.listCmd.add(this.osInfo.host)
			}
		}
		return this.listCmd
	}
	def netUse(listCmd){
		listCmd.add("net")
		listCmd.add("use")
		listCmd.add("\\\\" +this.osInfo.host + "\\ipc\$")
		listCmd.add("/user:" +this.osInfo.user)
		listCmd.add(this.osInfo.pass)
		this.procExecute(listCmd, true)
	}
	//Run process closure
	def procExecute(List<String> listCmd,boolean isWaitFor){
		def resultData = [:]
		ProcessBuilder builder = new ProcessBuilder(listCmd)
		Process proc = builder.start()
		if(isWaitFor){
			resultData['exitVal'] = proc.waitFor()
		}
		//Read output and error from process executing
		InputStreamReader isrOut = new InputStreamReader(proc.getInputStream())
		InputStreamReader isrErr = new InputStreamReader(proc.getErrorStream())
		BufferedReader brOut = new BufferedReader(isrOut)
		BufferedReader brErr = new BufferedReader(isrErr)
		//Read output
		StringBuilder stdout = new StringBuilder()
		def line = null
		while((line = brOut.readLine()) != null){
			stdout.append(line + "\n")
		}
		//Read error
		StringBuilder stderr = new StringBuilder()
		line = null
		while((line = brErr.readLine()) != null){
			stderr.append(line + "\n")
		}
		resultData['out'] = stdout.toString()
		resultData['err'] = stderr.toString()
		return resultData
	}
}

def OSINFO = [host:'10.0.1.60',user:'LUVINA\\nguyenxuanluong',pass:'luong321',os:'win']


def procRunner = new ProcessRunner(OSINFO)
 def listCmd2 = new ArrayList<String>()
 procRunner.netUse(listCmd2)
 def listCmd = procRunner.buildListCmdRemote()
 listCmd.add("cmd")
 listCmd.add("/c")
 listCmd.add("ipconfig")
 println  procRunner.procExecute(listCmd,false)
 */

 File workDir = new File(System.getProperty("user.dir"));
println workDir

def mongo = new GMongo("localhost", 27017)
def db = mongo.getDB("wiperdog")
def data = "{ret:'test data'}"
def obj = JSON.parse(data)
def col = db.getCollection("test")
mongo.close()
println db


