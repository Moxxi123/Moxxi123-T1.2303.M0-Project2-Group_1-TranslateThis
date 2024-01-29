@echo off
java --module-path "..\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -jar TranslateThis.jar
pause