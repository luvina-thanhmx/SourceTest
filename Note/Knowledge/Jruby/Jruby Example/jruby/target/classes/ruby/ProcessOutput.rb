require 'ruby/ProcessFilter'
#require 'ProcessTCP'
#require 'ProcessStream'
#...

# Get config file from command line arguments
conf = ARGV[1]

processFilterObj = ProcessFilter.new
#ProcessTCPObj = ProcessTCP.new
#ProcessStreamObj = ProcessStream.new
#...

# Process output log file
dataProcessLog = processFilterObj.filter(conf)

# Process write to output file
dataProcessLog.each do |eListData|
	begin
		file = File.open(eListData['output'], 'w')
		eListData['data'].each do |eData|
			file.write("#{eData}\n")	
		end
		puts "Write to file #{eListData['output']} success !!!"
	rescue IOError => e
	  #some error occur, dir not writable etc.
	ensure
	  file.close unless file == nil
	end
end