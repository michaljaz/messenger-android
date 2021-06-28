#!/bin/bash
echo "====================== GOOGLE HASH ======================"
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android | openssl sha1 -c

echo "====================== FACEBOOK HASH ======================"
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android | openssl sha1 -binary | openssl base64