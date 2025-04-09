package lecture.spring.batch.job.uc01;

import lecture.spring.batch.util.FileUtil;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component("uc01SetupTasklet1")
public class UC01SetupTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        new FileUtil().createFile(Paths.get("data/spring/batch/uc01/output2.csv"));
        return RepeatStatus.FINISHED;
    }
}
