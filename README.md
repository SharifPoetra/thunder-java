## Compile
```bash
mvn process-resources compile assembly:single
```
## Start it up
```bash
cp config.txt ./target && cd target && java -jar thunder-1.0-jar-with-dependencies.jar
```