File file = new File("D:\\test.log");

def readRandomLine(File file, int lines){
	java.io.RandomAccessFile fileHandler = null;
	try {
		fileHandler =
			new java.io.RandomAccessFile( file, "r" );
		long fileLength = file.length() - 1;
		StringBuilder sb = new StringBuilder();
		int line = 0;
		for(long filePointer = fileLength; filePointer != -1; filePointer--){
			fileHandler.seek( filePointer );
			int readByte = fileHandler.readByte();

			if( readByte == 0xA ) {
				if (line == lines) {
					if (filePointer == fileLength) {
						continue;
					} else {
						break;
					}
				}
			} else if( readByte == 0xD ) {
				line = line + 1;
				if (line == lines) {
					if (filePointer == fileLength - 1) {
						continue;
					} else {
						break;
					}
				}
			}
		   sb.append( ( char ) readByte );
		}
		sb.deleteCharAt(sb.length()-1);
		String lastLine = sb.reverse().toString();
		return lastLine;
	} catch(Exception ex) {
		println ex
	}	
}

println "data: " + readRandomLine(file, 1);