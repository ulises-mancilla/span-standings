# Ranking Project by Span

Given an input file with teams and scores, an output txt file with Rankings is generated.

Stack:
* Spring Boot 2.7.3
  * Spring Batch

## Build Project

Requirements
* Amazon Coretto 11 jdk, you can download the corresponding version from [here](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html).

Maven wrapper has been included. So move to your folder project and run in order to generate the .jar file.

```bash
./mvnw clean install
```

## Usage

From your project folder run :
```bash
java -jar target/standings-1.0.0.jar input.file.name=input.csv
```
Example of input file:

```bash
Lions 3, Snakes 3
Tarantulas FC 1, FC Awesome 0
Lions 1, FC Awesome  1
Tarantulas FC 3, Snakes 1
Lions 4, Grouches 0
```

The name of the resulting file output.csv should be placed into the folder project.
## Test
You can run tests using the maven wrapper as follow:

```bash
./mvnw test
```



