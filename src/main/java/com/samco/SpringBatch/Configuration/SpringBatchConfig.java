package com.samco.SpringBatch.Configuration;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.samco.SpringBatch.Entity.Employees;
import com.samco.SpringBatch.Repository.EmployeeRepository;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private EmployeeRepository employeeRepository;


    @Bean
    public FlatFileItemReader<Employees> reader() {
        FlatFileItemReader<Employees> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("/home/samcouser/Downloads/SpringBatch/src/main/resources/employees.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    public LineMapper<Employees> lineMapper() {
        DefaultLineMapper<Employees> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","email","name","salary");

        BeanWrapperFieldSetMapper<Employees> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employees.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employees> writer() {
        RepositoryItemWriter<Employees> writer = new RepositoryItemWriter<>();
        writer.setRepository(employeeRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv-step").<Employees, Employees>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("importCustomers")
                .flow(step1()).end().build();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}