import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    // member data
    private boolean isRunning, gameRunning = false; //i
    private static final int NUMALIENS = 30;
    private static String workingDirectory;
    private static boolean isGraphicsInitialised = false;

    private int deadAliens, redrawnCount, currentScore, highScore = 0;
    public double xSpeed = 4.5;
    public Image alienImage1, alienImage2, shipImage, bulletImage;
    public Alien[] AliensArray = new Alien[NUMALIENS];
    private Spaceship PlayerShip;
    public final List<PlayerBullet> bulletsList = new ArrayList<PlayerBullet>();
    private BufferStrategy strategy;
    private static final Dimension WindowSize = new Dimension(800, 600);

    // constructor
    public InvadersApplication() {
        // Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width / 2 - WindowSize.width / 2;
        int y = screensize.height / 2 - WindowSize.height / 2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
        this.setTitle("Space Invaders");

        //deal with buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // Load images
        ImageIcon icon = new ImageIcon(workingDirectory + "\\alien_ship_1.png");
        alienImage1 = icon.getImage();
        icon = new ImageIcon(workingDirectory + "\\alien_ship_2.png");
        alienImage2 = icon.getImage();
        icon = new ImageIcon(workingDirectory + "\\player_ship.png");
        shipImage = icon.getImage();
        icon = new ImageIcon(workingDirectory + "\\bullet.png");
        bulletImage = icon.getImage();

        // create and initialise some aliens, passing them each the image we have loaded
        for (int i = 0; i < NUMALIENS; i++) {
            AliensArray[i] = new Alien(alienImage1, alienImage2, WindowSize);
        }

        // create and initialise the player's spaceship
        PlayerShip = new Spaceship(shipImage, WindowSize);
        PlayerShip.setPosition(300, 530);

        startNewGame(); // start a new game

        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();
        isRunning = true;

        // send keyboard events arriving into this JFrame back to its own event handlers
        addKeyListener(this);
        isGraphicsInitialised = true; // it’s now safe to paint the images
    }

    public void startNewGame() { //start a new game method - resets variables, calls a new wave
        currentScore = 0;
        redrawnCount = 0;
        xSpeed = 4.5;
        startNewWave();
    }

    public void startNewWave() { //sets up wave - takes in counter of number of redraws as a multiplier
        if (redrawnCount > 0 && xSpeed > 0)
            xSpeed += 1.0;
        else if (redrawnCount > 0 && xSpeed < 0)
            xSpeed -= 1.0;

        for (int i = 0; i < NUMALIENS; i++) {
            AliensArray[i].setXSpeed(xSpeed);
            AliensArray[i].setAlive(true);
            // alien is 50 wide x 32 tall
            int xArray = ((i % 5) * 80) + 25;
            int yArray = (int) (java.lang.Math.floor(i / 5) * 45) + 40;

            AliensArray[i].setPosition(xArray, yArray); // set position
        }
    }

    // thread's entry point
    public void run() {
        boolean wallCollision = false; // applications general collision boolean - if any alien hits a wall it descends

        while (1 == 1) { // the game loop
            if (isRunning) { //if program is running
                try { // 1: sleep for 1/50 sec
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    System.out.println("lol");
                }
                if (gameRunning) { //if the game is running - 2: animate game objects
                    if (deadAliens >= NUMALIENS) { // if all aliens are dead
                        redrawnCount++; //new wave
                        startNewWave();
                        deadAliens = 0;
                    }

                    if (wallCollision) {
                        xSpeed = -xSpeed; // reversing alien direction of travel if they hit a wall
                    }

                    for (int i = 0; i < NUMALIENS; i++) {
                        if (AliensArray[i].checkCollision(bulletsList)) { //if there is a bullet collision
                            AliensArray[i].setAlive(false); //kill alien
                            deadAliens++;
                            currentScore += 10;
                            if (currentScore > highScore) //setting current score + updating highscore if needed
                                highScore = currentScore;
                        }
                        AliensArray[i].setXSpeed(xSpeed); // letting all aliens change direction of travel at once
                        // calling alien to move, and passing in if there was a collision - used to prompt alien to descend
                        AliensArray[i].move(wallCollision);
                        //if the aliens reach the y of the player - game over
                        if (AliensArray[i].y > PlayerShip.y - 32 && AliensArray[i].isAlive()) {
                            gameRunning = false;// Game over
                            startNewGame();
                        }

                    }
                    wallCollision = false; // resetting the collision boolean as all aliens are moving away from collision
                    // for all aliens - if there hasn't been a collision already - we check if there
                    // is any collisions detected
                    for (int i = 0; i < NUMALIENS; i++) {
                        if (!wallCollision && AliensArray[i].isAlive()) {
                            wallCollision = AliensArray[i].wallCollision();
                        }
                    }

                    Iterator iterator = bulletsList.iterator(); //move bullets
                    while (iterator.hasNext()) {
                        PlayerBullet b = (PlayerBullet) iterator.next();
                        b.move();
                    }
                    PlayerShip.move(); //move spaceship
                }
                this.repaint(); // 3: force an application repaint
            } else {
                this.repaint(); // 3: force an application repaint
            }

        }
    }

    // Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
        if (!gameRunning) { //press space to start the game
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameRunning = true;
                startNewGame();
            }
        } else { //otherwise there is movement controls for the ship or shoot bullet
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                PlayerShip.setXSpeed(-4);
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                PlayerShip.setXSpeed(4);
            else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                shootBullet();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
            PlayerShip.setXSpeed(0);
    }

    public void keyTyped(KeyEvent e) {
    }

    //
    // application's paint method
    public void paint(Graphics g) {
        if (isGraphicsInitialised) { // don’t try to draw uninitialized objects! - clear the canvas with a big
            if (!gameRunning) { //when there is no game running - do menu
                g = strategy.getDrawGraphics(); // redirecting drawing calls to offscreen buffer

                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WindowSize.width, WindowSize.height);
                g.setColor(Color.WHITE);
                g.drawString("SPACE INVADERS", 350, 200);
                g.drawString("PRESS  S P A C E  TO BEGIN", 330, 320);
                strategy.show(); // flip the buffers

            } else if (gameRunning) { //if there is a game
                g = strategy.getDrawGraphics(); // redirecting drawing calls to offscreen buffer
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WindowSize.width, WindowSize.height);

                // redraw all game objects
                for (int i = 0; i < NUMALIENS; i++)
                    AliensArray[i].paint(g);
                PlayerShip.paint(g);

                Iterator iterator = bulletsList.iterator();
                while (iterator.hasNext()) {
                    PlayerBullet b = (PlayerBullet) iterator.next();
                    b.paint(g);
                }

                // printing scores and waves
                g.setColor(Color.WHITE);
                g.drawString("Wave: " + (redrawnCount + 1), 720, 50);
                g.drawString("Current score: " + currentScore, 20, 50);
                g.drawString("High score: " + highScore, 20, 70);

                strategy.show(); // flip the buffers
            }
        }

    }

    public void shootBullet() {
        // add a new bullet to our list
        PlayerBullet b = new PlayerBullet(bulletImage, WindowSize);
        b.setPosition(PlayerShip.x + 50 / 2, PlayerShip.y); //shoot bullet from players
        bulletsList.add(b);
    }

    // application entry point
    public static void main(String[] args) {
        workingDirectory = System.getProperty("user.dir");
        InvadersApplication w = new InvadersApplication();
    }
}