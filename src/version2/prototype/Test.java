package version2.prototype;

public class Test {

    public static void main(String[] args) throws Exception {

        int a = 5;

        int b = method2(5) - method1(a);

        System.out.println( b);
    }

    public static int  method1(int a)
    {
        int b = 9;

        b = b * a;

        System.out.println( "method 1");

        return b;
    }

    public static int  method2(int a)
    {
        int b = 11;

        b = b * a;

        System.out.println( "method 2");

        return b;
    }
}
