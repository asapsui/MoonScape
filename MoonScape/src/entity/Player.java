package entity;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.Game;

public class Player  
implements KeyListener {
	
	// made x and y public so its accessible to the ray class
	private Camera cam;
	public double x;
    public double y;
	
    int turnDirection; // -1 for left, +1 for right
    int walkDirection; // -1 for walking backwards(up),// +1 for walking forwards(down)
    public double rotationAngle;
    public double moveSpeed;
    public double rotationSpeed;

    public Player(){
    	
        this.x = 300;
        this.y = 250;

	//try to make the cam
	this.cam = cam;
        this.turnDirection = 0; // -1 if left, +1 if right
        this.walkDirection = 0; // -1 if back, +1 if front
        this.rotationAngle = Math.PI / 2;
        this.moveSpeed = 2;
        this.rotationSpeed = 2 * (Math.PI / 180);
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

	//update movement (fixing)
	/*
	public void update(Player player, double time) {
        if(cam.getBounds().collides(this.bounds)) {
	
	//collides(boolean)****
            super.update(time);
            chase(player);
            move();

            if(teleported) {
                teleported = false;

                bounds.setWidth(size / 2);
                bounds.setHeight(size / 2 - yOffset);
                bounds.setXOffset(size / 2 - xOffset);
                bounds.setYOffset(size / 2 + yOffset);

                hitBounds = new AABB(pos, size, size);
                hitBounds.setXOffset(size / 2);

                sense = new AABB(new Vector2f(pos.x + size / 2 - r_sense / 2, pos.y + size / 2 - r_sense / 2), r_sense);
                attackrange = new AABB(new Vector2f(pos.x + bounds.getXOffset() + bounds.getWidth() / 2 - r_attackrange / 2 , pos.y + bounds.getYOffset() + bounds.getHeight() / 2 - r_attackrange / 2 ), r_attackrange);
            }

            if(attackrange.colCircleBox(player.getBounds()) && !isInvincible) {
                attack = true;
                player.setHealth(player.getHealth() - damage, 5f * getDirection(), currentDirection == UP || currentDirection == DOWN);
            } else {
                attack = false;
            }

            if (!fallen) {
                if (!tc.collisionTile(dx, 0)) {
                    sense.getPos().x += dx;
                    attackrange.getPos().x += dx;
                    pos.x += dx;
                }
                if (!tc.collisionTile(0, dy)) {
                    sense.getPos().y += dy;
                    attackrange.getPos().y += dy;
                    pos.y += dy;
                }
            } else {
                if(ani.hasPlayedOnce()) {
                    die = true;
                }
            }
        }
    }
	*/
	
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
