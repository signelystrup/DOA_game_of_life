Programmet er en simulation af et økosystem med kaniner, ulve, græs og hegn. 



\## Vejledning til at køre applikationen



\- hav JDK22 eller nyere version

\- klon repositoriet



Kør java projekt gennem IDE eller terminal med nedenstående kommandoer:



\### Kør projekt gennem IDE

\*\*Applikation:\*\*   Kør Main.java → Indtast startværdier og tryk “Start Game”





\*\*Benchmarks:\*\* Kør BenchmarkRunner.java (Tager 3 minutter, gemmes i demo/benchmark\_results.csv)



\### Kør projekt gennem terminal

`$ cd /DOA\_game\_of\_life/demo`



\*\*Applikation:\*\* `$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.Main"` → Indtast startværdier og tryk “Start Game”





\*\*Benchmarks:\*\* `$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.BenchmarkRunner"` 

(Tager 3 minutter, gemmes i demo/benchmark\_results.csv)





—-----



Programmet er en simulation af et økosystem med kaniner, ulve, græs og hegn. 



\## Vejledning til at køre applikationen



\- hav JDK22 eller nyere version

\- klon repositoriet



Kør java projekt gennem IDE eller terminal med nedenstående kommandoer:



\### Kør simulation:

\*\*Gennem IDE:\*\*  Kør Main.java 



\*\*Gennem Terminal:\*\* `$ cd /DOA\_game\_of\_life/demo`\\

`$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.Main"`





\- Indtast startværdier

\- tryk “Start Game”



\### Kør Benchmarks:



\*\*Gennem IDE:\*\* Kør BenchmarkRunner.java 



\*\*Gennem Terminal:\*\* `$ cd /DOA\_game\_of\_life/demo`\\

`$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.BenchmarkRunner"` 





\- Det tager 3 minutter at køre benchmarks

\- Resultaterne gemmes i demo/benchmark\_results.csv





