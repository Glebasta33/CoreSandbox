package kotlin_in_action.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {


    public static void iterateAnimals(Collection<Animal> animals) {}

    public static void iterateAnimalsWildcard(Collection<? extends Animal> animals) {}

    public static void main(String[] args) {
        List<Cat> cats = new ArrayList<>();
        cats.add(new Cat());
        cats.add(new Cat());

        // iterateAnimals(cats); ERROR
        iterateAnimalsWildcard(cats);
    }
}
