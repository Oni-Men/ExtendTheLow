#   
#	Build the MOD and copy file to MOD directory.
#
############################################################################

#TODO This is not good way to change jdk version
export JAVA_HOME="D:\Applications\java\jdk-8"

rm ./build/libs/ExtendTheLow-*.jar

./gradlew build && {
  cp -i `ls -t ./build/libs/ExtendTheLow-*[0-9].jar | head -n 1` "$APPDATA"/.thelow/mods/
}
