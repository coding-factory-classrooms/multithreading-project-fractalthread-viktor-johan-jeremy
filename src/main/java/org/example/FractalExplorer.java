package org.example;
 /*
  A Fractal Explorer created in Java
  @author Notre equipe de 3 professionnels Jeremy Viktor et Johan
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.List;


public class FractalExplorer{

	private final BufferedImage fractalImage;
	private final ArrayList<ThreadTask.MyLine> mylines = new ArrayList<>();

	private static final String PATH_PICTURE = "./src/main/resources/static/img/";
	private static final String PICTURE_NAME = "mandelbrot.png";

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


	/**
	 * Updates the fractal by computing the number of iterations
	 * for each point in the fractal and changing the color
	 * based on that.
	 **/

	public void updateFractal() {
		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		long start = System.currentTimeMillis();
		
		List<Future<ThreadTask.MyLine>> futures = new ArrayList<>();
		for (int x = 0; x < width; x++ ) {
			ThreadTask task = new ThreadTask(x,topLeftX,topLeftY,zoomFactor,1,height);
			Future<ThreadTask.MyLine> future = threadPool.submit(task);
			futures.add(future);
		}

		threadPool.shutdown();

		for (Future<ThreadTask.MyLine> future : futures){
			try {
				ThreadTask.MyLine myline = null;
				try {
					myline = future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				if (myline != null) {
					mylines.add(myline.getIndex(), myline);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		long elapsed = System.currentTimeMillis() - start;
		System.out.println(elapsed + " ms");

		BufferedImage[] buffImages = new BufferedImage[width];
		for (ThreadTask.MyLine line:
			 mylines) {
			buffImages[line.getIndex()] = line.getImage();
		}
		//Initializing the final image
		for (int i = 0; i < width; i++) {
			fractalImage.createGraphics().drawImage(buffImages[i], i, 0, null);
		}

		updatePNG(fractalImage);

	} // updateFractal

	public void updatePNG(BufferedImage fractalImage){
		try {
			ImageIO.write(fractalImage, "png", new File(PATH_PICTURE+PICTURE_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // updatePNG

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

	} // adjustZoom

	public void requestZoomPicture(double x,double y, int action){
		if(action == 0){
			adjustZoom(x,y,zoomFactor/2);
		}
		else{
			adjustZoom(x,y,zoomFactor*2);
		}

	}

	public void requestMovePicture(String type){
		switch (type) {
			case "up": {
				moveUp();
				break;
			}
			case "down": {
				moveDown();
				break;
			}
			case "right": {
				moveRight();
				break;
			}
			default: {
				moveLeft();
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
























