This Project and its dependencies are managed by maven. see file /termproject/pom.xml for dependencies

To compile a new jar, in /termproject run:  "mvn package"
This will create two jars, one with and one without dependencies. They will be created in the /target folder
** Note this requires the maven package to be installed, the code server at fit does not have this installed. Submission includes precompiled jar. **


To run the jar, from /cse5693hw4 run:
java -cp ../target/CSE5693-HW3-1.0-SNAPSHOT-jar-with-dependencies.jar com.ttoggweiler.com.ttoggweiler.cse5693.GeneticRunner

with args:
-afile=             argument file path
-tfile=             training data file path
=vfile=             validation data file path

-learningrate
-iterations
-momentum
-treedepth
-input <tennis, iris, soy-lg, thyroid, bool>
-script  <selction, replace>

Scripts: (Dir: /scripts)
testIris.sh
testIrisNoisy.sh
testTennis.sh
testIdentity.sh

Logs can be found in /logs
Log Config file: /src/main/resources/log4j2.xml
Change level="info" for the package you would like to change
** Note this will require a recompile by maven **
-Dlog4j.configuration={path to file} to specify a log file without the need to recompile