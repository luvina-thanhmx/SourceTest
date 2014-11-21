require 'win32ole'
#require 'win32/eventlog'
#require 'win32/mc'

#wmi = WIN32OLE.connect("winmgmts://")

#wmi_query = "Select * from __InstanceCreationEvent Where TargetInstance ISA 'Win32_NTLogEvent' And (TargetInstance.LogFile = 'Application')"

#events = wmi.ExecNotificationQuery(wmi_query)

#puts "------- #{events.class} ---------"


#while notification = events.NextEvent
	#puts "+++++++++++ #{notification} ++++++++++"
#	event = notification.TargetInstance
#	puts "+++++++ #{event} ++++++++"
#end

wmi = WIN32OLE.connect("winmgmts://")
processes = wmi.ExecQuery("select * from win32_process")

for process in processes do
    puts "Name: #{process.Name}"
    puts "CommandLine: #{process.CommandLine}"
    puts "CreationDate: #{process.CreationDate}"
    puts "WorkingSetSize: #{process.WorkingSetSize}"
    puts
end