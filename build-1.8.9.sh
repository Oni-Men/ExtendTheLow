git checkout 1.8.9

./gradlew build
cp -f build/libs/ExtendTheLow-[0-9].[0-9].[0-9].jar "$APPDATA"/.thelow/mods/

git checkout master