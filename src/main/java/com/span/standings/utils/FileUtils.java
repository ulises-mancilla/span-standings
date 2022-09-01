package com.span.standings.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.span.standings.domain.Match;
import com.span.standings.domain.Standings;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

/***
 * Utility class for reading and writing the file.
 */
@Log4j2
public class FileUtils {

  private String fileName;
  private CSVReader CSVReader;
  private CSVWriter CSVWriter;
  private FileReader fileReader;
  private FileWriter fileWriter;
  private File file;
  private Matcher matcher;
  private final String regex = "(?<teamA>.*) (?<scoreA>\\d+), (?<teamB>.*) (?<scoreB>\\d+)";
  private Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

  public FileUtils(String fileName) {
    this.fileName = fileName;
  }

  public Match readLine() throws Exception {
    if (CSVReader == null) {
      initReader();
    }
    String[] line = CSVReader.readNext();
    if (line == null) {
      return null;
    }
    String match = line[0] + "," + line[1];
    matcher = pattern.matcher(match);
    if (matcher.find()) {
      log.info("Full match: " + matcher.group(0));
      return new Match(matcher.group(1).trim(), Integer.valueOf(matcher.group(2)), matcher.group(3).trim(),
          Integer.valueOf(matcher.group(4)));
    }
    return null;
  }

  public void writeLine(Standings standings) throws Exception {
    if (CSVWriter == null) {
      initWriter();
    }
    String[] lineStr = new String[2];
    String pts = standings.getPoints() == 1 ? " pt" : " pts";
    lineStr[0] = String.valueOf(standings.getRank()) + ". " + standings.getTeam();
    lineStr[1] = " " + String.valueOf(standings.getPoints()) + pts;
    CSVWriter.writeNext(lineStr);
  }

  private void initReader() throws Exception {
    if (file == null) {
      file = new File(fileName);
    }
    if (fileReader == null) {
      fileReader = new FileReader(file);
    }
    if (CSVReader == null) {
      CSVReader = new CSVReader(fileReader);
    }
  }

  private void initWriter() throws Exception {
    if (file == null) {
      file = new File(fileName);
      file.createNewFile();
    }
    if (fileWriter == null) {
      fileWriter = new FileWriter(file, false);
    }
    if (CSVWriter == null) {
      CSVWriter = new CSVWriter(fileWriter, ',', com.opencsv.CSVWriter.NO_QUOTE_CHARACTER);
    }
  }

  public void closeReader() {
    try {
      CSVReader.close();
      fileReader.close();
    } catch (IOException e) {
      log.error("Error while closing reader.");
    }
  }

  public void closeWriter() {
    try {
      CSVWriter.close();
      fileWriter.close();
    } catch (IOException e) {
      log.error("Error while closing writer.");
    }
  }
}
