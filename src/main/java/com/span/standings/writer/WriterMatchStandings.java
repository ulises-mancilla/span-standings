package com.span.standings.writer;

import com.span.standings.domain.Standings;
import com.span.standings.utils.FileUtils;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

@Log4j2
public class WriterMatchStandings implements Tasklet, StepExecutionListener {

  private List<Standings> standings;
  private FileUtils fu;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution
        .getJobExecution()
        .getExecutionContext();
    this.standings = (List<Standings>) executionContext.get("lines");
    fu = new FileUtils("output.txt");
    log.info("Lines Writer initialized.");
  }

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
      throws Exception {
    int rank = 0, index = 0, lastPoints = -1;
    for (Standings standing : standings) {
      index++;
      if (standing.getPoints() != lastPoints) {
        if (index > rank) {
          rank = index;
        } else {
          rank++;
        }
      }
      standing.setRank(rank);
      fu.writeLine(standing);
      log.info("Wrote line " + standing.toString());
      lastPoints = standing.getPoints();
    }
    return RepeatStatus.FINISHED;
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    fu.closeWriter();
    log.debug("Lines Writer ended.");
    return ExitStatus.COMPLETED;
  }
}
