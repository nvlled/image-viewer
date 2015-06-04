SRC_DIR=src
BUILD_DIR=build
ENTRY_POINT=nvlled.imageviewer.Main
JAR_NAME=image-viewer.jar

default: compile

jar: compile
	jar cfe ${JAR_NAME} ${ENTRY_POINT} -C ${BUILD_DIR} nvlled

run: compile
	java -cp $(BUILD_DIR) ${ENTRY_POINT}

compile: prepare-dirs $(find -iname "*.java")
	javac -d $(BUILD_DIR) `find -iname "*.java"`

prepare-dirs:
	mkdir -p $(BUILD_DIR)

clean:
	rm -v -rf $(BUILD_DIR)
	rm -f ${JAR_NAME}
