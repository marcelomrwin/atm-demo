#!/bin/bash

mvn clean package oc:resource -DskipTests -Popenshift
mvn oc:undeploy -Popenshift
mvn clean package oc:build oc:resource oc:apply -Popenshift -DskipTests