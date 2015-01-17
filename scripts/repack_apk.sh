#!/bin/bash
# if not root, run as root

if (( $EUID != 0 )); then
    echo "Run as ROOT!"
    exit
fi

if [ -d "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter" ]
then
    echo "Directory exists."
    rm -rR "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter"
else
    echo "Run Forest Run!!"
fi

apktool d "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter.apk"

echo "Copy Services!"
cp -avr "/home/piiiters/projects/az-mov/src/apk_installer/apktool/META-INF" "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter/original/META-INF"

apktool b -cd "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter"

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore azmob.keystore -keypass azmobmeter -storepass azmobmeter "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter/dist/AZmobMeter.apk" azmob

/home/piiiters/android_toolchain/android-sdk-linux/build-tools/21.0.1/zipalign -v 4 "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter/dist/AZmobMeter.apk" "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter/dist/AZmobMeter_Aligned.apk"

adb uninstall "com.thinken.azmobmeter"

adb install "/home/piiiters/projects/az-mov/src/apk_installer/apktool/AZmobMeter/dist/AZmobMeter_Aligned.apk"
