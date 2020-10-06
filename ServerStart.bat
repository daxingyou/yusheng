@echo off

title ÌìÌÃ2Çø Server Game(363)
cls

@java -Xoptimize -Xms256m -Xmx256m -XX:ParallelGCThreads=4 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:MetaspaceSize=64M -XX:MaxMetaspaceSize=128M -cp l1jserver.jar;lib\c3p0-0.9.1.1.jar;lib\mysql-connector-java-5.1.5-bin.jar;lib\javolution.jar;lib\mina-core-2.0.7.jar;lib\slf4j-api-1.5.2.jar;lib\log4j-1.2.16.jar;lib\slf4j-simple-1.5.2.jar;lib\commons-logging-1.1.3.jar;lib\ZHConverter.jar l1j.server.Server

ServerStart.bat