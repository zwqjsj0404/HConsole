home=`dirname "$0"`
home=`cd "$home"; pwd`

if [ ! -e ~/".jline.rc" ]; then
  cp "$home/.jline.rc" ~/
fi

CONF_DIR="$home/conf"
BUILD_CLASSES="$home/bin"

# add conf
CLASSPATH="${CONF_DIR}"

# add classes
CLASSPATH=${CLASSPATH}:$BUILD_CLASSES

#add libs
for f in $home/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
# CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar

exec java -cp "$CLASSPATH" org.ccnt.hadoop.HadoopConsole