package io.github.vipcxj.beanknife.utils;

import io.github.vipcxj.beanknife.models.ViewProcessorData;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import java.util.WeakHashMap;

public class ProcessorManager {
    private final ProcessingEnvironment environment;
    private final WeakHashMap<RoundEnvironment, ViewProcessorData> viewProcessorDataMap;

    public ProcessorManager(ProcessingEnvironment environment) {
        this.environment = environment;
        this.viewProcessorDataMap = new WeakHashMap<>();
    }

    public ViewProcessorData getData(RoundEnvironment roundEnvironment) {
        ViewProcessorData viewProcessorData = viewProcessorDataMap.get(roundEnvironment);
        if (viewProcessorData == null) {
            viewProcessorData = ViewProcessorData.collect(environment, roundEnvironment);
            viewProcessorDataMap.put(roundEnvironment, viewProcessorData);
        }
        return viewProcessorData;
    }
}
