#!/bin/bash
echo Compiling...
for n in LinuxCharDevMidiProvider/*.java; do javac -g $n; done
echo Building jar...
# put all files into the jar package not only the class files
#jar cf ../LinuxCharDev.jar META-INF/services/javax.sound.midi.spi.MidiDeviceProvider LinuxCharDevMidiProvider/*.class
jar cf ../LinuxCharDev.jar .
echo Done.
