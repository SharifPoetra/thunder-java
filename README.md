## Configuration

Rename `config.txt.example` to `config.txt` and fill it out.

## Format the code

```bash
java -jar ./src/main/resources/formatter.jar --replace -i $(git ls-files|grep \.java$)
```

## Compile

```bash
mvn package
```

## Start it up

```bash
cp config.txt ./target && cd target && java -jar <jar file>
```
