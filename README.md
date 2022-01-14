# async-profiler-JfrParser
Parser of jfr file created by async-profiler

# Dependencies
https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.6/async-profiler-2.6-linux-x64.tar.gz
- async-profiler.jar
- converter.jar

# Generate JFR file using maven
Download and extract async-profiler:
- wget https://github.com/jvm-profiling-tools/async-profiler/releases/download/v2.6/async-profiler-2.6-linux-x64.tar.gz
- tar -xzvf async-profiler-2.6-linux-x64.tar.gz

Set environment variable MAVEN_OPTS with parameters of async-profiler:
- sudo vim /etc/environment
- export MAVEN_OPTS="-agentpath:/path/async-profiler-2.5.1-linux-x64/build/libasyncProfiler.so=start,event=wall,alloc,interval=10000,file=/path/profile.jfr"
- save and exit (:wq)
- source /etc/environment

Run maven command
- mvn test

# Parse JFR file
java JfrParser /path/profile.jfr /path/profile.txt 

