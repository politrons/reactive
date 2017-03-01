package good_practices;

import org.junit.Test;

/**
 * Created by pabloperezgarcia on 01/03/2017.
 *
 * As per the LSP, derived classes must be substitutable for the base class
 * In this case Square which extends Rectangle must be possible be reference as rectangle
 * and continue working.
 */
public class LiskovSubstitutionPrinciple {

    class Rectangle {
        private int length;
        private int breadth;

        Rectangle(int length, int breadth) {
            this.length = length;
            this.breadth = breadth;
        }

        int getArea() {
            return this.length * this.breadth;
        }
    }

    /**
     * Square class; Square inherits from Rectangle;
     * Represents ISA relationship - Square is a Rectangle
     *
     */
    public class Square extends Rectangle {
        Square(int length, int breadth) {
            super(length, breadth);
        }
    }

    @Test
    public void calcAreaTest() {
        assert calculateArea(new Rectangle(2, 4)) == 8 : "Error";
        assert calculateArea(new Square(2, 2)) == 4 : "Error";
    }

    /**
     * calculate area method prove the LSP since it´s receiving just rectangles but we manage to pass a
     * square and works as a rectangle
     */
    private int calculateArea(Rectangle r) {
        return r.getArea();
    }

}
