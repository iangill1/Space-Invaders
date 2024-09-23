import java.awt.*;
import java.util.List;
import java.util.Iterator;
import java.awt.Image;

public class Alien extends Sprite2D{

    public Alien(Image i, Image j, Dimension WindowSize){
        //calling super to inherit from Sprite2D
        super(i, j, WindowSize);
    }

    public void move(boolean descend){
        x += xSpeed;
        if(descend){
            y += 20;
        }
    }

    public boolean checkCollision(List<PlayerBullet> bulletList){
        //check to see if an alien has collided with a bullet from bullet list
        Iterator<PlayerBullet> iterator = bulletList.iterator();
        while(iterator.hasNext()){
            PlayerBullet bullet = (PlayerBullet) iterator.next();
            if(isAlive() && ((x < bullet.x && x+iWidth > bullet.x) || (bullet.x < x && bullet.x + bullet.iWidth > x))
            && ((y < bullet.y && y+iHeight > bullet.y) || (bullet.y < y && bullet.y + bullet.iHeight > y))){
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean wallCollision(){
        if((x + 60) >= 800 || (x - 15) <=0){
            return true;
        } else{
            return false;
        }
    }
}
