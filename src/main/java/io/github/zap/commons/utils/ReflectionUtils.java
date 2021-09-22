package io.github.zap.commons.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
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
                    if(!includeSelf || item != target)
                        result = item;
                }
            }
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
    public static List<List<? extends Class<?>>> getSuperclassTypeParams(ParameterizedType clazz, Class<?> targetAncestor) {
        Map<TypeVariable<?>, Class<?>> rootActualTypeArgs = new HashMap<>();
        var pt = clazz.getActualTypeArguments();
        var rawPt = ((Class<?>) clazz.getRawType()).getTypeParameters();

        for(var i = 0; i < pt.length; i++) {
            rootActualTypeArgs.put(rawPt[i], (Class<?>) pt[i]);
        }
        return GetSuperClassTypeParamsHelper.traverse(clazz, targetAncestor, rootActualTypeArgs);
    }

    static class GetSuperClassTypeParamsHelper {
        @NotNull
        private static List<List<? extends Class<?>>> traverse(
                ParameterizedType parameterizedType,
                Class<?> targetAncestor,
                Map<TypeVariable<?>, Class<?>> rootActualTypeArgs) {
            List<List<? extends Class<?>>> resultBuffer = new ArrayList<>();
            var rawType = (Class<?>) parameterizedType.getRawType();

            for(var interfaze : rawType.getGenericInterfaces()) {
                if(interfaze instanceof ParameterizedType ipt) {
                    if(ipt.getRawType() == targetAncestor) {
                        resultBuffer.add(rootActualTypeArgs.values().stream().toList());
                    } else {
                        resultBuffer.addAll(traverse(ipt, targetAncestor, updateRootTypeParams(rootActualTypeArgs, ipt)));
                    }
                }
            }

            var ct = rawType.getGenericSuperclass();
            if(ct instanceof ParameterizedType cpt) {
                resultBuffer.addAll(traverse(cpt, targetAncestor, updateRootTypeParams(rootActualTypeArgs, cpt)));
            }
            return resultBuffer;
        }

        @NotNull
        private static HashMap<TypeVariable<?>, Class<?>> updateRootTypeParams(
                Map<TypeVariable<?>, Class<?>> rootActualTypeArgs,
                ParameterizedType pt) {
            var actualTypesParams = pt.getActualTypeArguments();
            var actualTypesParamsRoot = new Class<?>[actualTypesParams.length];
            var actualTypesParamsRootMap = new HashMap<TypeVariable<?>, Class<?>>();
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

