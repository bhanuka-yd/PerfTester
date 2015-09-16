# PerfTester
This Application will run a given command number of times with given configurations

#Example
the Programme need three commandline arguements to function,

 1 - "touch @@.@@ % helloworld,txt % helloworld2,txt"
 2 - "path to configuration file"
 3 - "path to envrionment variables file"
 
 
   1 : this text passed into JAR's first arguement will create two files 
    <br />
     1.helloworld.txt<br />
     2.helloworld2.txt<br />
    
    program iterates the command replacing the @@ sign with values after % sign.
   
   2 : this file contains information about what command to run and what files to upload to the server before each test
   
   3 : this file has environmnet variables to execute before executing commands in remote server
