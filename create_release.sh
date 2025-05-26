#!/bin/bash

# Start the timer
start_time=$(date +%s)

# Navigate to the app directory
cd app

# Run the gradle commands in parallel
../gradlew bundleIdroLifeRelease
../gradlew bundleIdroProRelease
../gradlew bundleIdroResRelease
../gradlew bundleIrriLifeRelease
../gradlew assembleIdroLifeRelease
../gradlew assembleIdroProRelease
../gradlew assembleIdroResRelease
../gradlew assembleIrriLifeRelease

# Navigate back to the parent directory
cd ..

# Get the current date and time
current_date=$(date +"%d_%b_%Y")

# Add the output files to the zip archive
zip -j "idrobit_$current_date.zip" \
app/build/outputs/bundle/idroLifeRelease/app-idroLife-release.aab \
app/build/outputs/bundle/idroProRelease/app-idroPro-release.aab \
app/build/outputs/bundle/idroResRelease/app-idroRes-release.aab \
app/build/outputs/bundle/irriLifeRelease/app-irriLife-release.aab \
app/build/outputs/apk/idroLife/release/app-idroLife-release.apk \
app/build/outputs/apk/idroPro/release/app-idroPro-release.apk \
app/build/outputs/apk/idroRes/release/app-idroRes-release.apk \
app/build/outputs/apk/irriLife/release/app-irriLife-release.apk

# End the timer and display the elapsed time
end_time=$(date +%s)
elapsed_time=$(expr $end_time - $start_time)
echo "Total time taken: $elapsed_time seconds"
