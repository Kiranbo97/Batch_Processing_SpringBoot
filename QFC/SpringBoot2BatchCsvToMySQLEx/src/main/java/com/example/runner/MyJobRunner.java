package com.example.runner;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyJobRunner implements CommandLineRunner
{
    @Autowired
	private JobLauncher jobLauncher;
    
    @Autowired
    private Job jobA;
	
	@Override
	public void run(String... args) throws Exception 
	{
		JobParameters jobParameter=
				new JobParametersBuilder()
				.addLong("time",System.currentTimeMillis())
				.toJobParameters();
		
		jobLauncher.run(jobA, jobParameter);
		System.out.println("Job execution done");
	   	
	}

}