package winter.di;

import winter.di.context.WinterDIContext;

public class Main {
    public static void main(String[] args) {
        var ctx = WinterDIContext.buildContext(Main.class);
        System.out.println("hello");
    }
}