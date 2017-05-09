package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * 播放3D音效
 * @author yanmaoyuan
 *
 */
public class HelloAudio extends SimpleApplication {

	/**
	 * 枪声
	 */
	private AudioNode audioGun;
	/**
	 * 环境音效
	 */
	private AudioNode audioNature;

	public static void main(String[] args) {
		HelloAudio app = new HelloAudio();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(40);

		/**
		 * 制作一个蓝色的小方块，用它来表示3D音源的位置。
		 */
	    Geometry player = new Geometry("Player", new Box(1, 1, 1));
	    Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
	    mat1.setColor("Color", ColorRGBA.Blue);
	    player.setMaterial(mat1);
	    rootNode.attachChild(player);

	    /** custom init methods, see below */
	    initKeys();
	    initAudio();

	}

	/** We create two audio nodes. */
	  private void initAudio() {
	    /* gun shot sound is to be triggered by a mouse click. */
	    audioGun = new AudioNode(assetManager, "Sound/Effects/Gun.wav", DataType.Buffer);
	    audioGun.setPositional(false);
	    audioGun.setLooping(false);
	    audioGun.setVolume(2);
	    rootNode.attachChild(audioGun);

	    /* nature sound - keeps playing in a loop. */
	    audioNature = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", DataType.Stream);
	    audioNature.setLooping(true);  // activate continuous playing
	    audioNature.setPositional(true);
	    audioNature.setVolume(3);
	    rootNode.attachChild(audioNature);
	    audioNature.play(); // play continuously!
	  }

	  /** Declaring "Shoot" action, mapping it to a trigger (mouse left click). */
	  private void initKeys() {
	    inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	    inputManager.addListener(actionListener, "Shoot");
	  }

	  /** Defining the "Shoot" action: Play a gun sound. */
	  private ActionListener actionListener = new ActionListener() {
	    @Override
	    public void onAction(String name, boolean keyPressed, float tpf) {
	      if (name.equals("Shoot") && !keyPressed) {
	        audioGun.playInstance(); // play each instance once!
	      }
	    }
	  };

	  /** Move the listener with the a camera - for 3D audio. */
	  @Override
	  public void simpleUpdate(float tpf) {
	    listener.setLocation(cam.getLocation());
	    listener.setRotation(cam.getRotation());
	  }
}
