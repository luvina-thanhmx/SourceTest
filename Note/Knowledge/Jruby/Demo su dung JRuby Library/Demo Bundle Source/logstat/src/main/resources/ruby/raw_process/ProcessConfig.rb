require 'rubygems'
require 'json'

##
 # ProcessConfig: process configuration file
##
class ProcessConfig
	##
	 # getConfigData: process configuration file
	 # @param conf: path to configuration file
	 # @return mapConfig: map config data
	##
	def getConfigData(conf)
		f = File.open(conf, 'r')
		mapConfig = Hash.new
		arrayf = f.read.split("\n\n")
		arrayf.each do |item|
			jsonInput = Array.new
			jsonFilter = Array.new
			jsonOutput = Array.new
			if item.include? "INPUT: "
				input = item.sub("INPUT: ", '')
				jsonInput = JSON.parse(input)
				mapConfig['input'] = jsonInput
			elsif item.include? "FILTER: "
				filter = item.sub("FILTER: ", '')
				jsonFilter = JSON.parse(filter)
				mapFilter = Hash.new
				jsonFilter.each do |itemFilter|
					mapFilter[itemFilter['identify']] = itemFilter['expression']
				end
				mapConfig['filter'] = mapFilter
			elsif item.include? "OUTPUT: "
				output = item.sub("OUTPUT: ", '')
				jsonOutput = JSON.parse(output)
				mapOutput = Hash.new
				jsonOutput.each do |itemOutput|
					mapOutput[itemOutput['identify']] = itemOutput['path']
				end
				mapConfig['output'] = mapOutput
			end
		end
		return mapConfig
	end
end