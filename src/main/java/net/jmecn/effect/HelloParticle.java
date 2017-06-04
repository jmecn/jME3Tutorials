package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * 使用粒子发射器实现火焰。
 * 
 * @author yanmaoyuan
 *
 */
public class HelloParticle extends SimpleApplication {

    public static void main(String[] args) {
        HelloParticle app = new HelloParticle();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // 加载材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        
        // 粒子发射器
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        fire.setMaterial(mat);
        
        // 设置2x2的动画
        fire.setImagesX(2);
        fire.setImagesY(2); // 2x2 texture animation
        
        // 初始颜色，结束颜色
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f)); // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        
        // 初始大小，结束大小
        fire.setStartSize(1.5f);
        fire.setEndSize(0.1f);
        
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        
        // 重力
        fire.setGravity(0, 0, 0);
        
        fire.setLowLife(1f);
        fire.setHighLife(3f);
        
        
        rootNode.attachChild(fire);
    }
}