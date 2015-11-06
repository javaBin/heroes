#!/bin/bash

set -e

if [ "z$APP_HOME" = "z" ]; then
  echo "No APP_HOME environment variable set"
  exit 1
fi

NAME=$(app conf get launcher.name)
if [ "z$NAME" = "z" ]; then
  echo "Missing NAME in config, defaulting to main"
  NAME="main"
fi

if [ "z$APP_FOREGROUND" = "z" ]; then
  mkdir -p "${APP_HOME}/logs"
  exec >> "${APP_HOME}/logs/${NAME}.out"
  exec 2>&1
fi

if [ -f $APP_HOME/environment ]; then
  source $APP_HOME/environment
fi

exec /usr/lib/jvm/java-8-oracle/jre/bin/java -jar bin/heroes*.jar server configuration.yaml
