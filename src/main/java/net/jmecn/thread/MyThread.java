package net.jmecn.thread;

public class MyThread extends Thread {

    private World world;
    private boolean running = true;

    public MyThread(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        long currentTime = System.nanoTime();
        long lastTime = 0;
        long deltaTime = 0;

        while(running) {
            lastTime = currentTime;
            currentTime = System.nanoTime();
            deltaTime = currentTime - lastTime;

            world.update(deltaTime * 0.000000001f);
        }
    }
    
    public void exit() {
        running = false;
    }
}
