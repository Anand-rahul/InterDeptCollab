package com.sharktank.interdepcollab.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiThreading {
    public final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Bean
    public void executeAsync(Runnable Task){
        executor.submit(Task);
    }
}
