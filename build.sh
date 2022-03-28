#   Build the mod for 1.8.8
#     ./build.sh 1.8.8
#   
#   Build the mod for 1.8.9
#     ./build.sh 1.8.9
#
############################################################################

#TODO This is not good way to change jdk version
export JAVA_HOME="D:\Applications\java\jdk-8"

initial_branch=`git branch --show-current`

case $1 in
  "" | "1.8.8" ) [ $initial_branch != "master" ] && git checkout master && version="1.8.8" ;;
  "1.8.9" )      [ $initial_branch != "1.8.9" ]  && git checkout 1.8.9  && version="1.8.9" ;;
esac

./gradlew build && {
  cp -i `ls -t ./build/libs/ExtendTheLow-*[0-9].jar | head -n 1` "$APPDATA"/.thelow/mods/
}
if [ `git branch --show-current` != initial_branch ]; then
  git checkout $initial_branch
fi
