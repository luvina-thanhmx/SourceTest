package impl;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.Bundle;

import service.LogStat;
/**
 * Implement of LogStat service
 * @author nguyenxuanluong
 *
 */
public class LogStatImpl implements LogStat{
	Bundle bundle;
	public LogStatImpl(Bundle bundle){
		this.bundle = bundle;
	}

	/**
	 * Monitoring logs
	 * @param args : An array of paramters 
	 */
	@Override
	public void runLogStat(String[] args) {
		try {
			ScriptingContainer container = new OSGiScriptingContainer(this.bundle,LocalContextScope.SINGLETHREAD,LocalVariableBehavior.TRANSIENT);
			container.setHomeDirectory("classpath:/META-INF/jruby.home");
			System.out.println("LogStartService Running ...");
			container.setArgv(args);
			container.runScriptlet("require 'ruby/ProcessOutput.rb'");
			System.out.println("LogStartService Completed ...");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		
	}

}
