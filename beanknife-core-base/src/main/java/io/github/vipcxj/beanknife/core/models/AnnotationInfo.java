package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.AnnotationUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.vipcxj.beanknife.core.utils.AnnotationUtils.*;

@SuppressWarnings("unused")
public class AnnotationInfo {
    
    @NonNull
    private final Elements elements;
    @NonNull
    private final AnnotationMirror annotationMirror;
    @NonNull
    private final Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues;
    
    public AnnotationInfo(@NonNull Elements elements, @NonNull AnnotationMirror annotationMirror) {
        this.elements = elements;
        this.annotationMirror = annotationMirror;
        this.annotationValues = elements.getElementValuesWithDefaults(annotationMirror);
    }
    
    public String getString(String name) {
        return AnnotationUtils.getStringAnnotationValue(annotationMirror, annotationValues, name);
    }
    
    public List<String> getStringList(String name) {
        return AnnotationUtils.getStringListAnnotationValue(annotationMirror, annotationValues, name);
    }

    public boolean getBoolean(@NonNull String name) {
        return getBooleanAnnotationValue(annotationMirror, annotationValues, name);
    }

    public boolean[] getBooleanArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, boolean[].class);
    }

    public short getShort(@NonNull String name) {
        return getShortAnnotationValue(annotationMirror, annotationValues, name);
    }

    public short[] getShortArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, short[].class);
    }

    public int getInt(@NonNull String name) {
        return getIntegerAnnotationValue(annotationMirror, annotationValues, name);
    }

    public int[] getIntArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, int[].class);
    }

    public long getLong(@NonNull String name) {
        return getLongAnnotationValue(annotationMirror, annotationValues, name);
    }

    public long[] getLongArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, long[].class);
    }

    public float getFloat(@NonNull String name) {
        return getFloatAnnotationValue(annotationMirror, annotationValues, name);
    }

    public float[] getFloatArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, float[].class);
    }

    public double getDouble(@NonNull String name) {
        return getDoubleAnnotationValue(annotationMirror, annotationValues, name);
    }

    public double[] getDoubleArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, double[].class);
    }

    public char getChar(@NonNull String name) {
        return getCharacterAnnotationValue(annotationMirror, annotationValues, name);
    }

    public char[] getCharArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, char[].class);
    }

    public byte getByte(@NonNull String name) {
        return getByteAnnotationValue(annotationMirror, annotationValues, name);
    }

    public byte[] getByteArray(@NonNull String name) {
        return getPrimitiveArrayAnnotationValue(annotationMirror, annotationValues, name, byte[].class);
    }

    public <T extends TypeMirror> T getType(@NonNull String name) {
        return getTypeAnnotationValue(annotationMirror, annotationValues, name);
    }

    public <T extends TypeMirror> List<T> getTypeList(@NonNull String name) {
        return getTypeListAnnotationValue(annotationMirror, annotationValues, name);
    }

    @NonNull
    public <T extends Enum<T>> T getEnum(@NonNull String name, @NonNull Class<T> enumType) {
        return getEnumAnnotationValue(annotationMirror, annotationValues, name, enumType);
    }

    @NonNull
    public <T extends Enum<T>> List<T> getEnumList(@NonNull String name, @NonNull Class<T> enumType) {
        return getEnumListAnnotationValue(annotationMirror, annotationValues, name, enumType);
    }

    @NonNull
    public String getEnumString(@NonNull String name) {
        return getEnumAnnotationValue(annotationMirror, annotationValues, name);
    }

    @NonNull
    public List<String> getEnumStringList(@NonNull String name) {
        return getEnumListAnnotationValue(annotationMirror, annotationValues, name);
    }

    @NonNull
    public AnnotationInfo getAnnotation(@NonNull String name) {
        AnnotationMirror value = getAnnotationAnnotationValue(annotationMirror, annotationValues, name);
        return new AnnotationInfo(elements, value);
    }

    @NonNull
    public List<AnnotationInfo> getAnnotationList(@NonNull String name) {
        List<AnnotationMirror> value = getAnnotationListAnnotationValue(annotationMirror, annotationValues, name);
        return value.stream().map(a -> new AnnotationInfo(elements, a)).collect(Collectors.toList());
    }
}
