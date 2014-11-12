require 'ruby/raw_process/ProcessConfig'
require 'ruby/raw_process/ProcessFile'
#require 'ruby/raw_process/ProcessStream'

##
 # ProcessInput: get configurarion and raw data
##
class ProcessInput
	##
	 # getInputData: get configurarion and raw data
	 # @param conf
	 # @return inputMap
	##
	def getInputData(conf)
		inputMap = Hash.new
		processConfigObj = ProcessConfig.new
		config = processConfigObj.getConfigData(conf)
		config['input'].each do |i|
			tmpInputMap = Hash.new
			if i['type'] == "file"
				# process file
				processFileObj = ProcessFile.new
				tmpInputMap['data'] = processFileObj.readFile(i['path'])
				tmpInputMap['expression'] = config['filter'][i['identify']]
				tmpInputMap['output'] = config['output'][i['identify']]
			#elsif i['type'] == "stream"
				#process stream
			end
			inputMap[i['identify']] = tmpInputMap
		end
		return inputMap
	end
end