package com.span.standings;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

/***
 * Definition of job Parameters
 */
public abstract class DefaultParameters {

  private final String INPUT_FILE_NAME = "input.file.name";
  private final String FILE_NAME= "src/test/resources/input.csv";

  public JobParameters inputFileName() {
    return new JobParametersBuilder()
        .addString(INPUT_FILE_NAME, FILE_NAME)
        .toJobParameters();
  }
}
