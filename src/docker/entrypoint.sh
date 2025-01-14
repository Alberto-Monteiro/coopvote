#!/bin/sh

echo "The application will start in ${SLEEP_APPLICATION}s..." && sleep ${SLEEP_APPLICATION}
exec java -jar app.jar
