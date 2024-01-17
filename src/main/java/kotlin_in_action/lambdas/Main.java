package kotlin_in_action.lambdas;

public class Main {
    public static void main(String[] args) {
        Button button = new Button();

        // создание анонимного класса
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick() {
                /* действия по щелчку */
            }
        });
        // Лямбда после Java 8:
//        button.setOnClickListener(() -> {
//            /* действия по щелчку */
//        });
    }
}
