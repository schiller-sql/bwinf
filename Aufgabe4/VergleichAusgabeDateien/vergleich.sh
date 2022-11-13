cd ../..

javac -d Aufgabe4/target Aufgabe4/Quelltext/Main.java;

java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath Aufgabe4/target Main | diff - Aufgabe4/vergleichausgabedateien/richtige-ausgabe-beides.txt