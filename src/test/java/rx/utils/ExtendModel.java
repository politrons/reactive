package rx.utils;

import org.junit.Test;

/**
 * Created by pabloperezgarcia on 27/11/2016.
 */
public class ExtendModel {


    public class A {
        protected String aString;
        private int anInt;

        public A(int anInt, String aString) {
            this.anInt = anInt;
            this.aString = aString;
        }
    }

    public class B extends A {
        public B(int i, String test) {
            super(i, test);
        }
    }


    @Test
    public void renderObject() {
        A a = new B(1, "test");
        System.out.println("Params from A:" + a.anInt +" " + a.aString);
        B b = (B) a;
        System.out.println("Params rfom B:" + b.aString);
    }

    @Test
    public void autoBox(){
        System.out.println(isEqual(421, 421));
        System.out.println(isEqual2(421, 421));

    }

    boolean isEqual(int x, int y) {
        return x == y;
    }
    boolean isEqual2(Integer x, Integer y) {
        return x == y;//Reference are different
    }
}


