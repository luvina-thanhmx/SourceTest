package activator;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.jruby.embed.ScriptingContainer;

public class Main {
	public static void main(String[] args)  throws ScriptException, URISyntaxException {
		Main m = new Main();
		m.runRubySource(args);
	}
	public void runRubySource(String[] args) throws URISyntaxException, ScriptException{
		ScriptingContainer container = new ScriptingContainer();
		List<String> loadPaths = new ArrayList<String>();
		container.setLoadPaths(loadPaths);
	    for (int i=0; i<args.length; i++) { 
	        container.runScriptlet("ARGV << '" + args[i] + "'"); 
	    } 
		container.runScriptlet("require 'ruby/ProcessOutput.rb'");
	}
}
