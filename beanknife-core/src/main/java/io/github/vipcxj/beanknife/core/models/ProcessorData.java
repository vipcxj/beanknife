package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class ProcessorData {
    private final List<String> errors;
    private final Map<String, ViewOfData> viewOfDataByGenTypeName;
    private final Map<String, List<ViewOfData>> viewOfDataByTargetTypeName;
    private final Map<String, List<ViewOfData>> viewOfDataByConfigTypeName;

    public ProcessorData() {
        this.errors = new ArrayList<>();
        this.viewOfDataByGenTypeName = new HashMap<>();
        this.viewOfDataByTargetTypeName = new HashMap<>();
        this.viewOfDataByConfigTypeName = new HashMap<>();
    }

    @NonNull
    public List<String> getErrors() {
        return errors;
    }

    @NonNull
    public List<ViewOfData> getByTargetElement(@NonNull TypeElement target) {
        List<ViewOfData> viewOfDataList = viewOfDataByTargetTypeName.get(target.getQualifiedName().toString());
        return viewOfDataList != null ? viewOfDataList : Collections.emptyList();
    }

    @NonNull
    public List<ViewOfData> getByConfigElement(@NonNull TypeElement config) {
        List<ViewOfData> viewOfDataList = viewOfDataByConfigTypeName.get(config.getQualifiedName().toString());
        return viewOfDataList != null ? viewOfDataList : Collections.emptyList();
    }

    private String tryCompleteTypeName(Set<String> imports, String type) {
        // type全限定名，并且已经被导入，即无意义的导入
        for (String anImport : imports) {
            if (type.startsWith(anImport)) {
                if (type.equals(anImport) || type.charAt(anImport.length()) == '.') {
                    return type;
                }
            }
        }
        // type简单名，并且未被导入
        int index = type.indexOf('.');
        String imported = index != -1 ? type.substring(0, index) : type;
        for (String anImport : imports) {
            // unnamed包的类永远不可能被显示导入，所以import必定包含'.'.
            if (anImport.endsWith(imported) && anImport.charAt(anImport.length() - imported.length() - 1) == '.') {
                return index != -1 ? anImport + type.substring(index) : anImport;
            }
        }
        // 原样返回，此时type可能是
        // 1. unnamed包中的（前提是当前package也是unnamed，因为只有unnamed包中的对象才能引用同是unnamed包中的对象）
        // 2. 完整的全限定名，未被导入
        // 3. 简单名，未被导入
        // 4. 简单名，被star import了
        // 如果是1,2种情况，必须与genTypeName完全相同，就能确定是View的全限定名，第3种情况需要额外确认，并且此时绝对不会与任一Gen。
        return type;
    }

    /**
     * 从所有可能的View生成名中检索type可能的全限定名。因为View必定不是嵌套类，所以不需要考虑type是嵌套类的可能性
     * @param imports 所有import语句
     * @param packageName 当前包名
     * @param type 需要修复的不可识别类型
     * @return 所有可能的View生成名中匹配type的全限定名或null
     */
    @CheckForNull
    public String fixType(@NonNull Set<String> imports, @NonNull String packageName, @NonNull String type) {
        type = tryCompleteTypeName(imports, type);
        for (String genTypeName : viewOfDataByGenTypeName.keySet()) {
            if (genTypeName.endsWith(type)) {
                if (genTypeName.equals(type)) {
                    if (packageName.isEmpty() || genTypeName.indexOf('.') != -1) {
                        return type;
                    } else {
                        return null;
                    }
                } else {
                    // 同一个包对象可见性优先级高于star import的对象，所以先判断同包的情况
                    // 假设type属于同一包内，加上包名再判断下
                    // 因为若当前包围unnamed，type如果处于同一包，即也为unnamed，此时必须与genTypeName完全相同，但这已经不可能了，所以pass
                    if (!packageName.isEmpty()) {
                        if (genTypeName.equals(packageName + "." + type)) {
                            return genTypeName;
                        }
                    }
                    for (String anImport : imports) {
                        if (anImport.endsWith(".*")) {
                            if (genTypeName.equals(anImport.substring(0, anImport.length() - 1) + type)) {
                                return genTypeName;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void collect(@NonNull ProcessingEnvironment environment, @NonNull RoundEnvironment roundEnvironment) {
        Map<String, ViewOfData> viewOfDataMap = this.viewOfDataByGenTypeName;
        List<ViewOfData> viewOfDataList = Utils.collectViewOfs(environment, roundEnvironment);
        for (ViewOfData viewOfData : viewOfDataList) {
            String genTypeName = Utils.extractGenTypeName(
                    viewOfData.getTargetElement(),
                    viewOfData.getGenName(),
                    viewOfData.getGenPackage(),
                    "View"
            );
            ViewOfData existed = viewOfDataMap.get(genTypeName);
            if (existed == null) {
                viewOfDataMap.put(genTypeName, viewOfData);
            } else {
                this.errors.add("The view class \"" +
                        genTypeName +
                        "\" which configured by \"" +
                        viewOfData.getConfigElement().getQualifiedName() +
                        "\" will not be generated, " +
                        "because the class \"" +
                        existed.getConfigElement().getQualifiedName() +
                        "\" has configured a view class with same name and has a higher priority.");
            }
        }
        Map<String, List<ViewOfData>> viewOfDataByTargetTypeName = this.viewOfDataByTargetTypeName;
        viewOfDataByTargetTypeName.clear();
        Map<String, List<ViewOfData>> viewOfDataByConfigTypeName = this.viewOfDataByConfigTypeName;
        viewOfDataByConfigTypeName.clear();
        for (ViewOfData viewOfData : viewOfDataMap.values()) {
            String targetName = viewOfData.getTargetElement().getQualifiedName().toString();
            String configName = viewOfData.getConfigElement().getQualifiedName().toString();
            List<ViewOfData> dataByTargetList = viewOfDataByTargetTypeName.computeIfAbsent(targetName, k -> new ArrayList<>());
            dataByTargetList.add(viewOfData);
            List<ViewOfData> dataByConfigList = viewOfDataByConfigTypeName.computeIfAbsent(configName, k -> new ArrayList<>());
            dataByConfigList.add(viewOfData);
        }
    }
}
