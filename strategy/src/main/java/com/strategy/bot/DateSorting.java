package com.strategy.bot;

import java.util.*;

public class DateSorting {

    public static void main(String[] args) {
        List<MyObject> objects = List.of(
                new MyObject("Object 1", null),
                new MyObject("Object 2", new Date()),
                new MyObject("Object 3", new Date())
        );
        objects = new ArrayList<>(objects);

        // Сортировка по дате с обработкой null-значений
        objects.sort(Comparator.comparing(MyObject::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        // Вывод отсортированного списка
        objects.forEach(System.out::println);
    }

    // Класс с датой
    private static class MyObject {
        private String name;
        private Date date;

        public MyObject(String name, Date date) {
            this.name = name;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "MyObject{" +
                    "name='" + name + '\'' +
                    ", date=" + date +
                    '}';
        }
    }
}


