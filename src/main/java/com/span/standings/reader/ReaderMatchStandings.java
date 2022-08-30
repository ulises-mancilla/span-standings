package com.span.standings.reader;

import com.span.standings.domain.Match;
import com.span.standings.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Log4j2
public class ReaderMatchStandings implements Tasklet, StepExecutionListener {

  private FileUtils fu;
  private List<Match> matches;
  private String fileName;

  public ReaderMatchStandings(String fileName) {
    this.fileName=fileName;
  }


  @Override
  public void beforeStep(StepExecution stepExecution) {
    matches = new ArrayList<>();
    log.info("FILE NAME: " + fileName);
    fu = new FileUtils(fileName);
    log.info("Lines Reader initialized.");
  }

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
      throws Exception {
    log.info("Reading");
    Match match = fu.readLine();
    while (match != null) {
      matches.add(match);
      log.info("Read line: " + match.toString());
      match = fu.readLine();
    }
    return RepeatStatus.FINISHED;
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    fu.closeReader();
    stepExecution
        .getJobExecution()
        .getExecutionContext()
        .put("lines", this.matches);
    log.debug("Lines Reader ended.");
    return ExitStatus.COMPLETED;
  }
}
