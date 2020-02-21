public class Test {

    public static void main(String[] args) {
        System.out.println(31/2);
    }

    public static void main2(String[] args) {
        int len = "/ws2/5000/".length();
        String string = "sad/ws2/5000/dsadasdsadasd sada";
        int i = string.indexOf("/ws2/5000/");

        System.out.println(string.replaceAll("/ws2/5000/", "/ws2/5000/".substring(0, len - 5)));
    }
}
