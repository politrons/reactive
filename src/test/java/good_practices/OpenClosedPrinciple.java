package good_practices;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by pabloperezgarcia on 01/03/2017.
 * <p>
 * Open/Closed Principle It’s a principle for object oriented design where software entities
 * (classes, modules, functions, etc.) should be open for extension, but closed for modification
 */
public class OpenClosedPrinciple {

    /**
     * We initially create a Rectangle class, and later on I have the need to calc the area.
     * Shall I modify the class?, NO! that would break the O/C principle
     * I will create a new class that calculate that
     */
    public class Rectangle {

        double Width;
        double Height;

        Rectangle(double width, double height) {
            Width = width;
            Height = height;
        }
    }

    private double Area(Object... shapes) {
        double totalArea = 0;
        for (Object shape : shapes) {
            if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;
                totalArea += rectangle.Width * rectangle.Height;
            }
        }
        return totalArea;
    }

    @Test
    public void calcRectangle() {
        System.out.println("Total area:" + Area(Arrays.asList(new Rectangle(2, 2),
                new Rectangle(4, 2),
                new Rectangle(5, 2)).toArray()));
    }

}
