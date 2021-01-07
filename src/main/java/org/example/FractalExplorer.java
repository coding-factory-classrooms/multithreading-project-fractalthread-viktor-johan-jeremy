package org.example; /**
 * A Fractal Explorer created in Java
 * @author William Fiset, william.alexandre.fiset@gmail.com
 **/

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FractalExplorer{
	
	static final int WIDTH  = 600;
	static final int HEIGHT = 600;
	
	Canvas canvas;
	BufferedImage fractalImage;
	
	static final int MAX_ITER = 200;
	
	static final double DEFAULT_ZOOM       = 100.0;
	static final double DEFAULT_TOP_LEFT_X = -3.0;
	static final double DEFAULT_TOP_LEFT_Y = +3.0;
	
	double zoomFactor = DEFAULT_ZOOM;
	double topLeftX   = DEFAULT_TOP_LEFT_X;
	double topLeftY   = DEFAULT_TOP_LEFT_Y;

	public FractalExplorer(double topLeftX, double topLeftY) {
		fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
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
		
		for (int x = 0; x < WIDTH; x++ ) {
			for (int y = 0; y < HEIGHT; y++ ) {
				
				double c_r = getXPos(x);
				double c_i = getYPos(y);
				
				int iterCount = computeIterations(c_r, c_i);
				
				int pixelColor = makeColor(iterCount);
				fractalImage.setRGB(x, y, pixelColor);
				
			}
		}

		canvas.repaint();
		savePNG(fractalImage);




	} // updateFractal

	public void savePNG(BufferedImage fractalImage){
		try {
			ImageIO.write(fractalImage, "png", new File("./src/main/java/org/example/pictures/mandelbrot_"+topLeftX+"_"+topLeftY+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		
		/*
		
		Let c = c_r + c_i
		Let z = z_r + z_i
		
		z' = z*z + c
		   = (z_r + z_i)(z_r + z_i) + (c_r + c_i)
		   = z_r² + 2*z_r*z_i - z_i² + c_r + c_i

		     z_r' = z_r² - z_i² + c_r
		     z_i' = 2*z_i*z_r + c_i
		     
		*/

		double z_r = 0.0;
		double z_i = 0.0;
		
		int iterCount = 0;

		// Modulus (distance) formula:
		// √(a² + b²) <= 2.0
		// a² + b² <= 4.0
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
		double curHeight = HEIGHT / zoomFactor;
		topLeftY += curHeight / 6;
		updateFractal();
	} // moveUp
// -------------------------------------------------------------------
	private void moveDown() {
		double curHeight = HEIGHT / zoomFactor;
		topLeftY -= curHeight / 6;
		updateFractal();
	} // moveDown
// -------------------------------------------------------------------
	private void moveLeft() {
		double curWidth = WIDTH / zoomFactor;
		topLeftX -= curWidth / 6;
		updateFractal();
	} // moveLeft
// -------------------------------------------------------------------
	private void moveRight() {
		double curWidth = WIDTH / zoomFactor;
		topLeftX += curWidth / 6;
		updateFractal();
	} // moveRight
// -------------------------------------------------------------------		

	private void adjustZoom( double newX, double newY, double newZoomFactor ) {
		
		topLeftX += newX/zoomFactor;
		topLeftY -= newY/zoomFactor;
		
		zoomFactor = newZoomFactor;
		
		topLeftX -= ( WIDTH/2) / zoomFactor;
		topLeftY += (HEIGHT/2) / zoomFactor;
		
		updateFractal();
		
	} // adjustZoom

} // FractalExplorer
























