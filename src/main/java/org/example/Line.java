package org.example;

import java.awt.image.BufferedImage;

public class Line {
    private BufferedImage image;
    private int lineWidth;
    private int lineHeight;


    public Line(BufferedImage image,int lineWidth,int lineHeight){
        this.image = image;
        this.lineWidth=lineWidth;
        this.lineHeight=lineHeight;

    }
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }


    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }
}
