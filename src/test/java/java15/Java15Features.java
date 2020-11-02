package java15;

import org.junit.Test;

public class Java15Features {

    /**
     * Java 15 introduce [sealed] class just like Scala had, to create super classes that must be extended
     * by subclass. It also contains a cool feature which is [permits] where as a sealed class, only allow to be
     * extended by some specific classes.
     *
     * Here in order to make this whole bound perfect, Javas introduce [non-sealed], which means
     * The subclass must extend a [sealed] class
     */
    public sealed class MySealedClass permits SealedClassExtension {
        public String helloSealedWorld = "hello world";
    }

    public non-sealed class SealedClassExtension extends MySealedClass {
        public void init(){
            System.out.println(helloSealedWorld);
        }
    }

    @Test
    public void sealedFeature(){
        new SealedClassExtension().init();
    }



}
