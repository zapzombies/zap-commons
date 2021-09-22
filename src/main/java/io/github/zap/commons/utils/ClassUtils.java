package io.github.zap.commons.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class ClassUtils {
    @Nullable
    public static Class<?> nearestAncestor(@NotNull Class<?> target, @NotNull Set<Class<?>> classes) {
        Objects.requireNonNull(target, "target cannot be null!");
        Objects.requireNonNull(classes, "classes cannot be null!");

        Class<?> result = null;

        for(var item : classes) {
            if(item.isAssignableFrom(target)) {
                if(result == null || result.isAssignableFrom(item)) {
                    result = item;
                }
            }
        }

        return result;
    }

    @Nullable
    public static Class<?> nearestDescendant(@NotNull Class<?> target, @NotNull Set<Class<?>> classes) {
        Objects.requireNonNull(target, "target cannot be null!");
        Objects.requireNonNull(classes, "classes cannot be null!");

        Class<?> result = null;

        for(var item : classes) {
            if(target.isAssignableFrom(item)) {
                if(result == null || item.isAssignableFrom(result)) {
                    result = item;
                }
            }
        }

        return result;
    }

    @NotNull
    public static List<List<? extends Class<?>>> getGenericAncestor(ParameterizedType clazz, Class<?> targetAncestor) {
        Map<TypeVariable<?>, Class<?>> rootActualTypeArgs = new HashMap<>();
        var pt = clazz.getActualTypeArguments();
        var rawPt = ((Class<?>) clazz.getRawType()).getTypeParameters();

        for(var i = 0; i < pt.length; i++) {
            rootActualTypeArgs.put(((TypeVariable<?>) rawPt[i]), (Class<?>) pt[i]);
        }
        return GetGenericAncestorHelper.traverse(clazz, targetAncestor, rootActualTypeArgs);
    }

    static class GetGenericAncestorHelper {
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

