package lecture.spring.batch.job.uc01;

import lecture.spring.batch.util.FileUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Configuration
public class UC01JobConfig {
    private final Logger logger = Logger.getLogger(UC01JobConfig.class.getName());

    @Bean
    public Job uc01Job(JobRepository jobRepository,
                       @Qualifier("uc01Step0") Step step0,
                       @Qualifier("uc01Step1") Step step1) {
        return new JobBuilder("uc01Job", jobRepository)
                // only work in case no provided JobParameters
                .incrementer(new RunIdIncrementer())
                .validator(parameters -> {
                })
                .start(step0)
                .next(step1)
                .build();
    }

    @Bean
    public Step uc01Step0(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          @Qualifier("uc01SetupTasklet0") Tasklet tasklet,
                          @Qualifier("uc01ExecCtxPromoListener") ExecutionContextPromotionListener promotionListener) {
        return new StepBuilder("uc01Step0", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(tasklet, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().put("job.shared", "this is state shared from step 0");
                    }
                })
                .listener(promotionListener)
                .build();
    }

    @Bean
    public Step uc01Step0V2(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager) {
        return new StepBuilder("uc01Step0", jobRepository)
                .allowStartIfComplete(true)
                .<String, String>chunk(1, transactionManager)
                .reader(new ItemReader<>() {
                    private final AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        if (count.getAndIncrement() > 0) {
                            return null;
                        }
                        return "data/spring/batch/uc01/output2.csv";
                    }
                })
                .writer(chunk -> {
                    var fu = new FileUtil();
                    chunk.forEach(line -> {
                        try {
                            fu.createFile(Paths.get(line));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                })
                .build();
    }

    @Bean
    @JobScope
    public Step uc01Step1(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          @Qualifier("uc01Reader2") FlatFileItemReader<String> reader,
                          @Qualifier("uc01Processor1") ItemProcessor<String, String> processor,
                          @Qualifier("uc01Writer1") FlatFileItemWriter<String> writer,
                          @Qualifier("uc01ExecCtxPromoListener") ExecutionContextPromotionListener promotionListener) {
        return new StepBuilder("uc01Step1", jobRepository)
                .allowStartIfComplete(true)
                .<String, String>chunk(2, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().put("step1.shared", "123");
                    }
                })
                .faultTolerant()
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> uc01Reader1(@Value("${uc01.step1.input}") Resource resource,
                                                  @Value("#{jobExecutionContext}") Properties jobExecutionContext,
                                                  @Value("#{stepExecutionContext}") Properties stepExecutionContext,
                                                  @Qualifier("uc01Reader1LineMapper") LineMapper<String> lineMapper) {
        logger.info("uc01Reader1 jobExecutionContext: " + jobExecutionContext);
        logger.info("uc01Reader1 stepExecutionContext: " + stepExecutionContext);
        return new FlatFileItemReaderBuilder<String>()
                .name("uc01Reader1")
                .resource(resource)
                .linesToSkip(1)
//                .lineTokenizer(new DelimitedLineTokenizer("|"))
//                .fieldSetMapper(fs -> fs.readString(0) + " has name " + fs.readString(1))
                // Demo fault tolerance
//                .lineMapper((line, lineNumber) -> {
//                    if (line.startsWith("8"))
//                        throw new RuntimeException("Exception in uc01Reader1");
//                    return line;
//                })
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> uc01Reader2(@Value("file:data/spring/batch/uc01/input2.csv") Resource resource) {
        var lm = new DefaultLineMapper<String>();
        var lt = new FixedLengthTokenizer();
        lt.setColumns(new Range(1, 2), new Range(3, 22));
        lm.setLineTokenizer(lt);
        lm.setFieldSetMapper(fs -> {
            logger.info("uc01Reader2 fs.readString: " + fs);
            return fs.readString(0) + " has name " + fs.readString(1);
        });
        return new FlatFileItemReaderBuilder<String>()
                .name("uc01Reader2")
                .resource(resource)
                .lineMapper(lm)
                .build();
    }

    @Bean
    LineMapper<String> uc01Reader1LineMapper() {
        var lm = new DefaultLineMapper<String>();
        lm.setLineTokenizer(new DelimitedLineTokenizer("|"));
        lm.setFieldSetMapper(fs -> {
            logger.info("uc01Reader1 fs.readString: " + fs);
            return fs.readString(0) + " has name " + fs.readString(1);
        });
        return lm;
    }

    @Bean
    @StepScope
    public ItemProcessor<String, String> uc01Processor1() {
        // Demo fault tolerance
        return item -> {
            if (item.startsWith("8"))
                throw new RuntimeException();
            return item + " is processed";
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> uc01Writer1(@Value("#{jobParameters['uc01.step1.output']}") WritableResource resource,
                                                  @Value("#{jobExecutionContext}") Properties jobExecutionContext,
                                                  @Value("#{stepExecutionContext}") Properties stepExecutionContext) {
        logger.info("uc01Writer1 jobExecutionContext: " + jobExecutionContext);
        logger.info("uc01Writer1 stepExecutionContext: " + stepExecutionContext);
        return new FlatFileItemWriterBuilder<String>()
                .name("uc01Writer1")
                .resource(resource)
                .lineAggregator(item -> item)
                // Demo fault tolerance
                .lineAggregator(item -> {
                    if (item.startsWith("8"))
                        throw new RuntimeException("Exception in uc01Writer1");
                    return item;
                })
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener uc01ExecCtxPromoListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"job.shared"});
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
