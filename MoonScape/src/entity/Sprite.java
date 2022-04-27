/* References:
 * https://www.youtube.com/watch?v=A9OsBSHFza8&t=638s
 * https://lodev.org/cgtutor/raycasting3.html
 * https://www.youtube.com/watch?v=xKGpBl3D0v0&list=PLA331A6709F40B79D&index=23
 */
package entity;

import java.awt.Image;

import graphics.Texture;
import main.Game;

// these are the basics components of a sprite
// this is a 2D picture that will always be facing to the player
public class Sprite {
	
	// all the members/properties that objects and enemies use
	public double x;
	public double y;
	
	// for the velocities
	public double dx;
	public double dy;
	private Texture texture;
	
	// this'll be used for when the player has killed a mob
		// 	we will remove it from the arraylist and stop rendering it
	public boolean removed;

	// 
	public Sprite1(double x, double y) {
		this.x = x;
		this.y = y;
		dx = 0;
		dy = 0;
		removed = false;
	}
	
	public void update() {
		// default - moving at a constant velocity
		x += dx;
		y += dy;
		
		// should we multiply by time?
	}
	
	// this texture class already handles the images width, height, and all 
	// images are set 64x64 dimension
	public void loadTexture(Texture texture) {
		this.texture = texture;
	}
	
	// added a getImage() method in the Texture class for
	public Texture getTexture() {
		return texture;
	}
	
	public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
    
    public double getVelocityX() {
    	return this.dx; 
    }
    
    public double getVeloctityY() {
    	return this.dy;
    }
    
    public void setVelocityX(double dx) {
    	this.dx = dx;
    }
    
    public void setVelocityY(double dy) {
    	this.dy = dy;
    }
    
    public void setX(double x) {
    	this.x = x;
    }
    
    public void setY(double y) {
    	this.y = y;
    }
    
    public boolean isRemoved() {
        return removed;
    }

    public void setVisibility(Boolean removed) {
        this.removed = removed;
    }
    
}
