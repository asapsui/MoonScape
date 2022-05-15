// Mary Czelusniak
// Sprite calculations,
// No colission check for sprites
// Since they are all 64x64 squares within the 3D "screen," implementing a colission check becomes much more complicated
// because we have to account for the pixels, the magenta background in the png files, that we do not see in the game.
// Not doing so makes the character unable to move way before it actually gets close to the sprite

package main;

public class Sprite extends Texture{

    public static Sprite plantInPot = new Sprite ("res/objects/PlantInPot.bmp",64,90,730);
    public static Sprite pillar = new Sprite("res/objects/pillar.png", 64,365 ,725);
    public static Sprite pillar2 = new Sprite("res/objects/pillar.png", 64,365 ,880);

    public static Sprite barrel = new Sprite ("res/objects/barrel.png",64,17*64+40,9*64+35);
    public static Sprite barrel2 = new Sprite ("res/objects/barrel.png",64,16*64+15,8*64+36);

    public static Sprite fancyBarrel = new Sprite ("res/objects/barrel2.png",127,18*64+36,10*64+36);
    public static Sprite fancyBarrel2 = new Sprite ("res/objects/barrel2.png",127,19*64+36,7*64+36);

    public static Sprite pot = new Sprite ("res/objects/pot.bmp",64,150,6*64+36);
    public static Sprite box = new Sprite("res/objects/box.png",64, 647, 150);



    public static Sprite blueLight = new Sprite("res/objects/blueLight.bmp",64, 775, 150);
    public static Sprite blueLight2 = new Sprite("res/objects/blueLight.bmp",64, 896, 832);
    public static Sprite blueLight3 = new Sprite("res/objects/blueLight.bmp",64, 1152, 832);
    public static Sprite blueLight4 = new Sprite("res/objects/blueLight.bmp",64, 1400, 732);



    public static Sprite bracelet = new Sprite ("res/objects/GoldBraclet.bmp",64,1500,296);


    public static Sprite candlestand = new Sprite("res/objects/candlestand.png",64, 80, 80);
    public static Sprite candlestand2 = new Sprite("res/objects/candlestand.png",64, 64*5+40, 80);


    public static Sprite fountain = new Sprite("res/objects/fountain.png",64, 11*64, 8*64);
    public static Sprite fountain2 = new Sprite("res/objects/fountain.png",64, 4*64, 8*64);





    double xPosition, yPosition, distance, angle, angleSpritePlayer;

    public boolean visible;
    public boolean isRemoved = false;

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
    public void Remove(){
        this.isRemoved = true;
    }

}
