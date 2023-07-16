package com.shpp.p2p.cs.ykryvoruchko.assignment12;

import acm.graphics.GImage;

public class Assignment12Part1 {

    //to control dispersion of pixels
    static final int SILHOUETTE_DELTA = 5;

    private int width;
    private int height;
    private final boolean[][] pixels;

    public Assignment12Part1(GImage image) {
        this.pixels = toBooleanArray(image);
    }

    public static void main(String[] args) {
        GImage image;
        try{
            image = new GImage(args[0]);
        }catch (ArrayIndexOutOfBoundsException e){
            image = new GImage("test.jpg");
        }

        Assignment12Part1 assignment = new Assignment12Part1(image);
        System.out.println(assignment.findSilhouettes());
    }

    /**
     * Make picture to boolean array where black or white color true or false
     * @param image picture whith silhouettes
     * @return result boolean array */
    private boolean[][] toBooleanArray(GImage image) {
        int[][] pixels = image.getPixelArray();
        this.height = pixels.length;
        this.width = pixels[0].length;
        int whitePixel = GImage.createRGBPixel(255, 255, 255);
        boolean[][] result = new boolean[height][width];

        for (int row = 0; row < height; ++row) {
            for (int col = 0; col < width; ++col) {
                result[row][col] = pixels[row][col] != whitePixel;
            }
        }
        return result;
    }

    /**
     * Walk for picture
     * @return number of silhouettes
     * */
    private int findSilhouettes() {
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point point = new Point(j, i);
                if (isValidBlackPoint(point)) {
                    findSilhouetteDfs(point);
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Find neighbours
     * @param point to get pixel
     * */
    public void findSilhouetteDfs(Point point) {
        if (isWhitePoint(point)) {
            return;
        }

        pixels[point.getY()][point.getX()] = false;
        for (int i = -SILHOUETTE_DELTA; i <= SILHOUETTE_DELTA; i++) {
            for (int j = -SILHOUETTE_DELTA; j <= SILHOUETTE_DELTA; j++) {
                Point nextPoint = new Point(point, i, j);
                if (isValidPoint(nextPoint)) {
                    findSilhouetteDfs(nextPoint);
                }
            }
        }
    }


    private boolean isValidBlackPoint(Point point) {
        return isValidPoint(point)
                && pixels[point.getY()][point.getX()];
    }

    private boolean isWhitePoint(Point point) {
        return !pixels[point.getY()][point.getX()];
    }

    private boolean isValidPoint(Point point) {
        return point.getX() >= 0 && point.getX() < width
                && point.getY() >= 0 && point.getY() < height;
    }


    /**
     * Class to control pixels and dispersion */
    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(Point point, int deltaX, int deltaY) {
            this.x = point.x + deltaX;
            this.y = point.y + deltaY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}