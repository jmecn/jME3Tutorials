package net.jmecn.thread;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

public class World {

    Vector2f min;
    Vector2f max;
    
    List<BodyControl> bodys;
    
    public World() {
        min = new Vector2f(0, 0);
        max = new Vector2f(800, 600);
        bodys = new ArrayList<BodyControl>();
    }
    
    /**
     * 设置世界边界
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void setBounds(float x1, float y1, float x2, float y2) {
        float tmp;
        if (x1 > x2) {
            tmp = x2;
            x2 = x1;
            x1 = tmp;
        }
        
        if (y1 > y2) {
            tmp = y2;
            y2 = y1;
            y1 = tmp;
        }
        
        min.set(x1, y1);
        max.set(x2, y2);
    }

    public void update(float tpf) {
        int size = bodys.size();
        
        TempVars tmpVar = TempVars.get();
        
        Vector3f pos = tmpVar.vect1;
        Vector3f delta = tmpVar.vect2;
        
        for(int i=0; i<size; i++) {
            // move bodys
            BodyControl body = bodys.get(i);
            
            delta = body.velocity.mult(tpf, delta);
            pos = body.position.add(delta, pos);
            
            // check bounds
            if ((pos.x-body.radius) < min.x || (pos.x + body.radius) > max.x) {
                body.velocity.x = -body.velocity.x;
            }
            
            if ((pos.y-body.radius) < min.y || (pos.y + body.radius) > max.y) {
                body.velocity.y = -body.velocity.y;
            }
            
            body.position.set(pos);
        }
        tmpVar.release();
    }
    
    public void addBody(BodyControl body) {
        this.bodys.add(body);
    }
}
