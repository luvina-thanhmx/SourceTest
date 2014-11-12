class ProcessFile
	def readFile(path)
		content = nil
		file = File.open(path, 'r')
		begin
			content = file.read
		rescue IOError => e
		  	# some error occur, can not read fle
		  	puts e
		ensure
		  	file.close unless file == nil
		end
		return content
	end
end