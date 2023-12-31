package com.example.listener;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobListener implements JobExecutionListener
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
}
