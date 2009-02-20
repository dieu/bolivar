package com.griddynamics.spring.batch.test;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.ParseException;


public class TestItemReader implements ItemReader<Integer> {
    private Integer count = 0;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException {
        count++;
        if(count % 10 == 0) return null;
        return count;
    }
}
