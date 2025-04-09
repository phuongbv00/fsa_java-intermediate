package lecture.spring.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job")
public class JobController {
    private final JobLauncher jobLauncher;

    @Qualifier("uc01Job")
    private final Job uc01Job;

    @Value("${uc01.step1.output}")
    private String uc01Step1Output;

    public JobController(JobLauncher jobLauncher, Job uc01Job) {
        this.jobLauncher = jobLauncher;
        this.uc01Job = uc01Job;
    }

    @GetMapping("uc01")
    public String uc01() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(uc01Job, new JobParametersBuilder()
                .addString("uc01.step1.output", uc01Step1Output)
                .toJobParameters());
        return "ok";
    }
}
