package com.span.standings.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import com.span.standings.DefaultParameters;
import java.util.Collection;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
    JobStandingsConfiguration.class})
@ActiveProfiles("test")
public class JobStandingsConfigurationTest extends DefaultParameters {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;

  @Test
  public void stepsExecutedSuccessfullyTest() throws Exception {

    JobExecution jobExecution = jobLauncherTestUtils.launchJob(inputFileName());
    Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();

    BatchStatus expectedStatus = BatchStatus.COMPLETED;
    int expectedSize = 3;

    assertThat(actualStepExecutions.size(), is(expectedSize));
    assertEquals(jobExecution.getStatus(), expectedStatus);

  }

  @AfterEach
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }


}