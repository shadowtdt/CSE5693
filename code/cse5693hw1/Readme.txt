This Project and its dependencies are managed by maven. see file /cse5693hw1/pom.xml for dependencies

To compile a new jar, in /cse5693hw1 run:  "mvn package"
This will create two jars, one with and one without dependencies. They will be created in the /target folder
** Note this requires the maven package to me installed to pull down dependencies , code server at fit does not have this.Submission includes precompiled jar. **

To run the jar, from /cse5693hw1 run:
"java -cp target/CSE5693-1.0.0-SNAPSHOT-jar-with-dependencies.jar com.ttoggweiler.cse5693.TTTGameRunner"

Arguments:
-file <path>                                    // path to text file with game traces
-i <iterations>                                // sets number of teacher iterations
-b <boardSize>                                // set board dimensions
-t <Teacher (random | sequential | rule) >       // sets which teacher to use
-s                                            //selfTeaching

You can chain teachers one after another :  "java -jar .. -i 100 -t rand -t rule"

The teacher file is located: /cse5693/inputFiles/boardSet1.txt

Two scripts are provided which simply call java with some arguments, to run these scripts make sure they are executable, then run ./<scriptFile>

testTeacher.sh uses the default teacher file to train
testNoTeacher.sh uses self teaching

Logs can be found at /logs

Turn on debug logging by changing info --> debug in file: /CSE5693/code/cse5693hw1/src/main/resources/log4j2.xml
<logger name="com.ttoggweiler.cse5693" level="info" additivity="false">
