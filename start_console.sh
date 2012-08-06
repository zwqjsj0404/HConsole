home=`dirname "$0"`
home=`cd "$bin"; pwd`

CONF_DIR="$home/conf"
BUILD_CLASSES="$home/bin"

# add conf
CLASSPATH="${CONF_DIR}"

# add classes
CLASSPATH=${CLASSPATH}:$BUILD_CLASSES

CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar

#add libs
for f in $home/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done


exec java -cp "$CLASSPATH" org.ccnt.hadoop.HadoopConsole
