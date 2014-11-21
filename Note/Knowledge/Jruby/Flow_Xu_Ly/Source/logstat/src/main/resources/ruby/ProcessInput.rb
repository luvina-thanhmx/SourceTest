require 'win32/eventlog'

class ProcessInput
	##
	 # getInputData: get configurarion and raw data
	 # @param conf
	 # @return inputMap
	##
	def getInputData(input_conf)
		if (input_conf['input_type'] == 'file')
			getDataFromFile(input_conf['input_source'],input_conf['start_pos'])
		elsif (input_conf['input_type'] == 'eventlog')
			getEventLog(input_conf['record_number_from'],input_conf['record_number_to'])
		end
	end
	
	def getDataFromFile(source_file,start_pos)
		list_logs = Array.new
		File.foreach(source_file).with_index do |line, line_num|		
				if(line_num >= start_pos ) 
					list_logs << line
				end				 
		end
		return list_logs
	end
	
	def getEventLog(recordNumberFrom, recordNumberTo)
		lstEventLog = Array.new
		Win32::EventLog.read('Application') do |log|
			if (log['record_number'] >= recordNumberFrom && log['record_number'] <= recordNumberTo)
				lstEventLog << log
			end
		end
		return lstEventLog
	end

	
	def getDataFromStream(source_file)
		
	end
	
	def my_require(gem,pack=nil)
		require gem
		rescue LoadError => e
		puts "error -----> #{e}"
		system("gem install #{pack||gem}")
		Gem.clear_paths() && require(gem) && puts("loading '#{gem}' ok!") 
	end
end