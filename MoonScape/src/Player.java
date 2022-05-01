import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player  implements KeyListener {
    double x;
    double y;
    int turnDirection; // -1 for left, +1 for right
    int walkDirection; // -1 for walking backwards(up),// +1 for walking forwards(down)
    double rotationAngle;
    double moveSpeed;
    double rotationSpeed;

    public Player(){

        this.x = 300;
        this.y = 250;

        this.turnDirection = 0; // -1 if left, +1 if right
        this.walkDirection = 0; // -1 if back, +1 if front
        this.rotationAngle = Math.PI / 2;
        this.moveSpeed = 2;
        this.rotationSpeed = 1.5 * (Math.PI / 180);
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
        }
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
    public double vectorX(){
        return this.x+ Math.cos(this.rotationAngle) * 30;
    }
    public double vectorY(){
        return this.y+ Math.sin(this.rotationAngle) * 30;
    }



    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'w'){
            this.walkDirection = +1;
        }
        if(e.getKeyChar()=='s'){
            this.walkDirection =-1;
        }
        if(e.getKeyChar() == 'd'){
            this.turnDirection = +1;
        }
        if(e.getKeyChar() == 'a'){
            this.turnDirection = -1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == 'w'){
            this.walkDirection = 0;
        }
        if(e.getKeyChar()=='s'){
            this.walkDirection =0;
        }
        if(e.getKeyChar() == 'd'){
            this.turnDirection = 0;
        }
        if(e.getKeyChar() == 'a'){
            this.turnDirection = 0;
        }
    }
}