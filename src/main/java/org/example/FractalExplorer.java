package org.example;
 /*
  A Fractal Explorer created in Java
  @author Notre equipe de 3 professionnels Jeremy Viktor et Johan
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FractalExplorer{
	
	private final BufferedImage fractalImage;
	
	private static final int MAX_ITER = 1000;

	private static final String PATH_PICTURE = "./src/main/resources/static/img/mandelbrot.png";

	private static int width;
	private static int height;

	double zoomFactor;
	double topLeftX;
	double topLeftY;

	public FractalExplorer(double topx, double topy, double zoomFactor, int width, int height) {
		this.topLeftX = topx;
		this.topLeftY = topy;
		FractalExplorer.width = width;
		FractalExplorer.height = height;
		this.zoomFactor = zoomFactor;
		this.fractalImage = new BufferedImage(FractalExplorer.width, FractalExplorer.height, BufferedImage.TYPE_INT_RGB);
	}

// -------------------------------------------------------------------
	private double getXPos(double x) {
		return x/zoomFactor + topLeftX;
	} // getXPos
// -------------------------------------------------------------------
	private double getYPos(double y) {
		return y/zoomFactor - topLeftY;
	} // getYPos
// -------------------------------------------------------------------


	/**
	 * Updates the fractal by computing the number of iterations
	 * for each point in the fractal and changing the color
	 * based on that.
	 **/

	public void updateFractal() {
		System.out.println("update");
		for (int x = 0; x < width; x++ ) {
			for (int y = 0; y < height; y++ ) {
				
				double c_r = getXPos(x);
				double c_i = getYPos(y);
				
				int iterCount = computeIterations(c_r, c_i);
				
				int pixelColor = makeColor(iterCount);
				fractalImage.setRGB(x, y, pixelColor);
				
			}
		}
		System.out.println("update : "+topLeftX+" and "+topLeftY+ " zoom " +zoomFactor);
		updatePNG(fractalImage);

	} // updateFractal

	public void updatePNG(BufferedImage fractalImage){
		try {
			ImageIO.write(fractalImage, "png", new File(PATH_PICTURE));
			System.out.println("image : "+ PATH_PICTURE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // updatePNG

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


// -------------------------------------------------------------------
	private void moveUp() {
		double curHeight = height / zoomFactor;
		topLeftY += curHeight / 6;
	} // moveUp
// -------------------------------------------------------------------
	private void moveDown() {
		double curHeight = height / zoomFactor;
		topLeftY -= curHeight / 6;
	} // moveDown
// -------------------------------------------------------------------
	private void moveLeft() {
		double curWidth = width / zoomFactor;
		topLeftX -= curWidth / 6;
	} // moveLeft
// -------------------------------------------------------------------
	private void moveRight() {
		double curWidth = width / zoomFactor;
		topLeftX += curWidth / 6;
	} // moveRight
// -------------------------------------------------------------------		

	private void adjustZoom( double newX, double newY, double newZoomFactor ) {
		
		topLeftX += newX/zoomFactor;
		topLeftY -= newY/zoomFactor;
		
		zoomFactor = newZoomFactor;
		
		topLeftX -= (width /2) / zoomFactor;
		topLeftY += (height /2) / zoomFactor;
		System.out.println("adjust : "+topLeftX+" and "+topLeftY+ " zoom " +zoomFactor);

	} // adjustZoom

	public void requestZoomPicture(double x,double y, int action){
		System.out.println("click : "+topLeftX+" and "+topLeftY+ " zoom " +zoomFactor);
		if(action == 0){
			adjustZoom(x,y,zoomFactor/2);
		}
		else{
			adjustZoom(x,y,zoomFactor*2);
		}

	}

	public void requestMovePicture(String type){
		System.out.println("request move with type = " +type);
		switch (type) {
			case "up": {
				moveUp();
				System.out.println("move up");
				break;
			}
			case "down": {
				moveDown();
				System.out.println("move down");
				break;
			}
			case "right": {
				moveRight();
				System.out.println("move right");
				break;
			}
			default: {
				moveLeft();
				System.out.println("move left");
				break;
			}
		}
	}
	public double getZoomFactor() {
		return zoomFactor;
	}

	public double getTopLeftX() {
		return topLeftX;
	}

	public double getTopLeftY() {
		return topLeftY;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public BufferedImage getFractalImage() {
		return fractalImage;
	}

	@Override
	public String toString() {
		return "FractalExplorer{" +
				"fractalImage=" + fractalImage +
				", zoomFactor=" + zoomFactor +
				", topLeftX=" + topLeftX +
				", topLeftY=" + topLeftY +
				'}';
	}

	public String provideKey(){
		return ""+zoomFactor+topLeftX+topLeftY;
	}
} // FractalExplorer
























