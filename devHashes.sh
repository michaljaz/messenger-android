#!/bin/bash

echo "====================== FACEBOOK HASH ======================"
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android | openssl sha1 -binary | openssl base64

echo "====================== GOOGLE HASH ======================"
keytool -keystore ~/.android/debug.keystore -list -v -storepass android

