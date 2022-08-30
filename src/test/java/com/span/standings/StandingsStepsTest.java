package com.span.standings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import com.span.standings.config.JobStandingsConfiguration;
import com.span.standings.domain.Match;
import com.span.standings.domain.Standings;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
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
  private final String EXPECTED_OUTPUT = "expected_output.txt";
  private final String ACTUAL_OUTPUT = "output.txt";


  public StepExecution getProcessStepExecution() {
    StepExecution step = MetaDataInstanceFactory.createStepExecution();
    step.getExecutionContext().put(GET_LINES_FROM_CONTEXT, matches);
    return step;
  }

  public StepExecution getWriterStepExecution() {
    StepExecution step = MetaDataInstanceFactory.createStepExecution();
    step.getExecutionContext().put(GET_LINES_FROM_CONTEXT, standings);
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
    givenFile_whenReading_thenSuccess();
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
  public void givenReferenceOutput_whenWrite_thenSuccess() {
    givenMatches_whenProcessed_thenRankCreated();
    JobExecution jobExecution =
        jobLauncherTestUtils.launchStep(WRITE_LINE_STEP,
            getWriterStepExecution().getExecutionContext());

    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
    assertThat(actualStepExecutions.size(), is(1));
    assertThat(actualJobExitStatus.getExitCode(), is(COMPLETED));

    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
    FileSystemResource actualResult = new FileSystemResource(ACTUAL_OUTPUT);
  }




}
