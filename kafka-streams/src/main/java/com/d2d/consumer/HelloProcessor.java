package com.d2d.consumer;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class HelloProcessor implements Processor<Integer, String> {

    private ProcessorContext context;

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(final Integer dummy, final String line) {
        System.out.println("context --> " + context);
        System.out.println("offset --> " + context.offset());
        System.out.println("partition --> " + context.partition());
    }

    @Override
    public void close() {
    }
};