package kotlin_in_action.types;

import java.util.List;

public class TypesJavaExample {

    public int strLen(String s) {
        return s.length();
    }

    public static List<String> uppercaseAll(List<String> items) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, items.get(i).toUpperCase());
        }
        return items;
    }
}
