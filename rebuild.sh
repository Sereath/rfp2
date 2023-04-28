#!/bin/bash
JAR=RealFirstPerson2*.jar
DST=~/.minecraft/instances/Forge\ 1.12.2/mods

echo ""
echo "deleting old jar..."
rm ./build/libs/*
rm $DST/$JAR

echo ""
echo "building..."
./gradlew build

echo ""
echo "copying replacement jar into "$DST/
cp ./build/libs/* $DST/
echo ""
echo "creating release zip..."
cd ./build/libs/
JARNAME=$(ls -1 $JAR)
ZIPNAME=$(basename $JAR .jar)".zip"
zip -9r $ZIPNAME $JARNAME
cd ../../
