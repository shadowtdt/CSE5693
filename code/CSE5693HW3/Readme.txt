This Project and its dependencies are managed by maven. see file /cse5693hw3/pom.xml for dependencies

To compile a new jar, in /cse5693hw3 run:  "mvn package"
This will create two jars, one with and one without dependencies. They will be created in the /target folder
** Note this requires the maven package to be installed, the code server at fit does not have this installed. Submission includes precompiled jar. **


todo  Update
To run the jar, from /cse5693hw2 run:
java -cp target/CSE5693-HW2-1.0-SNAPSHOT-jar-with-dependencies.jar com.ttoggweiler.cse5693.DecisionTreeRunner <attributeFile> <trainDataFile> <validationDataFile>

Scripts: (Dir: /scripts)
testIris.sh
testIrisNoisy.sh
testTennis.sh

Logs can be found in /logs
Log Config file: /src/main/resources/log4j2.xml
Change level="info" for the package you would like to change
** Note this will require a recompile by maven **
