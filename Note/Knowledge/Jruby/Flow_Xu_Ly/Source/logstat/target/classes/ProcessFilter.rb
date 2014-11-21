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
	def filter(logsString,filter_exp)
		puts filter_exp
		resultData = Array.new
		logsString.each_line do |line|
			tmpMapResult = Hash.new
			filter_exp.each do |element|
				if element.include? "->"
					mapRexp = element.split("->")
					strMatch = line.match(/#{mapRexp[1].strip}/).to_s
					puts strMatch
					tmpMapResult[mapRexp[0].strip] = strMatch
					line = line.sub(strMatch, '')
				end	
			end
			tmpMapResult['message'] = line.strip
			resultData << tmpMapResult
		end
		return resultData
	end
end