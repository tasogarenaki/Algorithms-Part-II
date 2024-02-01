/**
 * Class representing a Seam Carver for image manipulation.
 */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private final Picture pic;
    private int width;
    private int height;

    /**
     * Constructs a SeamCarver object based on the given picture.
     *
     * @param picture The input picture for seam carving.
     * @throws IllegalArgumentException if the picture is null.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null.");
        }

        this.width = picture.width();
        this.height = picture.height();
        this.pic = new Picture(picture);
    }

    /**
     * Returns the current picture.
     *
     * @return The current picture.
     */
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                picture.set(x, y, pic.get(x, y));
            }
        }
        return picture;
    }

    /**
     * Returns the width of the current picture.
     *
     * @return The width of the current picture.
     */
    public int width() {
        return width;
    }

    /**
     * Returns the height of the current picture.
     *
     * @return The height of the current picture.
     */
    public int height() {
        return height;
    }

    /**
     * Computes the energy of a pixel at the specified coordinates.
     *
     * @param x The column index.
     * @param y The row index.
     * @return The energy value of the pixel.
     * @throws IllegalArgumentException if the indices are out of boundary.
     */
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("INdices are out of boundary.");
        }

        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER_ENERGY;
        }

        int deltaX = gradientSquare(x + 1, y, x - 1, y);
        int deltaY = gradientSquare(x, y + 1, x, y - 1);
        return Math.sqrt(deltaX + deltaY);
    }

    /**
     * Computes the square of the gradient between two pixels.
     *
     * @param x1 Column index of the first pixel.
     * @param y1 Row index of the first pixel.
     * @param x2 Column index of the second pixel.
     * @param y2 Row index of the second pixel.
     * @return The square of the gradient.
     */
    private int gradientSquare(int x1, int y1, int x2, int y2) {
        Color color1 = pic.get(x1, y1);
        Color color2 = pic.get(x2, y2);

        int red = color1.getRed() - color2.getRed();
        int green = color1.getGreen() - color2.getGreen();
        int blue = color1.getBlue() - color2.getBlue();

        return (int) (Math.pow(red, 2) + Math.pow(green, 2) + Math.pow(blue, 2));
    }

    /**
     * Finds a horizontal seam in the current picture.
     *
     * @return An array of indices representing the horizontal seam.
     */
    public int[] findHorizontalSeam() {
        double[][] totalE = new double[2][height];
        int[][] parent = new int[width][height];

        for (int y = 0; y < height; y++) {
            totalE[0][y] = BORDER_ENERGY;
            parent[0][y] = y;   // pointer to store index
        }

        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double temp = totalE[(x - 1) % 2][y];
                parent[x][y] = y;

                if (y > 0 && totalE[(x - 1) % 2][y - 1] < temp) {
                    temp = totalE[(x - 1) % 2][y - 1];
                    parent[x][y] = y - 1;
                }

                if (y < height - 1 && totalE[(x - 1) % 2][y + 1] < temp) {
                    temp = totalE[(x - 1) % 2][y + 1];
                    parent[x][y] = y + 1;
                }
                totalE[x % 2][y] = energy(x, y) + temp;
            }
        }

        int index = 0;
        for (int y = 1; y < height; y++) {
            if (totalE[(width - 1) % 2][y] < totalE[(width - 1) % 2][index]) {
                index = y;
            }
        }

        int[] seam = new int[width];
        seam[width - 1] = index;
        for (int x = width - 2; x >= 0; x--) {
            seam[x] = parent[x + 1][index];
            index = parent[x + 1][index];
        }
        return seam;
    }

    /**
     * Finds a vertical seam in the current picture.
     *
     * @return An array of indices representing the vertical seam.
     */
    public int[] findVerticalSeam() {
        double[][] totalE = new double[width][2];
        int[][] parent = new int[width][height];

        for (int x = 0; x < width; x++) {
            totalE[x][0] = BORDER_ENERGY;
            parent[x][0] = x;   // pointer to store index
        }

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double temp = totalE[x][(y - 1) % 2];
                parent[x][y] = x;

                if (x > 0 && totalE[x - 1][(y - 1) % 2] < temp) {
                    temp = totalE[x - 1][(y - 1) % 2];
                    parent[x][y] = x - 1;
                }

                if (x < width - 1 && totalE[x + 1][(y - 1) % 2] < temp) {
                    temp = totalE[x + 1][(y - 1) % 2];
                    parent[x][y] = x + 1;
                }
                totalE[x][y % 2] = energy(x, y) + temp;
            }
        }

        int index = 0;
        for (int x = 1; x < width; x++) {
            if (totalE[x][(height - 1) % 2] < totalE[index][(height - 1) % 2]) {
                index = x;
            }
        }

        int[] seam = new int[height];
        seam[height - 1] = index;
        for (int y = height - 2; y >= 0; y--) {
            seam[y] = parent[index][y + 1];
            index = parent[index][y + 1];
        }
        return seam;
    }

    /**
     * Removes a horizontal seam from the current picture.
     *
     * @param seam An array of indices representing the horizontal seam to be removed.
     * @throws IllegalArgumentException if the seam is illegal.
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) {
            throw new IllegalArgumentException("Seam is illegal.");
        }

        if (height <= 1) {
            throw new IllegalArgumentException("Height of the picture is less than or equal to 1");
        }

        for (int x = 0; x < width; ++x) {
            if (seam[x] < 0 || seam[x] >= height || (x > 0
                    && Math.abs(seam[x] - seam[x - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for (int y = seam[x]; y < height - 1; ++y) {
                pic.set(x, y, pic.get(x, y + 1));
            }
        }

        height--;
    }

    /**
     * Removes a vertical seam from the current picture.
     *
     * @param seam An array of indices representing the vertical seam to be removed.
     * @throws IllegalArgumentException if the seam is illegal.
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height) {
            throw new IllegalArgumentException("Seam is illegal.");
        }

        if (width <= 1) {
            throw new IllegalArgumentException("Width of the picture is less than or equal to 1");
        }

        for (int y = 0; y < height; ++y) {
            if (seam[y] < 0 || seam[y] >= width || (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for (int x = seam[y]; x < width - 1; ++x) {
                pic.set(x, y, pic.get(x + 1, y));
            }
        }

        width--;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
    }
}
