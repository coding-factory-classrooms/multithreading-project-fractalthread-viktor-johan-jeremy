package org.example;

import java.awt.image.BufferedImage;

public class Line {
    private BufferedImage image;
    private int lineWidth;
    private int lineHeight;
    private int index;

    public Line(BufferedImage image,int lineWidth,int lineHeight,int index){
        this.image = image;
        this.lineWidth=lineWidth;
        this.lineHeight=lineHeight;
        this.index=index;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
