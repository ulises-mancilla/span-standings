package com.span.standings.procesor;

import com.span.standings.domain.Match;
import com.span.standings.domain.Standings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ProcessorMatchStandings implements Tasklet, StepExecutionListener {

  private List<Match> matches;
  private Map<String, Integer> mapStandings;
  private List<Standings> standings;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution
        .getJobExecution()
        .getExecutionContext();
    this.matches = (List<Match>) executionContext.get("lines");
    this.mapStandings = new HashMap<>();
    this.standings = new ArrayList<>();
    for (Match m : matches) {
      mapStandings.put(m.getTeamA(), 0);
      mapStandings.put(m.getTeamB(), 0);
    }

    log.info("Lines Processor initialized.");
  }

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
      throws Exception {
    for (Match match : matches) {
      if (match.getScoreA() > match.getScoreB()) {
        mapStandings.put(match.getTeamA(), mapStandings.get(match.getTeamA()) + 3);
      } else if (match.getScoreB() > match.getScoreA()) {
        mapStandings.put(match.getTeamB(), mapStandings.get(match.getTeamB()) + 3);
      } else {
        mapStandings.put(match.getTeamA(), mapStandings.get(match.getTeamA()) + 1);
        mapStandings.put(match.getTeamB(), mapStandings.get(match.getTeamB()) + 1);
      }
    }
    sortStandings();
    standings.forEach(s -> log.info(s.toString()));
    return RepeatStatus.FINISHED;
  }


  private void sortStandings() {
    for (Map.Entry<String,Integer> entry: mapStandings.entrySet()) {
      Standings standing = new Standings();
      standing.setTeam(entry.getKey());
      standing.setPoints(entry.getValue());
      standings.add(standing);
    }
    Collections.sort(standings);
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    stepExecution
        .getJobExecution()
        .getExecutionContext()
        .put("lines", this.standings);
    log.info("Lines Processor ended.");
    return ExitStatus.COMPLETED;
  }
}
