Programmet er en simulation af et økosystem med kaniner, ulve, græs og hegn. 

## Vejledning til at køre applikationen

- hav JDK22 eller nyere version
- klon repositoriet

Kør java projekt gennem IDE eller terminal med nedenstående kommandoer:

### Kør simulation:
**Gennem IDE:**  Kør Main.java 

**Gennem Terminal:** `$ cd /DOA_game_of_life/demo`\
`$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.Main"`


- Indtast startværdier
- tryk “Start Game”

### Kør Benchmarks:

**Gennem IDE:** Kør BenchmarkRunner.java 

**Gennem Terminal:** `$ cd /DOA_game_of_life/demo`\
`$ ./mvnw clean compile exec:java -Dexec.mainClass="com.example.demo.BenchmarkRunner"` 


- Det tager 3 minutter at køre benchmarks
- Resultaterne gemmes i demo/benchmark_results.csv
