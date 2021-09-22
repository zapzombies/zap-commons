package io.github.zap.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ReflectionUtilsTest {
    static class TestConcreteTypeParamClassModel extends ArrayList<String> {}
    static abstract class TestInheritedConcreteTypeParamClassModel<T> extends TestConcreteTypeParamClassModel implements Supplier<T> {
    }

    static class TestClassModel {
        private ArrayList<String> singleParams;
        private HashMap<String, ArrayList<String>> multipleNestedParams;
        private TestConcreteTypeParamClassModel concreteTypeParams;
        private TestInheritedConcreteTypeParamClassModel<Boolean> inheritedConcreteTypeParams;
    }


    @Test
    public void interfaceTarget() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                (ParameterizedType) TestClassModel.class.getDeclaredField("singleParams").getGenericType(),
                Iterable.class);

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.stream().allMatch(i -> i.size() == 1 && i.get(0) == String.class));
    }

    @Test
    public void classTarget() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                (ParameterizedType) TestClassModel.class.getDeclaredField("singleParams").getGenericType(),
                AbstractList.class);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.stream().allMatch(i -> i.size() == 1 && i.get(0) == String.class));
    }

    @Test
    public void multipleNestedParams() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                (ParameterizedType) TestClassModel.class.getDeclaredField("multipleNestedParams").getGenericType(),
                Map.class);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(i -> i.size() == 2 && i.get(0) == String.class && ((ParameterizedType) i.get(1)).getRawType() == ArrayList.class));
    }


    @Test
    public void incorrectSuperclass() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                (ParameterizedType) TestClassModel.class.getDeclaredField("multipleNestedParams").getGenericType(),
                Iterable.class);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void concreteTypeParams() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                NonParameterizedType.fromClass(TestClassModel.class.getDeclaredField("concreteTypeParams").getType()),
                Iterable.class);

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.stream().allMatch(i -> i.size() == 1 && i.get(0) == String.class));;
    }

    @Test
    public void inheritedConcreteTypeParams() throws NoSuchFieldException {
        var result = ReflectionUtils.getSuperclassTypeParams(
                NonParameterizedType.fromClass(TestClassModel.class.getDeclaredField("inheritedConcreteTypeParams").getType()),
                Iterable.class);

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.stream().allMatch(i -> i.size() == 1 && i.get(0) == String.class));;
    }

}
