package io.github.zap.commons.utils;

import com.google.common.collect.ObjectArrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Helper class for dealing with shitty Java reflection
 */
public class ReflectionUtils {
    /**
     * Get the nearest superclass (including itself) from a set of classes
     * @param target target class
     * @param classes the set of classes
     * @return the nearest superclass
     */
    @Nullable
    public static Class<?> nearestSuperclass(@NotNull Class<?> target, @NotNull Set<Class<?>> classes) {
        return nearestSuperclass(target, classes, true);
    }

    /**
     * Get the nearest superclass from a set of classes
     * @param target target class
     * @param classes the set of classes
     * @param includeSelf exclude target class from the set of classes
     * @return the nearest superclass
     */
    @Nullable
    public static Class<?> nearestSuperclass(@NotNull Class<?> target, @NotNull Set<Class<?>> classes, boolean includeSelf) {
        Objects.requireNonNull(target, "target cannot be null!");
        Objects.requireNonNull(classes, "classes cannot be null!");

        Class<?> result = null;

        for(var item : classes) {
            if(item.isAssignableFrom(target)) {
                if(result == null || result.isAssignableFrom(item)) {
                    if(!includeSelf || item != target)
                        result = item;
                }
            }
        }

        return result;
    }

    /**
     * Get the nearest subclass (including itself) from a set of classes
     * @param target target class
     * @param classes the set of classes
     * @return the nearest subclass
     */
    @Nullable
    public static Class<?> nearestSubclass(@NotNull Class<?> target, @NotNull Set<Class<?>> classes) {
        return nearestSubclass(target, classes, true);
    }

    /**
     * Get the nearest subclass from a set of classes
     * @param target target class
     * @param classes the set of classes
     * @param includeSelf exclude target class from the set of classes
     * @return the nearest subclass
     */
    @Nullable
    public static Class<?> nearestSubclass(@NotNull Class<?> target, @NotNull Set<Class<?>> classes, boolean includeSelf) {
        Objects.requireNonNull(target, "target cannot be null!");
        Objects.requireNonNull(classes, "classes cannot be null!");

        Class<?> result = null;

        for(var item : classes) {
            if(target.isAssignableFrom(item)) {
                if(result == null || item.isAssignableFrom(result)) {
                    if(includeSelf || item != target)
                        result = item;
                }
            }
        }

        return result;
    }


    /**
     * Get all fields annotated with a specified annotation from a class
     * @param target the class to retrieve fields
     * @param annotation the annotation to look for
     * @param <T> the type of the annotation
     * @return A hashmap contains the matching {@link Field}
     */
    public static <T extends Annotation> Map<Field, T> getAnnotatedFields(Class<?> target, Class<T> annotation) {
        var fields = getFieldsUpTo(target, null);
        var resultMap = new HashMap<Field, T>();

        for(int i = 0; i < fields.length; i++) {
            var instance = fields[i].getAnnotation(annotation);
            if(instance != null) {
                resultMap.put(fields[i], instance);
            }
        }

        return resultMap;
    }

    /**
     * get all declared fields from superclass and itself
     * @param type the class to search
     * @param exclusiveParent the superclass to search up to
     * @return a list of fields
     */
    public static Field[] getFieldsUpTo(@Nonnull Class<?> type, @Nullable Class<?> exclusiveParent) {
        Field[] result = type.getDeclaredFields();

        Class<?> parentClass = type.getSuperclass();
        if (parentClass != null && (!parentClass.equals(exclusiveParent))) {
            Field[] parentClassFields = getFieldsUpTo(parentClass, exclusiveParent);
            result = ObjectArrays.concat(result, parentClassFields, Field.class);
        }

        return result;
    }

    /**
     * Get the type parameters of a specified class for a given subclass.
     * Eg. Get Iterator type params of an ArrayList
     * @param clazz the subclass to search
     * @param targetAncestor the target superclass
     * @return a list of type params list, each nested list contains the ordered type params of the superclass
     */
    @NotNull
    public static List<List<? extends Type>> getSuperclassTypeParams(ParameterizedType clazz, Type targetAncestor) {
        Map<TypeVariable<?>, Type> rootActualTypeArgs = new HashMap<>();
        var pt = clazz.getActualTypeArguments();
        var rawPt = ((Class<?>) clazz.getRawType()).getTypeParameters();

        for(var i = 0; i < pt.length; i++) {
            rootActualTypeArgs.put(rawPt[i], pt[i]);
        }
        return GetSuperClassTypeParamsHelper.traverse(clazz, targetAncestor, rootActualTypeArgs);
    }


    static class GetSuperClassTypeParamsHelper {
        @NotNull
        private static List<List<? extends Type>> traverse(
                ParameterizedType parameterizedType,
                Type targetAncestor,
                Map<TypeVariable<?>, Type> rootActualTypeArgs) {
            List<List<? extends Type>> resultBuffer = new ArrayList<>();
            var rawType = (Class<?>) parameterizedType.getRawType();

            for(var interfaze : rawType.getGenericInterfaces()) {
                resultBuffer.addAll(traverseSuperclass(interfaze, targetAncestor, rootActualTypeArgs));
            }

            resultBuffer.addAll(traverseSuperclass(rawType.getGenericSuperclass(), targetAncestor, rootActualTypeArgs));
            return resultBuffer;
        }

        @NotNull
        private static List<List<? extends Type>> traverseSuperclass(Type type, Type targetAncestor, Map<TypeVariable<?>, Type> rootActualTypeArgs) {
            List<List<? extends Type>> resultBuffer = new ArrayList<>();
            if(type instanceof ParameterizedType pt) {
                if(pt.getRawType() == targetAncestor) {
                    resultBuffer.add(updateRootTypeParams(rootActualTypeArgs, pt).values().stream().toList());
                } else {
                    resultBuffer.addAll(traverse(pt, targetAncestor, updateRootTypeParams(rootActualTypeArgs, pt)));
                }
            } else if (type instanceof Class<?> clazz) {
                var child = clazz.getGenericSuperclass();
                if(child != null) {
                    var ipt = child instanceof ParameterizedType ?
                            (ParameterizedType) child :
                            NonParameterizedType.fromClass(((Class<?>) child));
                    resultBuffer.addAll(traverse(ipt, targetAncestor, updateRootTypeParams(rootActualTypeArgs, ipt)));
                }
            }

            return resultBuffer;
        }

        @NotNull
        private static Map<TypeVariable<?>, Type> updateRootTypeParams(
                Map<TypeVariable<?>, Type> rootActualTypeArgs,
                ParameterizedType pt) {
            var actualTypesParams = pt.getActualTypeArguments();
            var actualTypesParamsRoot = new Type[actualTypesParams.length];
            var actualTypesParamsRootMap = new LinkedHashMap<TypeVariable<?>, Type>();
            for(int i = 0; i < actualTypesParams.length; i++) {
                actualTypesParamsRoot[i] = actualTypesParams[i] instanceof Class<?> actualClassParam ?
                        actualClassParam :
                        rootActualTypeArgs.get((TypeVariable<?>) actualTypesParams[i]);
            }

            var rawPt = ((Class<?>) pt.getRawType()).getTypeParameters();
            for (int i = 0; i < rawPt.length; i++) {
                actualTypesParamsRootMap.put(rawPt[i], actualTypesParamsRoot[i]);
            }
            return actualTypesParamsRootMap;
        }
    }
}

