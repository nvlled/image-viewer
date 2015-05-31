SRC_DIR=src
BUILD_DIR=build

default: all

all: compile

run: compile
	java -cp $(BUILD_DIR) nvlled.imageviewer.Main

compile: prepare-dirs $(find -iname "*.java")
	javac -d $(BUILD_DIR) `find -iname "*.java"`

prepare-dirs:
	mkdir -p $(BUILD_DIR)

clean:
	rm -v -rf $(BUILD_DIR)
