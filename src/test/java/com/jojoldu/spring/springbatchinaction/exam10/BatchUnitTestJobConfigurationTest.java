package com.jojoldu.spring.springbatchinaction.exam10;

import com.jojoldu.spring.springbatchinaction.TestBatchConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.time.LocalDate;

import static com.jojoldu.spring.springbatchinaction.exam10.BatchUnitTestConfiguration.FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jojoldu@gmail.com on 06/10/2019
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

@RunWith(SpringRunner.class)
@SpringBatchTest
@DataJpaTest
@ContextConfiguration(classes={BatchUnitTestConfiguration.class, TestBatchConfig.class})
public class BatchUnitTestJobConfigurationTest {



    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JpaPagingItemReader<SalesSum> batchUnitTestJobReader;

    @Autowired
    private SalesRepository salesRepository;

    private static final LocalDate ORDER_DATE = LocalDate.of(2019,10,6);

    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("orderDate", ORDER_DATE.format(FORMATTER))
                .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    @Test
    public void 기간내_Sales가_집계되어_SalesSum이된다() throws Exception {
        //given
        int amount1 = 1000;
        int amount2 = 500;
        int amount3 = 100;

        salesRepository.save(new Sales(ORDER_DATE, amount1, "1"));
        salesRepository.save(new Sales(ORDER_DATE, amount2, "2"));
        salesRepository.save(new Sales(ORDER_DATE, amount3, "3"));

        //when
        batchUnitTestJobReader.open(new ExecutionContext());

        //then
        SalesSum read1 = batchUnitTestJobReader.read();
        assertThat(read1.getAmountSum()).isEqualTo(amount1+amount2+amount3);
    }


}