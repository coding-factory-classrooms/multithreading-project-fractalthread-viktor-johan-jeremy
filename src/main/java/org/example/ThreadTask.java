package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ThreadTask implements Runnable{
    private final BufferedImage fractalImage;
    private static int width;
    private static int height;
    private static int x;
    private static int y;
    private static final int MAX_ITER = 1000;
    double zoomFactor;
    double topLeftX;
    double topLeftY;
    public static Object Testing;
    public ThreadTask(int x,int y,double topx, double topy, double zoomFactor, int width, int height){
        this.x = x;
        this.y = y;
        this.topLeftX = topx;
        this.topLeftY = topy;
        ThreadTask.width = width;
        ThreadTask.height = height;
        this.zoomFactor = zoomFactor;
        this.fractalImage = new BufferedImage(ThreadTask.width, ThreadTask.height, BufferedImage.TYPE_INT_RGB);
    }

    public static Object getTesting() {
        return Testing;
    }

    public static void setTesting(Object test) {
        Testing = test;
    }


    // -------------------------------------------------------------------
    public double getXPos(double x) {
        return x/zoomFactor + topLeftX;
    } // getXPos
    // -------------------------------------------------------------------
    public double getYPos(double y) {
        return y/zoomFactor - topLeftY;
    } // getYPos
    // -------------------------------------------------------------------
    @Override
    public void run() {
        System.out.println("x: "+x+"width: "+width+"height"+height);
        for (y = 0; y < height; y++ ) {
            double c_r = getXPos(x);
            double c_i = getYPos(y);
            int iterCount = computeIterations(c_r, c_i);
            int pixelColor = makeColor(iterCount);
            fractalImage.setRGB(x, y, pixelColor);
            Line line = new Line(fractalImage,width,height,x);
            BufferedImage image = fractalImage;
            int width = ThreadTask.width;
            int height = ThreadTask.height;
            setTesting(line);
        }
        System.out.println("update : "+topLeftX+" and "+topLeftY+ " zoom " +zoomFactor);
    }
    // -------------------------------------------------------------------
    /** Returns a posterized color based off of the iteration count
     of a given point in the fractal **/
    private int makeColor( int iterCount ) {

        int color = 0b011011100001100101101000;
        int mask  = 0b000000000000010101110111;
        int shiftMag = iterCount / 13;

        if (iterCount == MAX_ITER)
            return Color.BLACK.getRGB();

        return color | (mask << shiftMag);

    } // makeColor

    // -------------------------------------------------------------------
    private int computeIterations(double c_r, double c_i) {

        double z_r = 0.0;
        double z_i = 0.0;

        int iterCount = 0;

        while ( z_r*z_r + z_i*z_i <= 4.0 ) {

            double z_r_tmp = z_r;

            z_r = z_r*z_r - z_i*z_i + c_r;
            z_i = 2*z_i*z_r_tmp + c_i;

            // Point was inside the Mandelbrot set
            if (iterCount >= MAX_ITER)
                return MAX_ITER;

            iterCount++;

        }

        // Complex point was outside Mandelbrot set
        return iterCount;

    } // computeIterations
}
