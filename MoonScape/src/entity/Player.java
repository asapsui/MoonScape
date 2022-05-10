package main;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

public class Player  
implements KeyListener {
	
	double x;
    double y;
	
    int turnDirection; // -1 for left, +1 for right
    int walkDirection; // -1 for walking backwards(up),// +1 for walking forwards(down)
    public double rotationAngle;
    public double moveSpeed;
    public double rotationSpeed;
    public int health;
    public int maxHealth;
    public static Texture currentHealthImage;
    
	public static Hashtable<Integer, Texture> HealthTable = new Hashtable<>(){{
		put(8, new Texture("res/healthbar/8.png", 300));
		put(7, new Texture("res/healthbar/7.png", 300));
		put(6, new Texture("res/healthbar/6.png", 300));
		put(5, new Texture("res/healthbar/5.png", 300));
		put(4, new Texture("res/healthbar/4.png", 300));
		put(3, new Texture("res/healthbar/3.png", 300));
		put(3, new Texture("res/healthbar/2.png", 300));
		put(1, new Texture("res/healthbar/1.png", 300));
		put(0, new Texture("res/healthbar/0.png", 300));


	}};
 
    public Player(){
    	
        this.x = 200;
        this.y = 800;

        this.turnDirection = 0; // -1 if left, +1 if right
        this.walkDirection = 0; // -1 if back, +1 if front
        this.rotationAngle = Math.PI / 2;
        this.moveSpeed = 2;
        this.rotationSpeed = 2 * (Math.PI / 180);
        // loads in with max health
        this.health = 9;
        this.maxHealth = 9;
        currentHealthImage = HealthTable.get(8);
    }
    public void update(int[][]map){
        this.rotationAngle += this.turnDirection * this.rotationSpeed;
        var moveStep = this.walkDirection * this.moveSpeed;
        var newPlayerX = this.x + Math.cos(this.rotationAngle) * moveStep;
        var newPlayerY = this.y + Math.sin(this.rotationAngle) * moveStep;

        if (!Game.hasWallAt(newPlayerX, newPlayerY)) {
            this.x = newPlayerX;
            this.y = newPlayerY;
        }
        
        
        // since we are updating the players position 60 times per second
        // this may mess up, since we updating the position so many times a second
        if (health != 0 && maxHealth != 0) {
	        if (health < maxHealth) {
	        	maxHealth = health;
	        	// change the image for the health bar
	        	currentHealthImage = HealthTable.get(health);
	        }
        }
        else {
        	currentHealthImage = HealthTable.get(0);
        }
        
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
    public double vectorX(){
        return this.x + Math.cos(this.rotationAngle) * 30;
    }
    public double vectorY(){
        return this.y+ Math.sin(this.rotationAngle) * 30;
    }
    
    public int getHealth() {
    	return this.health;
    }
    
    public void drawHealthbar(Graphics g) {
        g.drawImage(currentHealthImage.getImage(), 10, 800, null);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_UP){
            this.walkDirection = +1;
        }
        if(e.getKeyChar()=='s' || e.getKeyCode() == KeyEvent.VK_DOWN){
            this.walkDirection =-1;
        }
        if(e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT){
            this.turnDirection = +1;
        }
        if(e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT){
            this.turnDirection = -1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_UP){
            this.walkDirection = 0;
        }
        if(e.getKeyChar()=='s' || e.getKeyCode() == KeyEvent.VK_DOWN){
            this.walkDirection = 0;
        }
        if(e.getKeyChar() == 'd' || e.getKeyCode() == KeyEvent.VK_RIGHT){
            this.turnDirection = 0;
        }
        if(e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_LEFT){
            this.turnDirection = 0;
        }
    }
}
