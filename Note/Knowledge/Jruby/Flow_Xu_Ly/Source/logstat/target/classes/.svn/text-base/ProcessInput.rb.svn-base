require 'ruby/raw_process/ProcessConfig'
require 'ruby/raw_process/ProcessFile'
class ProcessInput
	##
	 # getInputData: get configurarion and raw data
	 # @param conf
	 # @return inputMap
	##
	def getInputData(input_conf)
			if (input_conf['input_type'] == 'file')
				getDataFromFile(input_conf['input_source'])
			end
			
	end
	def getDataFromFile(source_file)
		logsString = File.read(source_file)
		return logsString
	end
	def getDataFromStream(source_file)
		
	end
end