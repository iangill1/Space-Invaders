import java.awt.*;
public class Sprite2D {
    // member data
    protected double x,y, iHeight, iWidth;
    protected double xSpeed=0;
    protected double ySpeed = 0;
    private Image myImage, myImage2;
    private int winWidth, winHeight;
    public boolean isAlive = true;
    private int framesDrawn = 0;

    // constructor
    public Sprite2D(Image i,Image j, Dimension WindowSize) {
        myImage = i;
        myImage2 = j;
        winWidth = (int)WindowSize.getWidth();
        winHeight = (int)WindowSize.getHeight();

        iWidth = i.getWidth(null);
        iHeight = i.getHeight(null);
    }

    public Sprite2D(Image i, Dimension WindowSize) {
        myImage = i;
        myImage2 = i;
        winWidth = (int) WindowSize.getWidth();
        winHeight = (int) WindowSize.getHeight();

        iWidth = i.getWidth(null);
        iHeight = i.getHeight(null);
    }

    public void setPosition(double xx, double yy) {
        this.x=xx;
        this.y=yy;
    }

    public void setXSpeed(double dx) {
        this.xSpeed=dx;
    }

    public void paint(Graphics g){
        framesDrawn++;
        if(!isAlive){ //dont re-paint if alien is dead
            ;
        } else if(framesDrawn % 100 < 50){ //paint switching between images
            g.drawImage(this.myImage, (int)this.x, (int)this.y, null);
        } else{
            g.drawImage(this.myImage2, (int)this.x, (int)this.y, null);
        }
    }

    public boolean isAlive(){
        return isAlive;
    }
    public void setAlive(boolean bool){
        isAlive = bool;
    }
    public Dimension getDimension(){
        return new Dimension((int)this.x, (int)this.y);
    }

}