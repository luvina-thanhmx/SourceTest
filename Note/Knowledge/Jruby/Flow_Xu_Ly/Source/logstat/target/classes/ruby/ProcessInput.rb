require 'ruby/raw_process/ProcessConfig'
require 'ruby/raw_process/ProcessFile'
class ProcessInput
	##
	 # getInputData: get configurarion and raw data
	 # @param conf
	 # @return inputMap
	##
	def getInputData(input_conf)
		puts input_conf
			if (input_conf['input_type'] == 'file')
				getDataFromFile(input_conf['input_source'],input_conf['start_pos'],input_conf['num_of_lines'])
			end
			
	end
	
	def getDataFromFile(source_file,start_post,nums_of_line)
		list_logs = Array.new
		File.open(source_file) do |file|
			file.each_line do |line,line_number|
					if(line_number >= start_pos ) 
						if(nums_of_line)						
							if((line_number - start_pos) <= nums_of_lines )
								list_logs << line
							else
								return list_logs	
							end
						else
							list_logs << line
						end
					else
						return list_logs
					end				 
			end
		end
		return list_logs
	end
	
	def getDataFromStream(source_file)
		
	end
end
	