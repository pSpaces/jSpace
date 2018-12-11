PROJECT_PATH=~/git/jSpace/common/
st -e java -jar $PROJECT_PATH/build/libs/chatServer.jar &
sleep 1; st -e bash -c "java -jar $PROJECT_PATH/build/libs/Bob.jar" &
sleep 1; st -e bash -c "java -jar $PROJECT_PATH/build/libs/Alice.jar"
