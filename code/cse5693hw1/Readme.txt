Arguments:
-file <path>
-i <iterations>
-b <boardSize>
-t <Teacher: rand, sequential, rule>


Compile Compile

Turn on debug logging by changing info to debug in file: /CSE5693/code/cse5693hw1/src/main/resources/log4j2.xml
        <logger name="com.ttoggweiler.cse5693" level="info --> debug" additivity="false">
            <AppenderRef ref="gameLog" />
            <AppenderRef ref="console" />
        </logger> mira