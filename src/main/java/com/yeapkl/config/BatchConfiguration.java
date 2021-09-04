package com.yeapkl.config;

import com.yeapkl.entity.PaymentDetails;
import com.yeapkl.mapper.PaymentDetailsMapper;
import com.yeapkl.repository.PaymentDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityManagerFactory;

//@Slf4j
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    EntityManagerFactory em;

    @Autowired
    PaymentDetailsRepository paymentDetailsRepository;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Bean
    public FlatFileItemReader<PaymentDetails> reader() {
        FlatFileItemReader<PaymentDetails> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("datasource.txt"));
        reader.setLinesToSkip(1);

        DefaultLineMapper<PaymentDetails> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("|");
        tokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId");

        lineMapper.setFieldSetMapper(new PaymentDetailsMapper());
        lineMapper.setLineTokenizer(tokenizer);
        reader.setLineMapper(lineMapper);

        return reader;
    }


    @Bean
    public JpaItemWriter<PaymentDetails> writer() {
        JpaItemWriter<PaymentDetails> writer = new JpaItemWriter();
        writer.setEntityManagerFactory(em);
        return writer;
    }

    @Bean
    public ItemProcessor<PaymentDetails, PaymentDetails> processor() {
        return (item) -> {
            //item.concatenateName();
            return item;
        };
    }

    @Bean
    public Job importPaymentJob() {
        return jobBuilderFactory.get("importPaymentJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .flow(step1())
//                .end()
//                .build();
                .listener(listener())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<PaymentDetails, PaymentDetails>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListener() {


            @Override
            public void beforeJob(JobExecution jobExecution) {
                /**
                 * As of now empty but can add some before job conditions
                 */
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    log.info("!!! JOB FINISHED! Time to verify the results");
                    paymentDetailsRepository.findAll().
                            forEach(person -> log.info("Found <" + person + "> in the database."));
                }
            }
        };
    }

    @Scheduled(fixedRate = 60000)
    public void run() throws Exception {
        jobLauncher.run(
                importPaymentJob(),
                new JobParametersBuilder().addLong("uniqueness", System.nanoTime()).toJobParameters()
        );
    }

}
