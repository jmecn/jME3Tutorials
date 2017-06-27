/*
 * Copyright (c) 2012-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.jmecn.texture;

import com.jme3.font.BitmapCharacter;
import com.jme3.font.BitmapCharacterSet;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tim
 */
public class ImagePainter {

    public static enum BlendMode {

        /**
         * Ignore the original color and just set the color of the pixel to the
         * incoming value..
         */
        SET {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = r;
                color.g = g;
                color.b = b;
                color.a = a;
            }

            @Override
            protected boolean needsOriginal(float a) {
                return false;
            }
        },
        /**
         * Add the original color and the new color based on the transparency of
         * the new color. This will lighten the image. Original Alpha is
         * increased by incoming alpha.
         */
        /**
         * Mix the original color with the new color based on the transparency
         * of the new color Original Alpha is merged with the incoming alpha.
         */
        NORMAL {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = color.r * (1 - a) + r * a;
                color.g = color.g * (1 - a) + g * a;
                color.b = color.b * (1 - a) + b * a;
                color.a = color.a * (1 - a) + a;
            }

            @Override
            protected boolean needsOriginal(float a) {
                return a < 1;
            }
        },
        /**
         * Add the original color and the new color based on the transparency of
         * the new color. This will lighten the image. Original Alpha is
         * increased by incoming alpha.
         */
        ADD {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = color.r + r * a;
                color.g = color.g + g * a;
                color.b = color.b + b * a;
                color.a = color.a + a;
            }

            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
        },
        /**
         * Subtract the new color from the original color based on the
         * transparency of the new color. This will darken the image. Original
         * Alpha is increased by incoming alpha.
         */
        SUBTRACT {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = color.r - r * a;
                color.g = color.g - g * a;
                color.b = color.b - b * a;
                color.a = color.a + a;
            }
 
            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
       },
        /**
         * Mix the original color with the new color based on the transparency
         * of the new color If the resulting channel (r,g,b are processed
         * separately) would be darker than the original then keep the original.
         * Original Alpha is left unchanged.
         */
        LIGHTEN_ONLY {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = Math.max(color.r * (1 - a) + r * a, color.r);
                color.g = Math.max(color.g * (1 - a) + g * a, color.g);
                color.b = Math.max(color.b * (1 - a) + b * a, color.b);
            }

            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
        },
        /**
         * Mix the original color with the new color based on the transparency
         * of the new color If the resulting channel (r,g,b are processed
         * separately) would be lighter than the original then keep the
         * original. Original Alpha is left unchanged.
         */
        DARKEN_ONLY {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = Math.min(color.r * (1 - a) + r * a, color.r);
                color.g = Math.min(color.g * (1 - a) + g * a, color.g);
                color.b = Math.min(color.b * (1 - a) + b * a, color.b);
            }

            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
        },
        /**
         * Mix the original color with the new color by multiplying them
         * together (r,g,b are processed separately). This will tend to darken
         * the image. Original Alpha is left unchanged.
         */
        MULTIPLY {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = color.r * (1 - a) + (r * color.r) * a;
                color.g = color.g * (1 - a) + (g * color.g) * a;
                color.b = color.b * (1 - a) + (b * color.b) * a;
            }

            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
        },
        /**
         * Mix the original color with the new color by multiplying the inverses
         * together (r,g,b are processed separately). This will tend to lighten
         * the image. Original Alpha is left unchanged.
         */
        SCREEN {
            @Override
            protected void apply(ColorRGBA color, float r, float g, float b, float a) {
                color.r = color.r * (1 - a) + (1 - (1 - r) * (1 - color.r)) * a;
                color.g = color.g * (1 - a) + (1 - (1 - g) * (1 - color.g)) * a;
                color.b = color.b * (1 - a) + (1 - (1 - b) * (1 - color.b)) * a;
            }

            @Override
            protected boolean needsOriginal(float a) {
                return true;
            }
        };

        /**
         * Used to apply this blend mode to a pixel. Note that out of range
         * values are accepted at this point. Anything greater than 1 or less
         * than 0 will be clipped when the texture is generated.
         */
        protected abstract void apply(ColorRGBA color, float r, float g, float b, float a);
        
        protected abstract boolean needsOriginal(float a);
    }
    
    private final Image image;
    private final ImageRaster imageRaster;
    private final ColorRGBA working = new ColorRGBA();

    /**
     * Constructs a new ImagePainter that will modify the supplied ImageRaster.
     *
     * @param imageRaster The image raster to paint into
     */
    public ImagePainter(ImageRaster imageRaster) {
        this.imageRaster = imageRaster;
        this.image = null;
    }

    /**
     * Constructs a new ImagePainter that will modify the supplied Image. An
     * ImageRaster is created to wrap the Image automatically.
     *
     * @param image The image to wrap with an ImageRaster then paint into
     */
    public ImagePainter(Image image) {
        this.imageRaster = ImageRaster.create(image);
        this.image = image;
    }

    /**
     * Constructs a new ImagePainter that will modify the supplied ImageRaster.
     * It also stores the Image passed in and allows it to be queried using
     * getImage, for people who later want to be able to associate an Image with
     * this ImagePainter.
     *
     * @param imageRaster The image raster to paint into
     */
    public ImagePainter(ImageRaster imageRaster, Image image) {
        this.imageRaster = imageRaster;
        this.image = image;
    }

    /**
     * Creates a new Image in the specified format with the specified width and
     * height and then creates a new ImagePainter that will paint into that
     * Image. The format must be one that supports setPixel.
     *
     * @param format The format to create the Image in
     * @param width The width of the Image to create
     * @param height The height of the Image to create
     */
    public ImagePainter(Format format, int width, int height) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * format.getBitsPerPixel() / 8);
        this.image = new Image(format, width, height, buffer, ColorSpace.Linear);
        this.imageRaster = ImageRaster.create(image, 0);
    }

    /**
     * Returns the image that this ImagePainter is painting into. Note that if
     * this ImagePainter was constructed using an ImageRaster rather than
     * creating a new Image then the return value will be null.
     *
     * @return The Image being painted into
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns the ImageRaster that this ImagePainter is painting into.
     *
     * @return The ImageRaster being painted into
     */
    public ImageRaster getImageRaster() {
        return imageRaster;
    }

    /**
     * Wipes the Image so that every pixel contains the supplied color.
     *
     * @param color The color to wipe the image too
     */
    public void wipe(ColorRGBA color) {
        for (int y = 0; y < imageRaster.getHeight(); y++) {
            for (int x = 0; x < imageRaster.getWidth(); x++) {
                imageRaster.setPixel(x, y, color);
            }
        }
    }

    /**
     * Sets the specified area within the Image to the specified color. The
     * rectangle will be clipped to the bounds of the Image.
     *
     * @param startX The X coordinate of the bottom left corner of the rectangle
     * @param startY The Y coordinate of the bottom left corner of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color to set the area too.
     */
    public void setRect(int startX, int startY, int width, int height, ColorRGBA color) {
        int endX = startX + width;
        int endY = startY + height;
        if (startX < 0) {
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endX > imageRaster.getWidth()) {
            endX = imageRaster.getWidth();
        }
        if (endY > imageRaster.getHeight()) {
            endY = imageRaster.getHeight();
        }

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                imageRaster.setPixel(x, y, color);
            }
        }
    }

    /**
     * Paints the specified area within the Image with the specified color. The
     * rectangle will be clipped to the bounds of the Image.
     *
     * @param startX The X coordinate of the bottom left corner of the rectangle
     * @param startY The Y coordinate of the bottom left corner of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color to paint the area with.
     * @param mode The blend mode to use for painting the rectangle
     */
    public final void paintRect(int startX, int startY, int width, int height, ColorRGBA color, BlendMode mode) {
        int endX = startX + width;
        int endY = startY + height;
        if (startX < 0) {
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endX > imageRaster.getWidth()) {
            endX = imageRaster.getWidth();
        }
        if (endY > imageRaster.getHeight()) {
            endY = imageRaster.getHeight();
        }

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                paintPixel(x, y, color, mode);
            }
        }
    }
    
    /**
     * Paints a straight line between two points on the ImageRaster. This is not clipped
     * so all points must be greater than width/2 from the edges of the ImageRaster. It
     * copes with lines in any direction including negative x and y.
     * 
     * The edges of the line are anti-aliased and it copes with non-integer (even smaller
     * than 1 although the results may not be ideal) widths. Currently there is no
     * special handling of the end of the line and it just just cut off either vertically
     * or horizontally so wide lines may need some work at the ends to tidy them up.
     * 
     * @param x1 The x co-ordinate of the start of the line
     * @param y1 The y co-ordinate of the start of the line
     * @param x2 The x co-ordinate of the end of the line
     * @param y2 The y co-ordinate of the end of the line
     * @param width The width to paint the line at (in pixels)
     * @param color The color with which to draw the line
     * @param mode The blend mode with which to draw the line
     */
    public void paintLine(int x1, int y1, int x2, int y2, float width, ColorRGBA color, BlendMode mode) {
        int xWidth = x2 - x1;
        int yHeight = y2 - y1;
        working.set(color);

        // Is the line more horizontal or more vertical?
        if (FastMath.abs(xWidth) > FastMath.abs(yHeight)) {
            float yStep = (float)yHeight / xWidth;
            // Trig to work out the angle needed then rotate the width accordingly
            // Using atan not atan2 as we don't actually need the sign of the result
            float angle = FastMath.HALF_PI + FastMath.atan(yStep);
            float rotatedHeight = width/FastMath.sin(angle);
            
            int xStep = xWidth < 0 ? -1 : 1;
            float y = y1 - rotatedHeight*0.5f;

            for (int x = x1; x != x2; x+=xStep, y+=yStep) {

                // Draw bottom pixel at correct opacity
                working.a = color.a*(1-(y-(int)y));
                paintPixel(x, (int)y, working, mode);
                
                float remainder = y+rotatedHeight;

                // Draw center full opacity pixels
                for (int h=(int)y+1;h<remainder-1;h++) {
                    paintPixel(x, h, color, mode);
                }

                // Narrow lines (width < 1) may draw on same pixel twice which makes the
                // line look a little dotty but we ned to handle that case somehow...
                working.a = color.a*(remainder-(int)remainder);
                paintPixel(x, (int)(remainder), working, mode);
            }
        } else {
            
            float xStep = (float)xWidth / yHeight;
            // Trig to work out the angle needed then rotate the width accordingly
            // Using atan not atan2 as we don't actually need the sign of the result
            float rotatedWidth = width/FastMath.sin(FastMath.HALF_PI + FastMath.atan(xStep));
            int yStep = yHeight < 0 ? -1 : 1;
            float x = x1 - rotatedWidth*0.5f;

            for (int y = y1; y != y2; x+=xStep, y+=yStep) {

                // Draw bottom pixel at correct opacity
                working.a = color.a*(1-(x-(int)x));
                paintPixel((int)x, y, working, mode);
                
                float remainder = x+rotatedWidth;

                // Draw center full opacity pixels
                for (int w=(int)x+1;w<remainder-1;w++) {
                    paintPixel(w, y, color, mode);
                }

                // Narrow lines (width < 1) may draw on same pixel twice which makes the
                // line look a little dotty but we ned to handle that case somehow...
                working.a = color.a*(remainder-(int)remainder);
                paintPixel((int)(remainder), y, working, mode);
            }
            
        }
    }

    /**
     * Paints the specified area within the Image with the specified gradient.
     * The color for all four corners of the rectangle is specified and the
     * paint process interpolates between them. The rectangle will be clipped to
     * the bounds of the Image.
     *
     * @param startX The X coordinate of the bottom left corner of the rectangle
     * @param startY The Y coordinate of the bottom left corner of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param topLeft The color for the top left corner of the rectangle
     * @param topRight The color for the top right corner of the rectangle
     * @param bottomLeft The color for the bottom left corner of the rectangle
     * @param bottomRight The color for the bottom right corner of the rectangle
     * @param mode The blend mode to use for painting the gradient into the
     * image
     */
    public void paintGradient(int startX, int startY, int width, int height, ColorRGBA topLeft, ColorRGBA topRight, ColorRGBA bottomLeft, ColorRGBA bottomRight, BlendMode mode) {
        int endX = startX + width;
        int endY = startY + height;
        if (startX < 0) {
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endX > imageRaster.getWidth()) {
            endX = imageRaster.getWidth();
        }
        if (endY > imageRaster.getHeight()) {
            endY = imageRaster.getHeight();
        }

        for (int y = startY; y < endY; y++) {
            float yRatio = ((float) (y - startY)) / height;
            for (int x = startX; x < endX; x++) {
                float xRatio = ((float) (x - startX)) / width;
                working.a =
                        (topLeft.a * (1 - xRatio) + topRight.a * (xRatio)) * (1 - yRatio)
                        + (bottomLeft.a * (1 - xRatio) + bottomRight.a * (xRatio)) * (yRatio);
                working.b =
                        (topLeft.b * (1 - xRatio) + topRight.b * (xRatio)) * (1 - yRatio)
                        + (bottomLeft.b * (1 - xRatio) + bottomRight.b * (xRatio)) * (yRatio);
                working.g =
                        (topLeft.g * (1 - xRatio) + topRight.g * (xRatio)) * (1 - yRatio)
                        + (bottomLeft.g * (1 - xRatio) + bottomRight.g * (xRatio)) * (yRatio);
                working.r =
                        (topLeft.r * (1 - xRatio) + topRight.r * (xRatio)) * (1 - yRatio)
                        + (bottomLeft.r * (1 - xRatio) + bottomRight.r * (xRatio)) * (yRatio);
                paintPixel(x, y, working, mode);
            }
        }
    }
    private final ColorRGBA paintWorking = new ColorRGBA();

    /**
     * This method paints the specified pixel using the specified blend mode.
     *
     * @param x The x coordinate to paint (0 is at the left of the image)
     * @param y The y coordinate to paint (0 is at the bottom of the image)
     * @param color The color to paint with
     * @param mode The blend mode to use
     */
    public void paintPixel(int x, int y, ColorRGBA color, BlendMode mode) {
        if (color.a <= 0)
            return;
        
        if (mode.needsOriginal(color.a)) {
            imageRaster.getPixel(x, y, paintWorking);
        }
        mode.apply(paintWorking, color.r, color.g, color.b, color.a);
        imageRaster.setPixel(x, y, paintWorking);
    }

    /**
     * This method paints the specified pixel using the specified blend mode,
     * first checking if it lies within the raster or not.
     *
     * @param x The x coordinate to paint (0 is at the left of the image)
     * @param y The y coordinate to paint (0 is at the bottom of the image)
     * @param color The color to paint with
     * @param mode The blend mode to use
     */
    protected void paintPixelCheckingBounds(int x, int y, ColorRGBA color, BlendMode mode) {
        if (    (x < 0) 
                || (x >= imageRaster.getWidth())
                || (y < 0)
                || (y >= imageRaster.getHeight())) {
            return;
        }
        paintPixel(x, y, color, mode);
    }

    /**
     * This method paints a copy of the source image into the image being
     * painted.
     *
     * @param startX The x coordinate of the bottom left corner from which to
     * start painting.
     * @param startY The y coordinate at the bottom left corner from which to
     * start painting.
     * @param source The source image.
     * @param mode The blending mode
     * @param opacity The opacity with which to paint the image.
     */
    public void paintImage(int startX, int startY, ImageRaster source, BlendMode mode, float opacity) {

        // Calculate clipping
        int lineOffset = -startX;
        if (lineOffset < 0) {
            lineOffset = 0;
        }

        int xLimit = startX + source.getWidth();
        if (xLimit > imageRaster.getWidth()) {
            xLimit = imageRaster.getWidth();
        }

        int rowOffset = -startY;
        if (rowOffset < 0) {
            rowOffset = 0;
        }

        int yLimit = startY + source.getHeight();
        if (yLimit > imageRaster.getHeight()) {
            yLimit = imageRaster.getHeight();
        }

        // Draw pixels
        for (int y = startY + rowOffset; y < yLimit; y++) {
            for (int x = startX + lineOffset; x < xLimit; x++) {
                source.getPixel(x - startX, y - startY, working);
                working.a *= opacity;
                paintPixel(x, y, working, mode);
            }

        }
    }

    /**
     * This method paints a scaled copy of the specified image into the image
     * being painted. This method does support negative width and height which
     * will cause the painted image to be flipped in the x and/or y planes.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the scaled Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the scaled Image
     * @param width Width to paint into
     * @param height Height to paint into
     * @param source The source image
     * @param mode The blending mode
     * @param opacity The opacity to paint the image at
     */
    public void paintStretchedImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, float opacity) {
        paintStretchedSubImage(startX, startY, width, height, source, mode, opacity, 0, 0, source.getWidth(), source.getHeight());
    }

    /**
     * This method paints a scaled copy of a section of the specified image into
     * the image being painted. This method does support negative width and
     * height which will cause the painted image to be flipped in the x and/or y
     * planes.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the partial scaled Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the partial scaled Image
     * @param width Width to paint into
     * @param height Height to paint into
     * @param source The source image
     * @param mode The blending mode
     * @param opacity The opacity to paint the image at
     * @param srcX The starting X in the source image
     * @param srcY The starting Y in the source image
     * @param srcWidth The amount of the width of the source image to use
     * @param srcHeight The amount of the height of the source image to use
     */
    public void paintStretchedSubImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, float opacity,
            int srcX, int srcY, int srcWidth, int srcHeight) {

        if (width < 0) {
            startX += width;
            width = -width;
            srcX += srcWidth - 1;
            srcWidth = 1 - srcWidth;
        }
        if (height < 0) {
            startY += height;
            height = -height;
            srcY += srcHeight - 1;
            srcHeight = 1 - srcHeight;
        }

        // Calculate clipping
        int lineOffset = -startX;
        if (lineOffset < 0) {
            lineOffset = 0;
        }

        int xLimit = startX + width;
        if (xLimit > imageRaster.getWidth()) {
            xLimit = imageRaster.getWidth();
        }

        int rowOffset = -startY;
        if (rowOffset < 0) {
            rowOffset = 0;
        }

        int yLimit = startY + height;
        if (yLimit > imageRaster.getHeight()) {
            yLimit = imageRaster.getHeight();
        }


        // Draw pixels
        for (int y = startY + rowOffset; y < yLimit; y++) {

            for (int x = startX + lineOffset; x < xLimit; x++) {

                // Read four source pixels and mix according to how "close" to each one we are
                float readX = srcX + ((float) (x - startX) * (srcWidth - 1)) / (width);
                float readY = srcY + ((float) (y - startY) * (srcHeight - 1)) / (height);

                ColorRGBA merged = getBilinearColor(source, readX, readY);
                merged.a *= opacity;
                if (mode.needsOriginal(merged.a)) {
                   imageRaster.getPixel(x, y, working);
                }
                mode.apply(working, merged.r, merged.g, merged.b, merged.a);
                imageRaster.setPixel(x, y, working);
            }

        }
    }

    /**
     * This method paints a scaled copy of a section of the specified image into
     * the image being painted.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the partial scaled Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the partial scaled Image
     * @param width Width to paint into
     * @param height Height to paint into
     * @param source The source image
     * @param mode The blending mode
     * @param srcX The starting X in the source image
     * @param srcY The starting Y in the source image
     * @param srcWidth The amount of the width of the source image to use
     * @param srcHeight The amount of the height of the source image to use
     */
    public void paintStretchedSubImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, ColorRGBA color,
            int srcX, int srcY, int srcWidth, int srcHeight) {

        // Calculate clipping
        int lineOffset = -startX;
        if (lineOffset < 0) {
            lineOffset = 0;
        }

        int xLimit = startX + width;
        if (xLimit > imageRaster.getWidth()) {
            xLimit = imageRaster.getWidth();
        }

        int rowOffset = -startY;
        if (rowOffset < 0) {
            rowOffset = 0;
        }

        int yLimit = startY + height;
        if (yLimit > imageRaster.getHeight()) {
            yLimit = imageRaster.getHeight();
        }


        // Draw pixels
        for (int y = startY + rowOffset; y < yLimit; y++) {

            for (int x = startX + lineOffset; x < xLimit; x++) {

                // Read four source pixels and mix according to how "close" to each one we are
                float readX = srcX + ((float) (x - startX) * (srcWidth - 1)) / (width);
                float readY = srcY + ((float) (y - startY) * (srcHeight - 1)) / (height);

                ColorRGBA merged = getBilinearColor(source, readX, readY);
                if (mode.needsOriginal(merged.a * color.a)) {
                    imageRaster.getPixel(x, y, working);
                }
                mode.apply(working, merged.r * color.r, merged.g * color.g, merged.b * color.b, merged.a * color.a);
                imageRaster.setPixel(x, y, working);
            }

        }
    }

    /**
     * This method paints a section of a source Image into the painted Image.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the partial Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the partial Image
     * @param width The width to paint
     * @param height The height to paint
     * @param source The source Image
     * @param mode The blend mode to use for painting
     * @param opacity The opacity with which to paint
     * @param srcX The x position of the starting point in the source Image
     * @param srcY The y position of the starting point in the source Image
     */
    public void paintSubImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, float opacity,
            int srcX, int srcY) {

        // Calculate clipping
        int lineOffset = -startX;
        if (lineOffset < 0) {
            lineOffset = 0;
        }

        int xLimit = startX + width;
        if (xLimit > imageRaster.getWidth()) {
            xLimit = imageRaster.getWidth();
        }

        int rowOffset = -startY;
        if (rowOffset < 0) {
            rowOffset = 0;
        }

        int yLimit = startY + height;
        if (yLimit > imageRaster.getHeight()) {
            yLimit = imageRaster.getHeight();
        }

        // Draw pixels
        for (int y = startY + rowOffset; y < yLimit; y++) {
            for (int x = startX + lineOffset; x < xLimit; x++) {
                source.getPixel(x - startX + srcX, y - startY + srcY, working);
                working.a *= opacity;
                paintPixel(x, y, working, mode);
            }

        }
    }

    /**
     * This method paints a section of a source Image into the painted Image,
     * multiplying each pixel in the source Image by the specified ColorRGBA
     * before painting.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the partial Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the partial Image
     * @param width The width to paint
     * @param height The height to paint
     * @param source The source Image
     * @param mode The blend mode to use for painting
     * @param srcX The x position of the starting point in the source Image
     * @param srcY The y position of the starting point in the source Image
     */
    public void paintSubImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, ColorRGBA color,
            int srcX, int srcY) {

        // Calculate clipping
        int lineOffset = -startX;
        if (lineOffset < 0) {
            lineOffset = 0;
        }

        int xLimit = startX + width;
        if (xLimit > imageRaster.getWidth()) {
            xLimit = imageRaster.getWidth();
        }

        int rowOffset = -startY;
        if (rowOffset < 0) {
            rowOffset = 0;
        }

        int yLimit = startY + height;
        if (yLimit > imageRaster.getHeight()) {
            yLimit = imageRaster.getHeight();
        }

        // Draw pixels
        for (int y = startY + rowOffset; y < yLimit; y++) {
            for (int x = startX + lineOffset; x < xLimit; x++) {
                source.getPixel(x - startX + srcX, y - startY + srcY, working);
                working.r *= color.r;
                working.g *= color.g;
                working.b *= color.b;
                working.a *= color.a;
                paintPixel(x, y, working, mode);
            }

        }
    }

    /**
     * This method paints a border Image. This is similar to a 9-patch in
     * Android or the border ImageMode in Nifty. The corners are not scaled at
     * all, the sides are stretched in the direction of the side and the center
     * is scaled in all directions. This is mostly useful for UI elements.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the border Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the border Image
     * @param width The width to draw the border image in
     * @param height The height to draw the border image in
     * @param source The source Image.
     * @param mode The Blend Mode
     * @param opacity The opacity with which to draw the Image
     * @param bottom The size of the border to leave at the bottom
     * @param top The size of the border to leave at the top
     * @param left The size of the border to leave at the left
     * @param right The size of the border to leave at the right
     */
    public void paintBorderImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, float opacity,
            int bottom, int top, int left, int right) {

        if (startX + left > 0) {
            if (startY + bottom > 0) {
                paintSubImage(startX, startY, left, bottom, source, mode, opacity, 0, 0);
            }
            int y = startY + bottom;
            paintStretchedSubImage(startX, y, left, height - bottom - top, source, mode, opacity, 0, bottom, left, source.getHeight() - bottom - top);

            y = startY + height - top;
            if (y < imageRaster.getHeight()) {
                paintSubImage(startX, y, left, top, source, mode, opacity, 0, source.getHeight() - top);
            }
        }

        int y = startY + bottom;
        int x = startX + left;
        int w = width - left - right;
        if (y > 0) {
            paintStretchedSubImage(x, startY, w, bottom, source, mode, opacity, left, 0, source.getWidth() - left - right, bottom);
        }
        paintStretchedSubImage(x, y, w, height - bottom - top, source, mode, opacity, left, bottom, source.getWidth() - left - right, source.getHeight() - bottom - top);

        y = startY + height - top;
        if (y < imageRaster.getHeight()) {
            paintStretchedSubImage(x, y, w, top, source, mode, opacity, left, source.getHeight() - top, source.getWidth() - left - right, top);
        }

        x = startX + width - right;
        if (x < imageRaster.getWidth()) {
            if (startY + bottom > 0) {
                paintSubImage(x, startY, right, bottom, source, mode, opacity, source.getWidth() - right, 0);
            }
            y = startY + bottom;
            paintStretchedSubImage(x, y, right, height - bottom - top, source, mode, opacity, source.getWidth() - right, bottom, right, source.getHeight() - bottom - top);

            y = startY + height - top;
            if (y < imageRaster.getHeight()) {
                paintSubImage(x, y, right, top, source, mode, opacity, source.getWidth() - right, source.getHeight() - top);
            }
        }

    }

    /**
     * This method paints a border Image. This is similar to a 9-patch in
     * Android or the border ImageMode in Nifty. The corners are not scaled at
     * all, the sides are stretched in the direction of the side and the center
     * is not drawn at all. This is mostly useful for UI elements.
     *
     * @param startX Starting X of the bottom left corner of the location to
     * paint the border Image
     * @param startY Starting Y of the bottom left corner of the location to
     * paint the border Image
     * @param width The width to draw the border image in
     * @param height The height to draw the border image in
     * @param source The source Image.
     * @param mode The Blend Mode
     * @param opacity The opacity with which to draw the Image
     * @param bottom The size of the border to leave at the bottom
     * @param top The size of the border to leave at the top
     * @param left The size of the border to leave at the left
     * @param right The size of the border to leave at the right
     */
    public void paintBorderOnlyImage(int startX, int startY, int width, int height, ImageRaster source, BlendMode mode, float opacity,
            int bottom, int top, int left, int right) {

        if (startX + left > 0) {
            if (startY + bottom > 0) {
                paintSubImage(startX, startY, left, bottom, source, mode, opacity, 0, 0);
            }
            int y = startY + bottom;
            paintStretchedSubImage(startX, y, left, height - bottom - top, source, mode, opacity, 0, bottom, left, source.getHeight() - bottom - top);

            y = startY + height - top;
            if (y < imageRaster.getHeight()) {
                paintSubImage(startX, y, left, top, source, mode, opacity, 0, source.getHeight() - top);
            }
        }

        int y = startY + bottom;
        int x = startX + left;
        int w = width - left - right;
        if (y > 0) {
            paintStretchedSubImage(x, startY, w, bottom, source, mode, opacity, left, 0, source.getWidth() - left - right, bottom);
        }

        y = startY + height - top;
        if (y < imageRaster.getHeight()) {
            paintStretchedSubImage(x, y, w, top, source, mode, opacity, left, source.getHeight() - top, source.getWidth() - left - right, top);
        }

        x = startX + width - right;
        if (x < imageRaster.getWidth()) {
            if (startY + bottom > 0) {
                paintSubImage(x, startY, right, bottom, source, mode, opacity, source.getWidth() - right, 0);
            }
            y = startY + bottom;
            paintStretchedSubImage(x, y, right, height - bottom - top, source, mode, opacity, source.getWidth() - right, bottom, right, source.getHeight() - bottom - top);

            y = startY + height - top;
            if (y < imageRaster.getHeight()) {
                paintSubImage(x, y, right, top, source, mode, opacity, source.getWidth() - right, source.getHeight() - top);
            }
        }

    }

    /**
     * Paints the source Image rotated clockwise by angle into the Image being
     * painted.
     *
     * @param centerX The x coordinate of the center of the location to paint
     * the rotated Image
     * @param centerY The y coordinate of the center of the location to paint
     * the rotated Image
     * @param source The Image to paint rotated
     * @param mode The blend mode to use for painting the Image
     * @param opacity The opacity with which to paint the Image
     * @param angle The angle in Radians by which to rotate the Image clockwise.
     */
    public void paintRotatedImage(int centerX, int centerY, ImageRaster source, BlendMode mode, float opacity, float angle) {

        Vector2f boundsChecker = new Vector2f((source.getWidth()) / 2f, (source.getHeight()) / 2f);
        rotate(boundsChecker, boundsChecker, angle);
        int xStart = (int) boundsChecker.x;
        int xEnd = (int) boundsChecker.x;
        int yStart = (int) boundsChecker.y;
        int yEnd = (int) boundsChecker.y;

        boundsChecker.set(-(source.getWidth()) / 2f, (source.getHeight()) / 2f);
        rotate(boundsChecker, boundsChecker, angle);
        if (boundsChecker.x < xStart) {
            xStart = (int) boundsChecker.x;
        }
        if (boundsChecker.x > xEnd) {
            xEnd = (int) boundsChecker.x;
        }
        if (boundsChecker.y < yStart) {
            yStart = (int) boundsChecker.y;
        }
        if (boundsChecker.y > yEnd) {
            yEnd = (int) boundsChecker.y;
        }

        boundsChecker.set((source.getWidth()) / 2f, -(source.getHeight()) / 2f);
        rotate(boundsChecker, boundsChecker, angle);
        if (boundsChecker.x < xStart) {
            xStart = (int) boundsChecker.x;
        }
        if (boundsChecker.x > xEnd) {
            xEnd = (int) boundsChecker.x;
        }
        if (boundsChecker.y < yStart) {
            yStart = (int) boundsChecker.y;
        }
        if (boundsChecker.y > yEnd) {
            yEnd = (int) boundsChecker.y;
        }

        boundsChecker.set(-(source.getWidth()) / 2f, -(source.getHeight()) / 2f);
        rotate(boundsChecker, boundsChecker, angle);
        if (boundsChecker.x < xStart) {
            xStart = (int) boundsChecker.x;
        }
        if (boundsChecker.x > xEnd) {
            xEnd = (int) boundsChecker.x;
        }
        if (boundsChecker.y < yStart) {
            yStart = (int) boundsChecker.y;
        }
        if (boundsChecker.y > yEnd) {
            yEnd = (int) boundsChecker.y;
        }

        xStart += centerX - 1;
        yStart += centerY - 1;
        xEnd += centerX + 1;
        yEnd += centerY + 1;

        Vector2f xStep = new Vector2f(0, 1);
        rotate(xStep, xStep, angle);
        Vector2f yStep = new Vector2f(1, xStart - xEnd);
        rotate(yStep, yStep, angle);

        Vector2f sourcePos = new Vector2f((xStart - xEnd) / 2f, (yStart - yEnd) / 2f);
        rotate(sourcePos, sourcePos, angle);
        sourcePos.addLocal((source.getWidth()) / 2f, (source.getHeight()) / 2f);

        if (yStart < 0) {
            sourcePos.x += xStep.x * (-yStart * (xEnd - xStart)) + yStep.x * (-yStart);
            sourcePos.y += xStep.y * (-yStart * (xEnd - xStart)) + yStep.y * (-yStart);
        }

        for (int y = (yStart < 0 ? 0 : yStart); y < yEnd && y < imageRaster.getHeight(); y++) {

            if (xStart < 0) {
                sourcePos.x += xStep.x * (-xStart);
                sourcePos.y += xStep.y * (-xStart);
            }

            for (int x = (xStart < 0 ? 0 : xStart); x < xEnd; x++) {

                sourcePos.addLocal(xStep);
                if (x >= imageRaster.getWidth() || y < 0) {
                    sourcePos.x += xStep.x * (xEnd - x - 1);
                    sourcePos.y += xStep.y * (xEnd - x - 1);
                    break;
                }

                ColorRGBA color = getBilinearColor(source, sourcePos.x, sourcePos.y);
                if (color.a > 0) {
                    color.a *= opacity;
                    paintPixel(x, y, color, mode);
                }
            }
            sourcePos.addLocal(yStep);
        }
    }

    /**
     * It is safe to use store and input as the same Vector2f
     *
     * @param input
     * @param store
     * @param theta
     * @return
     */
    private Vector2f rotate(Vector2f input, Vector2f store, float theta) {
        // Use -theta to rotate clockwise instead of anticlockwise
        float sinTheta = FastMath.sin(-theta);
        float cosTheta = FastMath.cos(-theta);
        float x = input.x * cosTheta - input.y * sinTheta;
        store.y = input.x * sinTheta + input.y * cosTheta;
        store.x = x;
        return store;
    }
    private ColorRGBA[] srcPixels = new ColorRGBA[]{
        new ColorRGBA(),
        new ColorRGBA(),
        new ColorRGBA(),
        new ColorRGBA(),};

    private ColorRGBA getBilinearColor(ImageRaster source, float x, float y) {

        int xI = (int) x;
        int yI = (int) y;
        x -= xI;
        y -= yI;

        readBilinearPixel(source, xI, yI, srcPixels[0]);
        readBilinearPixel(source, xI + 1, yI, srcPixels[1]);
        readBilinearPixel(source, xI, yI + 1, srcPixels[2]);
        readBilinearPixel(source, xI + 1, yI + 1, srcPixels[3]);

        mergecolor(srcPixels[0], srcPixels[2], y);
        mergecolor(srcPixels[1], srcPixels[3], y);
        mergecolor(srcPixels[0], srcPixels[1], x);

        return srcPixels[0];
    }

    private void readBilinearPixel(ImageRaster source, int x, int y, ColorRGBA dest) {
        if (x < 0 || x >= source.getWidth() || y < 0 || y >= source.getHeight()) {
            dest.set(-1, -1, -1, 0);
        } else {
            dest.set(source.getPixel(x, y));
        }
    }

    private void mergecolor(ColorRGBA store, ColorRGBA merge, float balance) {
        if (store.r == -1 && merge.r == -1) {
            store.r = -1;
        } else if (store.r == -1) {
            store.r = merge.r;
        } else if (merge.r != -1) {
            store.r = store.r * (1 - balance) + merge.r * (balance);
        }

        if (store.g == -1 && merge.g == -1) {
            store.g = -1;
        } else if (store.g == -1) {
            store.g = merge.g;
        } else if (merge.g != -1) {
            store.g = store.g * (1 - balance) + merge.g * (balance);
        }

        if (store.b == -1 && merge.b == -1) {
            store.b = -1;
        } else if (store.b == -1) {
            store.b = merge.b;
        } else if (merge.b != -1) {
            store.b = store.b * (1 - balance) + merge.b * (balance);
        }

        store.a = store.a * (1 - balance) + merge.a * (balance);
    }

    public enum TextHAlign {

        Left {
            @Override
            int calcXOffsetFor(int space, int height) {
                return 0;
            }
        },
        Center {
            @Override
            int calcXOffsetFor(int space, int height) {
                return (space - height) / 2;
            }
        },
        Right {
            @Override
            int calcXOffsetFor(int space, int height) {
                return space - height;
            }
        };

        abstract int calcXOffsetFor(int space, int height);
    }

    public enum TextVAlign {

        /**
         * Place the text at the top of the box
         */
        Top {
            @Override
            int calcYOffsetFor(int space, int height) {
                return space - height;
            }
        },
        /**
         * Place the text two thirds of the way up the box. This can look better
         * than centering, particularly for blocks of text in a larger area.
         */
        Elevated {
            @Override
            int calcYOffsetFor(int space, int height) {
                return ((space - height) * 2) / 3;
            }
        },
        /**
         * Center the text
         */
        Center {
            @Override
            int calcYOffsetFor(int space, int height) {
                return (space - height) / 2;
            }
        },
        /**
         * Place the text at the bottom of the box.
         */
        Bottom {
            @Override
            int calcYOffsetFor(int space, int height) {
                return 0;
            }
        };

        abstract int calcYOffsetFor(int space, int height);
    }

    /**
     * Paints the given text into the box specified by the first four
     * parameters. It uses the specified font, positions the text in the box
     * according to the alignment and paints the text in the given color and
     * with the given blend mode. The text is not clipped to the bounds of the
     * box, although it is clipped to the bounds of the Image.
     *
     * @param x The x coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param y The y coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param width The width of the box to paint the text into
     * @param height The height of the box to paint the text into
     * @param font The font to use to paint the text
     * @param text The text to paint
     * @param hAlign The horizontal alignment of the text within the box
     * @param vAlign The vertical alignment of the text within the box
     * @param color The color to paint the text with
     * @param blendMode The blend mode to use for painting the text
     */
    public void paintTextLine(int x, int y, int width, int height, BitmapFont font, String text, TextHAlign hAlign, TextVAlign vAlign, ColorRGBA color, BlendMode blendMode) {
        if (text.isEmpty()) {
            return;
        }

        BitmapCharacterSet charSet = font.getCharSet();

        BitmapCharacter[] letters = new BitmapCharacter[text.length()];

        int textDrawWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            BitmapCharacter c = charSet.getCharacter(text.charAt(i));
            letters[i] = c;
            if (c != null) {
                textDrawWidth += c.getXAdvance();
            }
        }
        BitmapCharacter last = letters[text.length() - 1];
        if (last != null) {
            textDrawWidth += last.getWidth() - last.getXAdvance();
        }

        x += hAlign.calcXOffsetFor(width, textDrawWidth);
        y += vAlign.calcYOffsetFor(height, charSet.getLineHeight());

        for (BitmapCharacter c : letters) {
            if (c != null) {
                Image source = font.getPage(c.getPage()).getTextureParam("ColorMap").getTextureValue().getImage();
                paintSubImage(
                        x + c.getXOffset(), y - c.getYOffset() + charSet.getLineHeight() - c.getHeight(),
                        c.getWidth(), c.getHeight(),
                        ImageRaster.create(source),
                        blendMode, color,
                        c.getX(), (source.getHeight() - c.getY()) - c.getHeight());

                x += c.getXAdvance();
            }
        }
    }

    /**
     * Paints the given text into the box specified by the first four
     * parameters. It uses the specified font, positions the text in the box
     * according to the alignment and paints the text in the given colour and
     * with the given blend mode. The text is word-wrapped at the horizontal
     * extents of the box. Each line is independently aligned horizontally and
     * then the block as a whole is aligned vertically. It is not clipped
     * vertically to the bounds of the box, although it is clipped to the bounds
     * of the Image.
     *
     * @param x The x coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param y The y coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param width The width of the box to paint the text into
     * @param height The height of the box to paint the text into
     * @param font The font to use to paint the text
     * @param text The text to paint
     * @param hAlign The horizontal alignment of the text within the box
     * @param vAlign The vertical alignment of the text within the box
     * @param color The colour to paint the text with
     * @param blendMode The blend mode to use for painting the text
     */
    public void paintTextArea(int x, int y, int width, int height, BitmapFont font, String text, TextHAlign hAlign, TextVAlign vAlign, ColorRGBA color, BlendMode blendMode) {
        BitmapCharacterSet charSet = font.getCharSet();

        int textDrawWidth = 0;
        List<Integer> lineStarts = new ArrayList<Integer>();

        for (int i = 0; i < text.length(); i++) {
            BitmapCharacter c = charSet.getCharacter(text.charAt(i));
            if (text.charAt(i) == '\n') {
                textDrawWidth = 0;
                lineStarts.add(i + 1);

            } else if (c != null) {
                if (textDrawWidth + c.getWidth() >= width) {
                    int startSearch = i;
                    while (!Character.isWhitespace(text.charAt(i)) && textDrawWidth > width / 2) {
                        i--;
                        c = charSet.getCharacter(text.charAt(i));
                        if (c != null) {
                            textDrawWidth -= c.getXAdvance();
                        }
                    }
                    if (!Character.isWhitespace(text.charAt(i))) {
                        i = startSearch - 1;
                    } else {
                        i++;
                    }
                    textDrawWidth = 0;
                    lineStarts.add(i);
                } else {
                    textDrawWidth += c.getXAdvance();
                }
            }
        }

        y += vAlign.calcYOffsetFor(height, charSet.getLineHeight() * (lineStarts.size() + 1));
        y += charSet.getLineHeight() * (lineStarts.size());

        int lastStart = 0;
        for (Integer start : lineStarts) {
            paintTextLine(x, y, width, charSet.getLineHeight(), font, text.substring(lastStart, start), hAlign, TextVAlign.Center, color, blendMode);
            y -= charSet.getLineHeight();
            lastStart = start;
        }
        paintTextLine(x, y, width, charSet.getLineHeight(), font, text.substring(lastStart, text.length()), hAlign, TextVAlign.Center, color, blendMode);
    }

    /**
     * Paints the given text into the box specified by the first four
     * parameters. It uses the specified font, positions the text in the box
     * according to the alignment and paints the text in the given color and
     * with the given blend mode.
     *
     * If the text would be too wide to fit into the box then it is scaled down
     * to fit.
     *
     * @param x The x coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param y The y coordinate of the bottom left corner of the box to draw
     * the text into.
     * @param width The width of the box to paint the text into
     * @param height The height of the box to paint the text into
     * @param font The font to use to paint the text
     * @param text The text to paint
     * @param hAlign The horizontal alignment of the text within the box
     * @param vAlign The vertical alignment of the text within the box
     * @param color The color to paint the text with
     * @param blendMode The blend mode to use for painting the text
     */
    public void paintFittedTextLine(int x, int y, int width, int height, BitmapFont font, String text, TextHAlign hAlign, TextVAlign vAlign, ColorRGBA color, BlendMode blendMode) {
        if (text.isEmpty()) {
            return;
        }

        BitmapCharacterSet charSet = font.getCharSet();

        BitmapCharacter[] letters = new BitmapCharacter[text.length()];

        int textDrawWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            BitmapCharacter c = charSet.getCharacter(text.charAt(i));
            letters[i] = c;
            if (c != null) {
                textDrawWidth += c.getXAdvance();
            }
        }
        BitmapCharacter last = letters[text.length() - 1];
        if (last != null) {
            textDrawWidth += last.getWidth() - last.getXAdvance();
        }

        float scale = 1;
        if (textDrawWidth > width) {
            scale = ((float) width) / textDrawWidth;

            textDrawWidth = 0;

            for (int i = 0; i < text.length(); i++) {
                BitmapCharacter c = charSet.getCharacter(text.charAt(i));
                letters[i] = c;
                if (c != null) {
                    textDrawWidth += c.getXAdvance() * scale;
                }
            }
        }

        x += hAlign.calcXOffsetFor(width, textDrawWidth);
        y += vAlign.calcYOffsetFor(height, (int) (charSet.getLineHeight() * scale));

        for (BitmapCharacter c : letters) {
            if (c != null) {
                Image source = font.getPage(c.getPage()).getTextureParam("ColorMap").getTextureValue().getImage();
                paintStretchedSubImage(
                        x + c.getXOffset(), (int) (y - (c.getYOffset() + charSet.getLineHeight() - c.getHeight()) * scale),
                        (int) (c.getWidth() * scale), (int) (c.getHeight() * scale),
                        ImageRaster.create(source),
                        blendMode, color,
                        c.getX(), (source.getHeight() - c.getY()) - c.getHeight(),
                        c.getWidth(), c.getHeight());

                x += c.getXAdvance() * scale;
            }
        }
    }

}