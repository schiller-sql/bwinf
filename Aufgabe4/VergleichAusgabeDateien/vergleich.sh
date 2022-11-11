cd ../..

/Users/tonyborchert/Library/Java/JavaVirtualMachines/openjdk-19.0.1/Contents/Home/bin/javac -d Aufgabe4/target Aufgabe4/Quelltext/Main.java;

/Users/tonyborchert/Library/Java/JavaVirtualMachines/openjdk-19.0.1/Contents/Home/bin/java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath Aufgabe4/target Main | diff - Aufgabe4/vergleichausgabedateien/richtige-ausgabe-beides.txt