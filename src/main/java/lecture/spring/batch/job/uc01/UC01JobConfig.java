package lecture.spring.batch.job.uc01;

import lecture.spring.batch.util.FileUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

@Configuration
public class UC01JobConfig {
    private final Logger logger = Logger.getLogger(UC01JobConfig.class.getName());

    @Bean
    public Job uc01Job(JobRepository jobRepository,
                       @Qualifier("uc01Step0") Step step0,
                       @Qualifier("uc01Step1") Step step1) {
        return new JobBuilder("uc01Job", jobRepository)
                .validator(parameters -> {
                })
                .start(step0)
                .next(step1)
                .build();
    }

    @Bean
    public Step uc01Step0(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          @Qualifier("uc01SetupTasklet0") Tasklet tasklet) {
        return new StepBuilder("uc01Step0", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step uc01Step1(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          @Qualifier("uc01Reader1") FlatFileItemReader<String> reader,
                          @Qualifier("uc01Writer1") FlatFileItemWriter<String> writer,
                          ExecutionContextPromotionListener promotionListener) {
        return new StepBuilder("uc01Step1", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .listener(promotionListener)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                        stepExecution.getExecutionContext().put("uc01Step1", "123");
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> uc01Reader1(@Value("${uc01.step1.input}") Resource resource,
                                                  @Value("#{jobExecutionContext}") Properties jobExecutionContext,
                                                  @Value("#{stepExecutionContext}") Properties stepExecutionContext) {
        logger.info("jobExecutionContext: " + jobExecutionContext);
        logger.info("stepExecutionContext: " + stepExecutionContext);
        stepExecutionContext.put("step1State", "abc");
        return new FlatFileItemReaderBuilder<String>()
                .name("uc01Reader1")
                .resource(resource)
                .linesToSkip(1)
                .lineTokenizer(new DelimitedLineTokenizer("|"))
                .fieldSetMapper(fs -> fs.readString(0) + " has name " + fs.readString(1))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> uc01Writer1(@Value("#{jobParameters['uc01.step1.output']}") WritableResource resource,
                                                  @Value("#{jobExecutionContext}") Properties jobExecutionContext,
                                                  @Value("#{stepExecutionContext}") Properties stepExecutionContext) {
        logger.info("jobExecutionContext: " + jobExecutionContext);
        logger.info("stepExecutionContext: " + stepExecutionContext);
        return new FlatFileItemWriterBuilder<String>()
                .name("uc01Writer1")
                .resource(resource)
                .lineAggregator(item -> item)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"jobState"});
        return listener;
    }

    @Bean
    public MethodInvokingTaskletAdapter uc01SetupTasklet0() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(new FileUtil());
        adapter.setTargetMethod("createFile");
        adapter.setArguments(new Object[]{Paths.get("data/spring/batch/uc01/output2.csv")});
        return adapter;
    }
}
