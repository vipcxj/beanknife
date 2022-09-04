package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.models.Context;
import io.github.vipcxj.beanknife.core.models.Type;
import org.apache.commons.text.StringEscapeUtils;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class AnnotationUtils {

    @NonNull
    public static String getAnnotationName(@NonNull AnnotationMirror mirror) {
        return Utils.toElement(mirror.getAnnotationType()).getQualifiedName().toString();
    }

    @CheckForNull
    public static AnnotationValue getAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
            if (name.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @NonNull
    private static <T> T toPrimitive(@NonNull AnnotationValue annotationValue, @NonNull Class<T> boxedType) {
        //noinspection unchecked
        return (T) annotationValue.getValue();
    }

    @CheckForNull
    public static <T> T getPrimitiveAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name, @NonNull Class<T> boxedType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toPrimitive(annotationValue, boxedType);
    }

    @NonNull
    private static <T> T toPrimitiveArray(@NonNull AnnotationValue annotationValue, @NonNull Class<T> primitiveArrayType) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        Class<?> primitiveType = primitiveArrayType.getComponentType();
        if (primitiveType == null) {
            throw new IllegalArgumentException("The input type must be a primitive array type, but it is " + primitiveArrayType.getName() + ".");
        }
        Object out = Array.newInstance(primitiveType, arrayValue.size());
        for (int i = 0; i < arrayValue.size(); ++i) {
            Object value = arrayValue.get(i).getValue();
            if (primitiveType == int.class) {
                Array.setInt(out, i, (Integer) value);
            } else if (primitiveType == long.class) {
                Array.setLong(out, i, (Long) value);
            } else if (primitiveType == boolean.class) {
                Array.setBoolean(out, i, (Boolean) value);
            } else if (primitiveType == float.class) {
                Array.setFloat(out, i, (Float) value);
            } else if (primitiveType == double.class) {
                Array.setDouble(out, i, (Double) value);
            } else if (primitiveType == char.class) {
                Array.setChar(out, i, (Character) value);
            } else if (primitiveType == byte.class) {
                Array.setByte(out, i, (Byte) value);
            } else if (primitiveType == short.class) {
                Array.setShort(out, i, (Short) value);
            } else {
                throw new IllegalArgumentException("The input type must be a primitive array type, but it is " + primitiveArrayType.getName() + ".");
            }
        }
        //noinspection unchecked
        return (T) out;
    }

    @CheckForNull
    public static <T> T getPrimitiveArrayAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name, @NonNull Class<T> primitiveArrayType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toPrimitiveArray(annotationValue, primitiveArrayType);
    }

    @NonNull
    private static String toString(@NonNull AnnotationValue annotationValue) {
        return (String) annotationValue.getValue();
    }

    @CheckForNull
    public static String getStringAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toString(annotationValue);
    }

    @NonNull
    private static List<String> toStringArray(@NonNull AnnotationValue annotationValue) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        return arrayValue.stream().map(value -> (String) value.getValue()).collect(Collectors.toList());
    }

    @CheckForNull
    public static List<String> getStringListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toStringArray(annotationValue);
    }

    @NonNull
    private static String toEnumString(@NonNull AnnotationValue annotationValue) {
        VariableElement variableElement = (VariableElement) annotationValue.getValue();
        TypeElement enumClass = (TypeElement) variableElement.getEnclosingElement();
        return enumClass.getQualifiedName() + "." + variableElement.getSimpleName();
    }

    @NonNull
    private static <T extends Enum<T>> T getEnum(@NonNull String qName, @NonNull Class<T> type) {
        String typeName = type.getName();
        for (T constant : type.getEnumConstants()) {
            if (Objects.equals(qName, typeName + "." + constant.name())) {
                return constant;
            }
        }
        throw new IllegalArgumentException(qName + " is not a enum of type " + typeName + ".");
    }

    @NonNull
    private static <T extends Enum<T>> T toEnum(@NonNull AnnotationValue annotationValue, @NonNull Class<T> enumType) {
        return getEnum(toEnumString(annotationValue), enumType);
    }

    @CheckForNull
    public static <T extends Enum<T>> T getEnumAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name, @NonNull Class<T> enumType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toEnum(annotationValue, enumType);
    }

    @NonNull
    private static <T extends Enum<T>> List<T> toEnumList(@NonNull AnnotationValue annotationValue, @NonNull Class<T> enumType) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        return arrayValue.stream().map(a -> toEnum(a, enumType)).collect(Collectors.toList());
    }

    @CheckForNull
    public static <T extends Enum<T>> List<T> getEnumArrayAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name, @NonNull Class<T> enumType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toEnumList(annotationValue, enumType);
    }

    @CheckForNull
    public static String getEnumAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toEnumString(annotationValue);
    }

    @NonNull
    private static List<String> toEnumList(@NonNull AnnotationValue annotationValue) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        return arrayValue.stream().map(AnnotationUtils::toEnumString).collect(Collectors.toList());
    }

    @CheckForNull
    public static List<String> getEnumListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toEnumList(annotationValue);
    }

    @CheckForNull
    public static Boolean getBooleanAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Boolean.class);
    }

    @CheckForNull
    public static Long getLongAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Long.class);
    }

    @CheckForNull
    public static Integer getIntegerAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Integer.class);
    }

    @CheckForNull
    public static Float getFloatAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Float.class);
    }

    @CheckForNull
    public static Short getShortAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Short.class);
    }

    @CheckForNull
    public static Character getCharacterAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Character.class);
    }

    @CheckForNull
    public static Double getDoubleAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Double.class);
    }

    @CheckForNull
    public static Byte getByteAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, name, Byte.class);
    }

    @NonNull
    private static <T extends TypeMirror> T toType(@NonNull AnnotationValue annotationValue) {
        //noinspection unchecked
        return (T) annotationValue.getValue();
    }

    @CheckForNull
    public static <T extends TypeMirror> T getTypeAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toType(annotationValue);
    }

    @NonNull
    private static <T extends TypeMirror> List<T> toTypeList(@NonNull AnnotationValue annotationValue) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        return arrayValue.stream().map(AnnotationUtils::<T>toType).collect(Collectors.toList());
    }

    @CheckForNull
    public static <T extends TypeMirror> List<T> getTypeListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toTypeList(annotationValue);
    }

    @NonNull
    private static AnnotationMirror toAnnotation(@NonNull AnnotationValue annotationValue) {
        return (AnnotationMirror) annotationValue.getValue();
    }

    @CheckForNull
    public static AnnotationMirror getAnnotationAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toAnnotation(annotationValue);
    }

    @NonNull
    private static List<AnnotationMirror> toAnnotationList(@NonNull AnnotationValue annotationValue) {
        //noinspection unchecked
        List<? extends AnnotationValue> arrayValue = (List<? extends AnnotationValue>) annotationValue.getValue();
        return arrayValue.stream().map(AnnotationUtils::toAnnotation).collect(Collectors.toList());
    }

    @CheckForNull
    public static List<AnnotationMirror> getAnnotationListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, name);
        if (annotationValue == null) {
            return null;
        }
        return toAnnotationList(annotationValue);
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
            if (name.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("There is no attribute named \"" + name + "\" in annotation " + getAnnotationName(annotation));
    }

    public enum AnnotationValueKind {
        BOXED, STRING, TYPE, ENUM, ANNOTATION, ARRAY
    }
    public static AnnotationValueKind getAnnotationValueType(@NonNull AnnotationValue value) {
        Object v = value.getValue();
        if (v instanceof Boolean
                || v instanceof Integer
                || v instanceof Long
                || v instanceof Float
                || v instanceof Short
                || v instanceof Character
                || v instanceof Double
                || v instanceof Byte
        ) {
            return AnnotationValueKind.BOXED;
        } else if (v instanceof String) {
            return AnnotationValueKind.STRING;
        } else if (v instanceof TypeMirror) {
            return AnnotationValueKind.TYPE;
        } else if (v instanceof VariableElement) {
            return AnnotationValueKind.ENUM;
        } else if (v instanceof AnnotationMirror) {
            return AnnotationValueKind.ANNOTATION;
        } else if (v instanceof List) {
            return AnnotationValueKind.ARRAY;
        }
        throw new IllegalArgumentException("This is impossible.");
    }

    private static void throwCastAnnotationValueTypeError(
            @NonNull AnnotationMirror annotation,
            @NonNull String attributeName,
            @NonNull AnnotationValueKind fromKind,
            @NonNull AnnotationValueKind toKind
    ) {
        throw new IllegalArgumentException(
                "Unable to cast attribute named \""
                        + attributeName
                        + "\" in annotation "
                        + getAnnotationName(annotation)
                        + " from "
                        + fromKind
                        + " to "
                        + toKind
                        + "."
        );
    }

    @NonNull
    public static String getStringAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.STRING) {
            return toString(annotationValue);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.STRING);
        throw new IllegalArgumentException("This is impossible.");
    }

    @NonNull
    public static <T> T getPrimitiveAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name, @NonNull Class<T> boxedType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.BOXED) {
            return toPrimitive(annotationValue, boxedType);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.BOXED);
        throw new IllegalArgumentException("This is impossible.");
    }

    @NonNull
    public static <T> T getPrimitiveArrayAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name, @NonNull Class<T> primitiveArrayType) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ARRAY) {
            return toPrimitiveArray(annotationValue, primitiveArrayType);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ARRAY);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static boolean getBooleanAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Boolean.class);
    }

    public static short getShortAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Short.class);
    }

    public static int getIntegerAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Integer.class);
    }

    public static long getLongAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Long.class);
    }

    public static float getFloatAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Float.class);
    }

    public static double getDoubleAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Double.class);
    }

    public static char getCharacterAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Character.class);
    }

    public static byte getByteAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        return getPrimitiveAnnotationValue(annotation, annotationValues, name, Byte.class);
    }

    public static <T extends TypeMirror> T getTypeAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.TYPE) {
            return toType(annotationValue);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.TYPE);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static String getEnumAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ENUM) {
            return toEnumString(annotationValue);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ENUM);
        throw new IllegalArgumentException("This is impossible.");
    }

    @NonNull
    public static <T extends Enum<T>> T getEnumAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name, @NonNull Class<T> enumType) {
        String qName = getEnumAnnotationValue(annotation, annotationValues, name);
        return Objects.requireNonNull(getEnum(qName, enumType));
    }

    @NonNull
    public static AnnotationMirror getAnnotationAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ANNOTATION) {
            return toAnnotation(annotationValue);
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ANNOTATION);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static List<? extends AnnotationValue> getArrayAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ARRAY) {
            //noinspection unchecked
            return (List<? extends AnnotationValue>) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ARRAY);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static List<String> getStringListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        return annValues.stream().map(AnnotationUtils::toString).collect(Collectors.toList());
    }

    public static <T extends TypeMirror> List<T> getTypeListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        return annValues.stream().map(AnnotationUtils::<T>toType).collect(Collectors.toList());
    }

    public static List<String> getEnumListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        return annValues.stream().map(AnnotationUtils::toEnumString).collect(Collectors.toList());
    }

    public static <T extends Enum<T>> List<T> getEnumListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name, @NonNull Class<T> enumType) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        return annValues.stream().map(a -> toEnum(a, enumType)).collect(Collectors.toList());
    }

    public static List<AnnotationMirror> getAnnotationListAnnotationValue(@NonNull AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @NonNull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        return annValues.stream().map(AnnotationUtils::toAnnotation).collect(Collectors.toList());
    }

    public static List<AnnotationMirror> getAnnotationElement(AnnotationMirror annotation, @NonNull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
            if ("value".equals(entry.getKey().getSimpleName().toString())) {
                //noinspection unchecked
                List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) entry.getValue().getValue();
                List<AnnotationMirror> annotations = new ArrayList<>();
                for (AnnotationValue value : values) {
                    annotations.add((AnnotationMirror) value.getValue());
                }
                return annotations;
            }
        }
        throw new IllegalArgumentException("There is no attribute named value in annotation " + getAnnotationName(annotation));
    }

    private static TypeMirror getFinalComponentType(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.ARRAY) {
            return typeMirror;
        }
        ArrayType arrayType = (ArrayType) typeMirror;
        return getFinalComponentType(arrayType.getComponentType());
    }

    private static int calcArrayLevel(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.ARRAY) {
            return 0;
        }
        ArrayType arrayType = (ArrayType) typeMirror;
        return 1 + calcArrayLevel(arrayType.getComponentType());
    }

    private static void printTypeAnnotationValue(@NonNull PrintWriter writer, @NonNull Context context, @NonNull TypeMirror typeMirror, boolean hasDotClass) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            TypeElement typeElement = Utils.toElement(declaredType);
            Type type = Type.extract(context, typeElement);
            if (type == null) {
                context.error("Unable to resolve type: " + typeElement.getQualifiedName() + ".");
                writer.print("error");
                return;
            } else {
                type.printType(writer, context, false, false);
            }
        } else if (typeMirror.getKind() == TypeKind.ARRAY) {
            int arrayLevel = calcArrayLevel(typeMirror);
            TypeMirror componentType = getFinalComponentType(typeMirror);
            printTypeAnnotationValue(writer, context, componentType, false);
            for (int i = 0; i < arrayLevel; ++i) {
                writer.print("[]");
            }
        } else if (typeMirror.getKind() == TypeKind.INT) {
            writer.print("int");
        } else if (typeMirror.getKind() == TypeKind.BOOLEAN) {
            writer.print("boolean");
        } else if (typeMirror.getKind() == TypeKind.VOID) {
            writer.print("void");
        } else if (typeMirror.getKind() == TypeKind.BYTE) {
            writer.print("byte");
        } else if (typeMirror.getKind() == TypeKind.CHAR) {
            writer.print("char");
        } else if (typeMirror.getKind() == TypeKind.DOUBLE) {
            writer.print("double");
        } else if (typeMirror.getKind() == TypeKind.FLOAT) {
            writer.print("float");
        } else if (typeMirror.getKind() == TypeKind.LONG) {
            writer.print("long");
        } else if (typeMirror.getKind() == TypeKind.SHORT) {
            writer.print("short");
        } else {
            context.error("Unable to resolve type: " + typeMirror + ".");
            writer.print("error");
            return;
        }
        if (hasDotClass) {
            writer.print(".class");
        }
    }

    public static void printAnnotationValue(@NonNull PrintWriter writer, @NonNull AnnotationValue annValue, @NonNull Context context, String indent, int indentNum) {
        Object value = annValue.getValue();
        AnnotationValueKind valueType = getAnnotationValueType(annValue);
        switch (valueType) {
            case ENUM: {
                VariableElement enumValue = (VariableElement) value;
                TypeElement enumClass = (TypeElement) enumValue.getEnclosingElement();
                Type enumClassType = Type.extract(context, enumClass);
                if (enumClassType == null) {
                    context.error("Unable to resolve type: " + enumClass.getQualifiedName() + ".");
                    writer.print("error");
                } else {
                    enumClassType.printType(writer, context, false, false);
                    writer.print(".");
                    writer.print(enumValue.getSimpleName());
                }
                break;
            }
            case BOXED: {
                if (value instanceof Character) {
                    writer.print("'");
                    writer.print(value);
                    writer.print("'");
                } else if (value instanceof Long) {
                    writer.print(value);
                    writer.print("l");
                } else if (value instanceof Float) {
                    writer.print(value);
                    writer.print("f");
                } else {
                    writer.print(value);
                }
                break;
            }
            case TYPE: {
                TypeMirror typeMirror = (TypeMirror) value;
                printTypeAnnotationValue(writer, context, typeMirror, true);
                break;
            }
            case STRING: {
                writer.print("\"");
                writer.print(StringEscapeUtils.escapeJava((String) value));
                writer.print("\"");
                break;
            }
            case ANNOTATION: {
                printAnnotation(writer, (AnnotationMirror) value, context, indent, indentNum);
                break;
            }
            case ARRAY: {
                //noinspection unchecked
                List<? extends AnnotationValue> annotationValues = (List<? extends AnnotationValue>) value;
                if (annotationValues.isEmpty()) {
                    writer.print("{}");
                } else {
                    writer.println("{");
                    int i = 0;
                    for (AnnotationValue annotationValue : annotationValues) {
                        Utils.printIndent(writer, indent, indentNum + 1);
                        printAnnotationValue(writer, annotationValue, context, indent, indentNum + 1);
                        if (i != annotationValues.size() - 1) {
                            writer.println(",");
                        } else {
                            writer.println();
                        }
                        ++i;
                    }
                    Utils.printIndent(writer, indent, indentNum);
                    writer.print("}");
                }
                break;
            }
            default:
                throw new IllegalArgumentException("This is impossible.");
        }
    }

    private static boolean shouldBreakLineForPrintingAnnotation(Collection<? extends AnnotationValue> annotationValues) {
        return annotationValues.size() > 3
                || (annotationValues.size() != 1 && annotationValues.stream().anyMatch(a -> a.getValue() instanceof List && !((List<?>) a.getValue()).isEmpty()));
    }

    public static void printAnnotation(@NonNull PrintWriter writer, @NonNull AnnotationMirror annotation, @NonNull Context context, String indent, int indentNum) {
        writer.print("@");
        Type type = Type.extract(context, annotation.getAnnotationType().asElement());
        type.printType(writer, context, false, false);
        Map<? extends ExecutableElement, ? extends AnnotationValue> attributes = annotation.getElementValues();
        if (!attributes.isEmpty()) {
            boolean shouldBreakLine = shouldBreakLineForPrintingAnnotation(attributes.values());
            boolean useValue = attributes.size() == 1 && attributes.keySet().iterator().next().getSimpleName().toString().equals("value");
            if (useValue) {
                AnnotationValue annotationValue = attributes.values().iterator().next();
                writer.print("(");
                printAnnotationValue(writer, annotationValue, context, indent, indentNum);
                writer.print(")");
            } else if (shouldBreakLine) {
                writer.println("(");
                int i = 0;
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : attributes.entrySet()) {
                    Utils.printIndent(writer, indent, indentNum + 1);
                    writer.print(entry.getKey().getSimpleName());
                    writer.print(" = ");
                    printAnnotationValue(writer, entry.getValue(), context, indent, indentNum + 1);
                    if (i != attributes.size() - 1) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                    ++i;
                }
                Utils.printIndent(writer, indent, indentNum);
                writer.print(")");
            } else {
                writer.print("(");
                int i = 0;
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : attributes.entrySet()) {
                    writer.print(entry.getKey().getSimpleName());
                    writer.print(" = ");
                    printAnnotationValue(writer, entry.getValue(), context, indent, indentNum);
                    if (i != attributes.size() - 1) {
                        writer.print(", ");
                    }
                    ++i;
                }
                writer.print(")");
            }
        }
    }
}
