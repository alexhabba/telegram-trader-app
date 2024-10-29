package com.strategy.bot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AlgoTask {

    public static void main(String[] args) {

        List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");


        myList.stream()
                .filter(s -> s.contains("c"))
                .map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);

    }

    public static int[] sum(int[] arr, int n) {
        if (isNull(arr) || arr.length < 2) return new int[0];

        Map<Integer, Integer> mapElementArrAndIndex = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            Integer el = mapElementArrAndIndex.get(n - arr[i]);
            if (nonNull(el)) {
                return new int[]{el, i};
            }
            mapElementArrAndIndex.put(arr[i], i);
        }
        return new int[0];
    }


//    List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
//    отфильтровать значения, оставить те, что начинаются с «с», перевести в верхний регистр, отсортировать по порядку и вывести каждый элемент на экран
}
