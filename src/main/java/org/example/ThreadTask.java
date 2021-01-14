package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTask implements Callable<ThreadTask.MyLine> {
    private final BufferedImage fractalImage;
    private static int width;
    private static int height;
    private final int index;
    private static final int MAX_ITER = 1000;
    double zoomFactor;
    double topLeftX;
    double topLeftY;

    ReentrantLock lock = new ReentrantLock();


    public ThreadTask(int index,double topx, double topy, double zoomFactor, int width, int height){
        this.index = index;
        this.topLeftX = topx;
        this.topLeftY = topy;
        ThreadTask.width = width;
        ThreadTask.height = height;
        this.zoomFactor = zoomFactor;
        this.fractalImage = new BufferedImage(ThreadTask.width, ThreadTask.height, BufferedImage.TYPE_INT_RGB);
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
    public MyLine call() {
        lock.lock();
        for (int y = 0; y < height; y++ ) {
            double c_r = getXPos(index);
            double c_i = getYPos(y);
            int iterCount = computeIterations(c_r, c_i);
            int pixelColor = makeColor(iterCount);
            fractalImage.setRGB(0, y, pixelColor);
        }
        MyLine myLine = new MyLine(fractalImage,width,height,index);
       //System.out.println(myLine.toString()+"\n");
        lock.unlock();
        return myLine;
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

    @Override
    public String toString() {
        return "ThreadTask{" +
                "fractalImage=" + fractalImage +
                ", zoomFactor=" + zoomFactor +
                ", topLeftX=" + topLeftX +
                ", topLeftY=" + topLeftY +
                '}';
    }

    public static class MyLine {
        private final BufferedImage image;
        private final int lineWidth;
        private final int lineHeight;
        private final int index;

        public MyLine(BufferedImage image,int lineWidth,int lineHeight,int index){
            this.image = image;
            this.lineWidth=lineWidth;
            this.lineHeight=lineHeight;
            this.index=index;

        }

        public BufferedImage getImage() {
            return image;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "MyLine{" +
                    "image=" + image +
                    ", lineWidth=" + lineWidth +
                    ", lineHeight=" + lineHeight +
                    ", index=" + index +
                    '}';
        }
    }
}
