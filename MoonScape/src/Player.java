package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.Hashtable;

import javax.sound.sampled.*;


public class Player  
implements KeyListener {
	
	enum WeaponHandling {
		Hold, Fire
	}
	
	
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
    public static Texture currentWeaponImage;
    boolean hasKey = false;
    boolean attacking = false;
    
    
    public static Hashtable<Integer, Texture> HealthTable = new Hashtable<>(){{
		put(8, new Texture("res/healthbar/8.png", 300));
        //put(15, new Texture("res/healthbar/8.png", 300));
        put(7, new Texture("res/healthbar/7.png", 300));
		//put(13, new Texture("res/healthbar/7.png", 300));
        put(6, new Texture("res/healthbar/6.png", 300));
		//put(11, new Texture("res/healthbar/6.png", 300));
        put(5, new Texture("res/healthbar/5.png", 300));
		//put(9, new Texture("res/healthbar/5.png", 300));
        put(4, new Texture("res/healthbar/4.png", 300));
		//put(7, new Texture("res/healthbar/4.png", 300));
        put(3, new Texture("res/healthbar/3.png", 300));
		//put(5, new Texture("res/healthbar/3.png", 300));
        put(2, new Texture("res/healthbar/2.png", 300));
		//put(3, new Texture("res/healthbar/2.png", 300));
        put(1, new Texture("res/healthbar/1.png", 300));
		//put(1, new Texture("res/healthbar/1.png", 300));
        put(0, new Texture("res/healthbar/0.png", 300));
	}};
	
	public static Hashtable<Player.WeaponHandling, Texture> Shotgun = new Hashtable<>()
	{{
		put(WeaponHandling.Hold, new Texture("res/weapons/shotgun2-noback.png", 64));
		put(WeaponHandling.Fire, new Texture("res/weapons/shotgun1-noback.png", 64));
	}};

    public Player(){
    	 this.x = 80;
         this.y = 880;

         this.turnDirection = 0; // -1 if left, +1 if right
         this.walkDirection = 0; // -1 if back, +1 if front
         this.rotationAngle = 270 * (Math.PI/180);
         this.moveSpeed = 6;
         this.rotationSpeed = 3.5 * (Math.PI / 180);
         // loads in with max health
         this.health = 24;
         this.maxHealth = 24;
         currentHealthImage = HealthTable.get(maxHealth/3);
         currentWeaponImage = Shotgun.get(WeaponHandling.Hold);
    }
    
    public void update(int[][]map){
    	this.rotationAngle += this.turnDirection * this.rotationSpeed;
        this.rotationAngle = Ray.normalizeAngle(rotationAngle);
        var moveStep = this.walkDirection * this.moveSpeed;
        var newPlayerX = this.x + Math.cos(this.rotationAngle) * moveStep;
        var newPlayerY = this.y + Math.sin(this.rotationAngle) * moveStep;

        if (!Game.hasWallAt(newPlayerX, newPlayerY)) {
            this.x = newPlayerX;
            this.y = newPlayerY;
           // System.out.println(this.x + " Y: " + this.y);

        }
        
       
        // since we are updating the players position 60 times per second
        if (health != 0 && maxHealth != 0) {
	        if (health == maxHealth - 3) {
	        	maxHealth = health;
	        	// change the image for the health bar

	        	currentHealthImage = HealthTable.get(health/3);
	        }
        }
        else {
        	currentHealthImage = HealthTable.get(0);
        }
        if(Math.abs(this.x - Sprite.bracelet.xPosition) <=  50 && Math.abs(this.y - Sprite.bracelet.yPosition) <= 50){
            this.hasKey = true;
            Sprite.bracelet.Remove();
        }
        checkIfWin();
        checkIfLost();
    }
    private void checkIfWin() {
        double checkXWin = Math.abs(this.x - 1508);
        double checkYWin = Math.abs(this.y - 90);
        if (checkXWin <= 50 && checkYWin <= 50) {
        	Game.State = Game.State.WON;
        }
    }
    
    private void checkIfLost() {
        if (health == 0) {
        	Game.State = Game.State.LOST;
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
    
    public void drawHealthbar(Graphics g) {
        g.drawImage(currentHealthImage.getImage(), 10, 800, null);
    }
    
    public void drawWeapon(Graphics g) {
        //TODO: filter pink
    	g.drawImage(currentWeaponImage.getImage().getScaledInstance(700, 700, Image.SCALE_DEFAULT), 400, 300, null);
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
        
        if(e.getKeyChar() == 'e' || e.getKeyChar() == 'E'){
            if(Game.mapIndexAt(this.vectorX(),this.vectorY() )==7  ){
                Game.map[Game.getYMapIndex(this.vectorY())][Game.getXMapIndex(this.vectorX())] = 77;
            }
            else if(Game.mapIndexAt(this.vectorX(),this.vectorY() )==77  ){
                Game.map[Game.getYMapIndex(this.vectorY())][Game.getXMapIndex(this.vectorX())] = 7;
            }
            if(Game.mapIndexAt(this.vectorX(),this.vectorY() )== 16 && this.hasKey ){
                Game.map[Game.getYMapIndex(this.vectorY())][Game.getXMapIndex(this.vectorX())] = 0;
            }
        }
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
        	try {
        		//playGunSound();
        	} catch (Exception e1) {
        		System.out.println(e1.toString());
        	}
        	currentWeaponImage = Shotgun.get(WeaponHandling.Fire);
            this.attacking = true;
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
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
        	currentWeaponImage = Shotgun.get(WeaponHandling.Hold);

        }
    }
    
    public boolean checkAttackRange(Mob mob, double test) {
       // currentWeaponImage == Shotgun.get(WeaponHandling.Fire)
    	if (attacking ) {

           System.out.println(" Player angle " + this.rotationAngle);
            System.out.println(" FOV angle " + Game.FOV_ANGLE/4);
            System.out.println(" Mob angle " + mob.angle);
//            System.out.println(mob.xPosition + " MOB POSITION Y: " + mob.yPosition);
//            System.out.println("MOB TO PLAYER DIST" + test);

    		if (test <= 250 && mob.angle <  Game.FOV_ANGLE/4 ) {
    			try {

            		    playDamageSound();
            	} catch (Exception e1) {
            		System.out.println(e1.toString());
            	}

    			return true;
    		}

    	}
        currentWeaponImage = Shotgun.get(WeaponHandling.Hold);

        return false;
    }
    
    static void playGunSound() throws Exception {
    	File file = new File("res/audio/shotgunsound.wav");
    	AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
    	Clip clip = AudioSystem.getClip();
    	clip.open(audioStream);
    	clip.start();

    }
    
    static void playDamageSound() throws Exception {
    	File file = new File("res/audio/mobdamagesound.wav");
    	AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
    	Clip clip = AudioSystem.getClip();
    	clip.open(audioStream);
    	clip.start();

    }
 
}
