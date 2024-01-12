package kotlin_in_action;

import basics.Person2;

public class Main {
    public static void main(String[] args) {
        Person2 person = new Person2("Bob", false);
        // для Java у Kotlin-классов генрятся методы доступа:
        System.out.println(person.getName());
        System.out.println(person.isMarried());
        person.setMarried(true);
        System.out.println(person.isMarried());
    }
}
