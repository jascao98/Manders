#!/bin/bash

mvn clean install
mvn dependency:copy-dependencies
./run.sh