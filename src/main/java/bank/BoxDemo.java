package bank;

public class BoxDemo {

    public static void main(String[] args) {
        Box stringBox = new Box();
        stringBox.set("Java");
        String text = (String) stringBox.get();
        System.out.println("String: " + text);

        Box numberBox = new Box();
        numberBox.set(123);

        try {
            String number = (String) numberBox.get();
            System.out.println("Number: " + number);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }
}
