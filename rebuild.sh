#!/bin/bash
JAR=RealFirstPerson2*.jar
DST1=~/.multimc/instances/ForgeDev/.minecraft/mods
DST2=~/.multimc/instances/ForgeDev2/.minecraft/mods
DST3=~/.multimc/instances/KilljoysMC_v2.0/.minecraft/mods

echo ""
echo "deleting old jar..."
rm ./build/libs/*
rm $DST1/$JAR
rm $DST2/$JAR
rm $DST3/$JAR

echo ""
echo "building..."
./gradlew build

echo ""
echo "copying replacement jar into test instances..."
echo "to: "$DST1/
cp ./build/libs/* $DST1/
echo "to: "$DST2/
cp ./build/libs/* $DST2/
echo "to: "$DST3/
cp ./build/libs/* $DST3/
echo ""
echo "creating release zip..."
cd ./build/libs/
JARNAME=$(ls -1 $JAR)
ZIPNAME=$(basename $JAR .jar)".zip"
zip -9r $ZIPNAME $JARNAME
cd ../../
