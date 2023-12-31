package com.example.config;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.StepBuilderException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.entity.Product;
import com.example.demo.processor.ProductProcessor;
import com.example.listener.MyJobListener;

@Configuration
@EnableBatchProcessing
public class BatchConfig 
{
     //a. Reader class object
	
	@Bean
	public FlatFileItemReader<Product> reader()
	{
		FlatFileItemReader<Product> reader=new FlatFileItemReader<Product>();
		reader.setResource(new ClassPathResource("products.csv")); //src/main/resource
		//reader.setResource(new FileSystemResource("C:\Users\bokir\Documents\QFC\SpringBoot2BatchCsvToMySQLEx\src\main\resources"))
        //reader.setResource(new ClassPathResource("http://abcd.com/files/product.csv")); // url
		
		reader.setLineMapper(new DefaultLineMapper<>() {{
			setLineTokenizer(new DelimitedLineTokenizer(){{
				setDelimiter(DELIMITER_COMMA);
				setNames("prodId","prodCode","prodCost");
			}});
			
			setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
				setTargetType(Product.class);
			}});
		}});
		return reader;
	}
	
	
	//b. Processor class object 
	
	@Bean
	public ItemProcessor<Product, Product> processor()
	
	{
		return new ProductProcessor();
		
//		return item-> {
//			double cost= item.getProdCost();
//		       item.setProdDisc(cost*12/100.0);
//		       item.setProdGst(cost*22/100.0);
//		       
//		       return item;
//		}
	}
	
	//c. Writer class Object
	@Autowired
	 private  DataSource dataSource;
	
	@Bean
	public JdbcBatchItemWriter<Product> writer()
	{
		JdbcBatchItemWriter<Product> writer=new JdbcBatchItemWriter<Product>();
		writer.setDataSource(dataSource);
		writer.setSql("insert into products(PID,PCODE,PCOST,PDISC,PGST) values(:prodId,:prodCode,:prodCost,:prodDisc,:prodGst)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		return writer;
	}
	
	//d. listener class object
	@Bean
	public JobExecutionListener listener() 
	{
		return new MyJobListener();
		/*
		return new JobExecutionListener()
			{
			   @Override
			   public void afterJob(JobExecution jobExecution)
			   {
				  System.out.println("End date and Time :"+new Date());
				  System.out.println("Status at Ending"+jobExecution.getExitStatus());
				 
				
			   }
			   
			   @Override
				public void beforeJob(JobExecution jobExecution) 
			   {
				    	System.out.println("Started Date and Time :"+new Date());
				    	System.out.println("Status at Starting"+jobExecution.getStatus());
			   }
			   
			};	   */
	}
	
	// e. autowire step builder factory
	
	@Autowired
	private StepBuilderFactory sf;
	
	
	// f. Step object
	
	public Step stepA() 
	{
		return sf.get("stepA")  // step name
				.<Product,Product>chunk(3) // <I,O>chunk
				.reader(reader())    // Reader Obj
				.processor(processor())  // processor obj
				.writer(writer())       // write obj
				.build();  
	}
	
	
	// g. autowire job bilder factory
	
	@Autowired
	private JobBuilderFactory jf;
	
	// h. job object
	@Bean
	public Job jobA() 
	{
		return jf.get("jobA")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(stepA())
				//.next(stepB());
				// .next(stepC());
				.build();
	}
	
	
	
}
