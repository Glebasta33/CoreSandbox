package kotlin_in_action.types;

import java.util.List;

public interface DataParserJava {
    <T> void parseData(String input, List<T> output, List<String> errors);
}
