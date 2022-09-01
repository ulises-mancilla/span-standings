package com.span.standings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import com.span.standings.config.JobStandingsConfiguration;
import com.span.standings.domain.Match;
import com.span.standings.domain.Standings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
    JobStandingsConfiguration.class})
@ActiveProfiles("test")
public class StandingsStepsTest extends DefaultParameters {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  private List<Match> matches;
  private List<Standings> standings;

  private final String READ_LINE_STEP = "readLines";
  private final String PROCESS_LINE_STEP = "processLines";
  private final String WRITE_LINE_STEP = "writeLines";
  private final String COMPLETED = "COMPLETED";
  private final String GET_LINES_FROM_CONTEXT = "lines";
  private final String EXPECTED_OUTPUT = "output.txt";
  private final String ACTUAL_OUTPUT = "src/test/resources/actual_output.txt";


  public StepExecution getProcessStepExecution() {
    StepExecution step = MetaDataInstanceFactory.createStepExecution();
    step.getExecutionContext().put(GET_LINES_FROM_CONTEXT, createListOfMatches());
    return step;
  }

  public StepExecution getWriterStepExecution() {
    StepExecution step = MetaDataInstanceFactory.createStepExecution();
    step.getExecutionContext().put(GET_LINES_FROM_CONTEXT, createStandings());
    return step;
  }

  @Test
  public void givenFile_whenReading_thenSuccess() {

    JobExecution jobExecution =
        jobLauncherTestUtils.launchStep(READ_LINE_STEP, inputFileName());

    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualJobExitStatus.getExitCode(), is(COMPLETED));

    ExecutionContext executionContext = jobExecution.getExecutionContext();
    matches = (List<Match>) executionContext.get(GET_LINES_FROM_CONTEXT);

    assertThat(matches.size(), is(5));
    Match expectedMatch = new Match("Lions", 3, "Snakes", 3);
    assertTrue(matches.contains(expectedMatch));
  }

  @Test
  public void givenMatches_whenProcessed_thenRankCreated() {

    JobExecution jobExecution =
        jobLauncherTestUtils.launchStep(PROCESS_LINE_STEP,
            getProcessStepExecution().getExecutionContext());

    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualJobExitStatus.getExitCode(), is(COMPLETED));

    ExecutionContext executionContext = jobExecution.getExecutionContext();
    standings = (List<Standings>) executionContext.get(GET_LINES_FROM_CONTEXT);
    assertThat(standings.get(0).getTeam(), is("Tarantulas"));
    assertThat(standings.get(0).getPoints(), is(6));
  }

  @Test
  public void givenReferenceOutput_whenWrite_thenSuccess() throws Exception {

    JobExecution jobExecution =
        jobLauncherTestUtils.launchStep(WRITE_LINE_STEP,
            getWriterStepExecution().getExecutionContext());

    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualJobExitStatus.getExitCode(), is(COMPLETED));

    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
    FileSystemResource actualResult = new FileSystemResource(ACTUAL_OUTPUT);

    AssertFile.assertFileEquals(expectedResult, actualResult);
  }

  private List<Match> createListOfMatches() {
    List<Match> matches = new ArrayList<>();
    Match match1 = new Match("Lions", 3, "Snakes", 3);
    Match match2 = new Match("Tarantulas", 1, "FC Awesome", 0);
    Match match3 = new Match("Lions", 1, "FC Awesome", 1);
    Match match4 = new Match("Tarantulas", 3, "Snakes", 1);
    Match match5 = new Match("Lions", 4, "Grouches", 0);
    matches.add(match1);
    matches.add(match2);
    matches.add(match3);
    matches.add(match4);
    matches.add(match5);
    return matches;
  }

  private List<Standings> createStandings() {
    List<Standings> standings = new ArrayList<>();
    Standings rank1 = new Standings(0,"Tarantulas",6);
    Standings rank2 = new Standings(0,"Lions", 5);
    Standings rank3 = new Standings(0,"FC Awesome", 1);
    Standings rank4 = new Standings(0,"Snakes", 1);
    Standings rank5 = new Standings(0,"Grouches", 0);
    standings.add(rank1);
    standings.add(rank2);
    standings.add(rank3);
    standings.add(rank4);
    standings.add(rank5);
    return standings;
  }


}
