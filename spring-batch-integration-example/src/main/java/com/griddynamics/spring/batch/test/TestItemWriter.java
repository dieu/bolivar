package com.griddynamics.spring.batch.test;

import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.ArrayList;


public class TestItemWriter implements ItemWriter<Integer> {

    private List<Integer> numbers;


    public TestItemWriter() {
        numbers = new ArrayList<Integer>();
    }

    public TestItemWriter(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public void write(List<? extends Integer> integers) throws Exception {
        System.out.println("Get following numbers" + integers);
        numbers.addAll(integers);
    }


    public List<Integer> getNumbers() {
        return numbers;
    }
}
