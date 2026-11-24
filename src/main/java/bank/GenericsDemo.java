package bank;

public class GenericsDemo {

    public static void main(String[] args) {
        Box<String> text = new Box<>();
        text.set("Java");
        String s = text.get();
        System.out.println("String: " + s);

        Box<Integer> number = new Box<>();
        number.set(42);
        Integer n = number.get();
        System.out.println("Integer: " + n);

        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        Pair<AccountNumber, String> owner = new Pair<>(
                new AccountNumber("1234567890"),
                "Ivan"
        );
        System.out.println("Pair: " + age);
        System.out.println("Pair: " + owner);

        String[] names = {"Ivan", "Petr", "Anna"};
        System.out.println("First: " + ArrayUtils.first(names));
        System.out.println("Last: " + ArrayUtils.last(names));
        System.out.println("Contains Petr: " + ArrayUtils.contains(names, "Petr"));
    }
}
