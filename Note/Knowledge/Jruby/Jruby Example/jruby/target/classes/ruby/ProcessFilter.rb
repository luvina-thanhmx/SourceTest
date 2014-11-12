require 'ruby/ProcessInput'

ProcessInputObj = ProcessInput.new
##
 # ProcessFilter: process raw log data
##
class ProcessFilter
	##
	 # filter: process raw log data
	 # @param conf: path to configuration file
	 # @return listFinal: list log data after process filter 
	##
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
			# Match data by expression
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
end