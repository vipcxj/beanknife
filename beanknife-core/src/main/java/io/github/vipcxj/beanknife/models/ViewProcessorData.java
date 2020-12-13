package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class ViewProcessorData {
    private final List<String> errors;
    private final Map<String, ViewOfData> viewOfDataByGenTypeName;
    private final Map<String, List<ViewOfData>> viewOfDataByTargetTypeName;
    private final Map<String, List<ViewOfData>> viewOfDataByConfigTypeName;

    private ViewProcessorData() {
        this.errors = new ArrayList<>();
        this.viewOfDataByGenTypeName = new HashMap<>();
        this.viewOfDataByTargetTypeName = new HashMap<>();
        this.viewOfDataByConfigTypeName = new HashMap<>();
    }

    @Nonnull
    public List<String> getErrors() {
        return errors;
    }

    @Nonnull
    public List<ViewOfData> getByTargetElement(@Nonnull TypeElement target) {
        List<ViewOfData> viewOfDataList = viewOfDataByTargetTypeName.get(target.getQualifiedName().toString());
        return viewOfDataList != null ? viewOfDataList : Collections.emptyList();
    }

    @Nonnull
    public List<ViewOfData> getByConfigElement(@Nonnull TypeElement config) {
        List<ViewOfData> viewOfDataList = viewOfDataByConfigTypeName.get(config.getQualifiedName().toString());
        return viewOfDataList != null ? viewOfDataList : Collections.emptyList();
    }

    public static ViewProcessorData collect(@Nonnull ProcessingEnvironment environment, @Nonnull RoundEnvironment roundEnvironment) {
        ViewProcessorData viewProcessorData = new ViewProcessorData();
        Map<String, ViewOfData> viewOfDataMap = viewProcessorData.viewOfDataByGenTypeName;
        List<ViewOfData> viewOfDataList = Utils.collectViewOfs(environment, roundEnvironment);
        for (ViewOfData viewOfData : viewOfDataList) {
            Type genType = Utils.extractGenType(
                    Type.extract(viewOfData.getTargetElement().asType()),
                    viewOfData.getGenName(),
                    viewOfData.getGenPackage(),
                    "View"
            );
            String genTypeName = genType.getQualifiedName();
            ViewOfData existed = viewOfDataMap.get(genTypeName);
            if (existed == null) {
                viewOfDataMap.put(genTypeName, viewOfData);
            } else {
                viewProcessorData.errors.add("The view class \"" +
                        genTypeName +
                        "\" which configured by \"" +
                        viewOfData.getConfigElement().getQualifiedName() +
                        "\" will not be generated, " +
                        "because the class \"" +
                        existed.getConfigElement().getQualifiedName() +
                        "\" has configured a view class with same name and has a higher priority.");
            }
        }
        Map<String, List<ViewOfData>> viewOfDataByTargetTypeName = viewProcessorData.viewOfDataByTargetTypeName;
        Map<String, List<ViewOfData>> viewOfDataByConfigTypeName = viewProcessorData.viewOfDataByConfigTypeName;
        for (ViewOfData viewOfData : viewOfDataMap.values()) {
            String targetName = viewOfData.getTargetElement().getQualifiedName().toString();
            String configName = viewOfData.getConfigElement().getQualifiedName().toString();
            List<ViewOfData> dataByTargetList = viewOfDataByTargetTypeName.computeIfAbsent(targetName, k -> new ArrayList<>());
            dataByTargetList.add(viewOfData);
            List<ViewOfData> dataByConfigList = viewOfDataByConfigTypeName.computeIfAbsent(configName, k -> new ArrayList<>());
            dataByConfigList.add(viewOfData);
        }
        return viewProcessorData;
    }
}
