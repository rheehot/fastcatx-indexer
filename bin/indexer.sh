#!/bin/sh
trap '' 1 2
PORT=8080
java -classpath indexer-1.1.0.jar:Altibase.jar:danawa-search-1.0.0-jar-with-dependencies.jar org.springframework.boot.loader.JarLauncher --server.port=$PORT >> output.log 2>&1 &

