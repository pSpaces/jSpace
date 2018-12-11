all: conntest

conntest:
	java -jar ./build/libs/serverconn.jar

debug-conn:
	java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,address=9998 \
		-cp "build/classes/java/main/;%classpath%" \
		-jar ./build/libs/serverconn.jar
#	java -Xdebug -Xrunjdwp:transport=dt_socket,address=:9998,suspend=y \
#		-jar ./build/libs/serverconn.jar

jdb:
	jdb -attach localhost:9998

#		-sourcepath ./src/main/java/org/jspace/ \
#		-classpath ./build/classes/java/main/org/jspace/ \
#		org.jspace.ServerConnectionTester

cert:
	keytool -genkey -keyalg RSA -keysize 2048 -validity 100 -alias mykey -keystore myKeyStore.jks
	keytool -export -alias mykey -keystore myKeyStore.jks -file mykey.cert
	keytool -import -file mykey.cert -alias mykey -keystore myTrustStore.jks
