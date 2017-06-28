package net.jmecn.outscene;

public class GaussianBlur {
    
    private float[] kernel;
    private double sigma = 2;
    private float min = 0;
    private float max = 255;

    public GaussianBlur() {
        kernel = new float[0];
    }

    public void setSigma(double a) {
        this.sigma = a;
    }
    
    public void setClamp(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float[] filter(final float[] heightData, final int width, final int height) {
        
        final int size = width * height;
        
        makeGaussianKernel(sigma, 0.002, (int) Math.min(width, height));

        float[] temp = new float[size];
        blur(heightData, temp, width, height); // H Gaussian
        blur(temp, heightData, height, width); // V Gaussain

        return heightData;
    }

    /**
     * 
     * @param inHeights
     * @param outHeights
     * @param width
     * @param height
     */
    private void blur(float[] inHeights, float[] outHeights, int width, int height) {
        int subCol = 0;
        int index = 0, index2 = 0;
        float sum = 0;
        int k = kernel.length - 1;
        for (int row = 0; row < height; row++) {
            float c = 0;
            index = row;
            for (int col = 0; col < width; col++) {
                sum = 0;
                for (int m = -k; m < kernel.length; m++) {
                    subCol = col + m;
                    if (subCol < 0 || subCol >= width) {
                        subCol = 0;
                    }
                    index2 = row * width + subCol;
                    c = inHeights[index2];
                    sum += c * kernel[Math.abs(m)];
                }
                outHeights[index] = clamp(sum);
                index += height;
            }
        }
    }

    private float clamp(float height) {
        return (height < min) ? min : (height > max) ? max : height;
    }

    private void makeGaussianKernel(final double sigma, final double accuracy, int maxRadius) {
        int kRadius = (int) Math.ceil(sigma * Math.sqrt(-2 * Math.log(accuracy))) + 1;
        if (maxRadius < 50)
            maxRadius = 50; // too small maxRadius would result in inaccurate sum.
        if (kRadius > maxRadius)
            kRadius = maxRadius;
        kernel = new float[kRadius];
        for (int i = 0; i < kRadius; i++) // Gaussian function
            kernel[i] = (float) (Math.exp(-0.5 * i * i / sigma / sigma));
        double sum; // sum over all kernel elements for normalization
        if (kRadius < maxRadius) {
            sum = kernel[0];
            for (int i = 1; i < kRadius; i++)
                sum += 2 * kernel[i];
        } else
            sum = sigma * Math.sqrt(2 * Math.PI);

        for (int i = 0; i < kRadius; i++) {
            double v = (kernel[i] / sum);
            kernel[i] = (float) v;
        }
        return;
    }
}