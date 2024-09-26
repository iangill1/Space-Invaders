import java.awt.Image;
import java.awt.*;

public class PlayerBullet extends Sprite2D {
    public PlayerBullet(Image i, Dimension WindowSize){
        super(i, WindowSize);
        ySpeed = 10;
    }

    public void move(){
        this.y -= ySpeed;
    }
    //remove bullet if it goes out of the window border
    public boolean pastBorder(){
        if(y <= 0){
            return true;
        } else{
            return false;
        }
    }
}
