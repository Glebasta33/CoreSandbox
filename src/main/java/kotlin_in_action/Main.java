package kotlin_in_action;

import c2_basics.Person2;

public class Main {
    public static void main(String[] args) {
        Person2 person = new Person2("Bob", false);
        // для Java у Kotlin-классов генрятся методы доступа:
        System.out.println(person.getName());
        System.out.println(person.isMarried());
        person.setMarried(true);
        System.out.println(person.isMarried());

        //цикл на Java:
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
        }
    }

    // Если внутри метода не обработать checked-exception обязательно нужно пометить метод:
    private void throwException() throws Exception {
        throw new Exception();
    }

    private void handleException() {
        try {
            throw new Exception();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}