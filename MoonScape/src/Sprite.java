package main;
import java.lang.reflect.Array;
import java.util.ArrayList;
// Mary Czelusniak
// Sprite calculations,
// No colission check for sprites
// Since they are all 64x64 squares within the 3D "screen," implementing a colission check becomes much more complicated
// because we have to account for the pixels, the magenta background in the png files, that we do not see in the game.
// Not doing so makes the character unable to move way before it actually gets close to the sprite
public class Sprite extends Texture{

    public static Sprite couch = new Sprite("res/objects/pillar.png", 64,400 ,500);
    public static Sprite candlestand = new Sprite("res/objects/candlestand.png",64, 300, 500);
    public static Sprite fountain = new Sprite("res/objects/fountain.png",64, 200, 450);
    public static Sprite box = new Sprite("res/objects/box.png",64, 250, 450);


    double xPosition, yPosition, distance, angle;

    public boolean visible;

    public Sprite(String location, int size, double x, double y) {
        super(location, size);
        this.xPosition = x;
        this.yPosition = y;
    }
    public void visibleSprite (Player player){
            double angleSpritePlayer = player.rotationAngle - Math.atan2(this.yPosition - player.y,
                    this.xPosition - player.x);

            if(angleSpritePlayer > Math.PI){
                angleSpritePlayer -= 2 * Math.PI;
            }
            if (angleSpritePlayer < -Math.PI ){
                angleSpritePlayer += 2 * Math.PI;
            }
            angleSpritePlayer = Math.abs(angleSpritePlayer);
            if(angleSpritePlayer < (Game.FOV_ANGLE/2) + 0.2){
                this.visible = true;
                this.angle = angleSpritePlayer;
                this.distance = Ray.distanceBetweenPoints(this.xPosition, this.yPosition, player.x,
                        player.y);

            }else{
                this.visible = false;
            }
    }


}