package com.strategy.bot;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class AlgoTask {

    public static void main(String[] args) {
//        AlgoTask algoTask = new AlgoTask();
//        algoTask.creteNodes();

//        System.out.println(filter(new int[]{1, 2, 3, 5}, new int[]{2, 3, 4}));

//        var firstArr = new int[]{1, 10, 5, 15, 20};
//        var secondArr = new int[]{1, 13, 8, 12};
//        System.out.println(findMinDifference(firstArr, secondArr));
        int factorial = factorial(2);
        int fibo = fibonacci(7);
        printAllNumber(5);

        System.out.println();
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

    public void creteNodes() {
        Node n1 = Node.builder().value(1).next(null).build();
        Node n2 = Node.builder().value(2).next(n1).build();
        Node n3 = Node.builder().value(3).next(n2).build();
        Node n4 = Node.builder().value(4).next(n3).build();
        Node n5 = Node.builder().value(5).next(n4).build();
        Node n6 = Node.builder().value(6).next(n5).build();
        reverse(n6);

        System.out.println("END " + n6);
        System.out.println("END " + n1);
    }

//              1 -> 2 -> 3 -> 4 -> null
//      null <- 1 <- 2 <- 3 <- 4


    public void some(Node node) {
        while (nonNull(node.getNext())) {

            node.setValue(node.getNext().getValue());
            node.setNext(node.getNext());

            node = node.getNext();

        }
    }

//              1 -> 2 -> 3 -> 4 -> null
//      null <- 1 <- 2 <- 3 <- 4

    public static Node revers(Node head) {
        if (head == null || head.next == null) {
            return head;
        }

        Node newNode = reverse(head.next);
        head.next = head;
        head.next = null;
        return newNode;
    }





    public static Node reverse(Node head) {
        if (head == null || head.next == null) {
            return head;
        }

        Node newHead = reverse(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    @Data
    @Builder
    @ToString(exclude = "next")
    private static class Node {
        private Node next;
        private int value;
    }

    //          TASK_1
//    Вывести все элементы первого массива, которые не встречаются во втором.
//    filter([1, 2, 3], [2, 3, 4]) => [1]
    public static List<Integer> filter(int[] arr1, int[] arr2) {
        List<Integer> list = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (arr1.length > i && arr2.length > j) {
            // если елементы из 1 массива меньше чем елемент из
            // 2 массива то кладем все в список
            if (arr1[i] < arr2[j]) {
                list.add(arr1[i++]);
            } else if (arr1[i] == arr2[j]) {
                i++;
                j++;
            } else {
                j++;
            }
        }

        while (arr1.length > i) {
            list.add(arr1[i++]);
        }
        return list;
    }


//    Дано два целочисленных массива а и b. Необходимо посчитать, чему равно min |a[i] - b[j]|.
//    Пример
//        1 10 5 15 20
//        3 16 8 12
//          Ответ: 1

    //              var firstArr =  new int[] {1, 10, 5, 15, 20};
//                  var secondArr = new int[] {3, 16, 8, 12};
//                  System.out.println(findMinDifference(firstArr, secondArr)) ;
    private static int findMinDifference(int[] arr1, int[] arr2) {
        Arrays.sort(arr1);
        Arrays.sort(arr2);

//        [1, 5, 10, 15, 20]
//        [3, 8, 12, 16]

        int i = 0;
        int j = 0;
        int temp = Integer.MAX_VALUE;
        while (arr1.length > i && arr2.length > j) {
            int abs = Math.abs(arr1[i++] - arr2[j++]);
            if (abs < temp) {
                temp = abs;
            }
            if (temp == 0) {
                return 0;
            }
        }
        System.out.println(Arrays.toString(arr1));
        System.out.println(Arrays.toString(arr2));
        return temp;
    }

    // факториал

//  1 -> 2 -> 3

    public static int factorial(int n) {

        if (n == 1) {
            return n;
        }
//      1 * 2 * 3
        return factorial(n - 1) * n;
    }


    // Последовательность фибоначи
    // 0 1 2 3 5 8 13

    public static int fibo(int numElement) {
        if (numElement - 1 <= 0) {
            return 0;
        } else if (numElement - 1 == 1) {
            return 1;
        } else if (numElement - 1 == 2) {
            return 2;
        }

//        1 -> 3-1 ->
//        2 -> 2-1 -> 1
//        3 -> 0
        int i = fibo(numElement - 2) + fibo(numElement - 1);

        return i;

    }

    // Последовательность фибоначи
    // 0 1 2 3 5 8 13
    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

//    Дано натуральное число n. Выведите все числа от 1 до n.
//    5
    public static int printAllNumber(int n) {
        if (n < 1) {
            return n;
        }

        int i = printAllNumber(n - 1);
        System.out.println(n);
        return i;
    }

//    Даны два целых числа A и В (каждое в отдельной строке).
//    Выведите все числа от A до B включительно, в порядке возрастания, если A < B,
//      или в порядке убывания в противном случае.

    //  5 ---- 10
//    public static int printAllNumber(int a, int b) {
//        if (a < b) {
//
//        }
//
//    }
}
