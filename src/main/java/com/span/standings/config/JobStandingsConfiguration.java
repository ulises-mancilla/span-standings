package com.span.standings.config;

import com.span.standings.procesor.ProcessorMatchStandings;
import com.span.standings.reader.ReaderMatchStandings;
import com.span.standings.writer.WriterMatchStandings;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobStandingsConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory job;

    public JobStandingsConfiguration(
        StepBuilderFactory steps,
        JobBuilderFactory job) {
        this.stepBuilderFactory = steps;
        this.job = job;
    }

    @StepScope
    @Bean
    public ReaderMatchStandings readerMatchStandings(@Value("#{jobParameters['input.file.name']}") String fileName) {
        return new ReaderMatchStandings(fileName);
    }

    @Bean
    public ProcessorMatchStandings processorMatchStandings() {
        return new ProcessorMatchStandings();
    }

    @Bean
    public WriterMatchStandings writerMatchStandings() {
        return new WriterMatchStandings();
    }

    @Bean
    protected Step readLines() {
        return stepBuilderFactory
            .get("readLines")
            .tasklet(readerMatchStandings(""))
            .build();
    }

    @Bean
    protected Step processLines() {
        return stepBuilderFactory
            .get("processLines")
            .tasklet(processorMatchStandings())
            .build();
    }

    @Bean
    protected Step writeLines() {
        return stepBuilderFactory
            .get("writeLines")
            .tasklet(writerMatchStandings())
            .build();
    }

    @Bean
    public Job job() {
        return job
            .get("standingsJob")
            .start(readLines())
            .next(processLines())
            .next(writeLines())
            .build();
    }
}
