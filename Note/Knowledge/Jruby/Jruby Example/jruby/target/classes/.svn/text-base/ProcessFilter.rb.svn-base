require 'ruby/ProcessInput'

ProcessInputObj = ProcessInput.new
##
 # ProcessFilter: process raw log data
##
class ProcessFilter
	def filter(conf)
		processInputObj = ProcessInput.new
		inputMap = processInputObj.getInputData(conf)
		listFinal = Array.new
		inputMap.each do |key,value|
			finalData = Hash.new
			finalData['output'] = value['output']
			
			# Declare list result data
			resultData = Array.new
			expression = value['expression']
			# Match data follow expression
			arrayExp = expression.split(",")
			data = value['data']
			data.each_line do |line|
				tmpMapResult = Hash.new
				arrayExp.each do |element|
					if element.include? "->"
						mapRexp = element.split("->")
						strMatch = line.match(/#{mapRexp[1].strip}/).to_s
						tmpMapResult[mapRexp[0].strip] = strMatch
						line = line.sub(strMatch, '')
					end	
				end
				tmpMapResult['message'] = line.strip
				resultData << tmpMapResult
			end
			finalData['data'] = resultData
			listFinal << finalData
		end
		return listFinal
	end
	##
	 # getMapFile: process raw log data
	 # @param conf: path to configuration file
	 # @return listFinal: list result of log
	##
	def getMapFile(conf)
		# Get information config
		mapConfig = ProcessInputObj.getConfigData(conf)
		# Declare list final data
		listFinal = Array.new
		# Declare list result data
		resultData = Array.new
		# Loop map config and process log
		mapConfig['input'].each do |item|
			finalData = Hash.new
			# Type of INPUT is file
			if item['type'] == "file"
				filePath = item['path']
				expression = mapConfig['filter'][item['identify']]
				output = mapConfig['output'][item['identify']]
				if filePath != nil && filePath != '' && expression != nil && expression != '' && output != nil && output != ''
					arrayExp = expression.split(",")
					# Read file by filePath
					f = File.open(filePath, 'r')
					while line = f.gets do
						tmpMapResult = Hash.new
						arrayExp.each do |element|
							if element.include? "->"
								mapRexp = element.split("->")
								strMatch = line.match(/#{mapRexp[1].strip}/).to_s
								tmpMapResult[mapRexp[0].strip] = strMatch
								line = line.sub(strMatch, '')
							end	
						end
						tmpMapResult['message'] = line.strip
						resultData << tmpMapResult
					end
					finalData['output'] = output
					finalData['data'] = resultData
				end
				listFinal << finalData
			end
		end
		return listFinal
	end
end