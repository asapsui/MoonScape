//Mary Czelusniak
//Ray Casting Engine
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public class Game extends JFrame implements Runnable, ActionListener {
    //////////////////////////
    // RESOLUTION CONSTS   ///
    //////////////////////////
    public static final int TILE_SIZE = 64;
    public static final int MAP_NUM_ROWS = 15;
    public static final int MAP_NUM_COLS = 15;
    public static final int WINDOW_HEIGHT = MAP_NUM_ROWS * TILE_SIZE;
    public static final int WINDOW_WIDTH = MAP_NUM_COLS * TILE_SIZE;

    ////////////////////////////
    // UNUSED MINIMAP CONSTANT//
    ////////////////////////////
    public final double MINIMAP_SCALE_FACTOR = .2;


    /////////////////////////
    // RAY CASTING CONSTS  //
    /////////////////////////
    public static final double FOV_ANGLE = 60 * (Math.PI/180); //60 in radians
    final int WALL_STRIP_WIDTH = 1;
    final int NUM_RAYS = WINDOW_WIDTH/WALL_STRIP_WIDTH;

    //////////////
    // OBJECTS  //
    //////////////
    public Player player;
    public ArrayList<Ray> rays;
    Timer timer;
    BufferStrategy bs;

    private BufferedImage image;
    public int[] pixels;
    public ArrayList<Texture> textures;




    public ArrayList<Sprite> sprites;
    public ArrayList<Sprite> visibleSprites;
    public double playerDistance = (WINDOW_WIDTH / 2) / (Math.tan(FOV_ANGLE / 2));





    private Thread thread;
    private boolean running = false;
    public static int[][] map =
            {       {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,1,0,2,0,4,0,3,0,2,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,2,0,3,0,4,0,2,0,1,0,0,1},
                    {1,0,0,0,0,0,0,5,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,5,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,5,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,2,0,3,3,3,3,3,1},
                    {1,0,0,0,0,0,0,2,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,2,0,0,0,4,0,0,1},
                    {1,0,0,0,0,0,0,2,0,0,0,0,4,4,1},
                    {1,0,0,0,0,0,0,2,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}

            };
    Game(){
        thread = new Thread(this);

        image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        textures = new ArrayList<Texture>();
        textures.add(Texture.wood);
        textures.add(Texture.brick);
        textures.add(Texture.bluestone);
        textures.add(Texture.stone);
        textures.add(Texture.moon);




        sprites = new ArrayList<Sprite>();
        sprites.add(Sprite.candlestand);
        sprites.add(Sprite.couch);
        sprites.add(Sprite.fountain);
        sprites.add(Sprite.box);
        visibleSprites = new ArrayList<>();

        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Ray Casting Engine");
        setLayout(new BorderLayout());
        rays = new ArrayList<Ray>();
        player = new Player();
        timer = new Timer (16,this);
        addKeyListener(player);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        start();
    }


    public synchronized void start(){
        running = true;
        thread.start();
    }
    public synchronized void stop(){
        running = false;
        try{
            thread.join();
        } catch (InterruptedException e) {
        }
    }
    @Override
    public void run() {
        while(running) {
//            renderingWalls(pixels,createAllRays());
//            renderSpriteProjection();
//            render();
            timer.start();

        }

    }


    public void renderSpriteProjection(){
        // Very similar to wall projection, with the added check of the sprite being visible or not to the player
        // Where the visible sprites are then added to the visibleSprites ArrayList, then cleared when the method is called again
        // In order to clear the screen
        visibleSprites.clear();
        for(Sprite sprite : sprites){
            sprite.visibleSprite(player);
            if(sprite.visible == true){
                visibleSprites.add(sprite);
            }

        }
        if(visibleSprites.size() > 0) {
            for(int i = 0; i < visibleSprites.size(); i++){
                for (int j = 0; j < visibleSprites.size()- i - 1; j++){
                    if(visibleSprites.get(i).distance < visibleSprites.get(i + 1).distance){
                        Sprite s = visibleSprites.get(i);
                        visibleSprites.set(i,visibleSprites.get(i + 1));
                        visibleSprites.set(i+1,s);
                    }
                }
            }
            for (int i = 0; i < visibleSprites.size(); i++) {

                double spriteAngle = Math.atan2(visibleSprites.get(i).yPosition - player.y,
                        visibleSprites.get(i).xPosition - player.x) - player.rotationAngle;

                double correctWallDistance = visibleSprites.get(i).distance * Math.cos(visibleSprites.get(i).angle);

                double spriteHeight = (TILE_SIZE / correctWallDistance) * playerDistance;
                double spriteWidth = spriteHeight;

                double spriteTopY = (WINDOW_HEIGHT / 2) - (spriteHeight / 2);
                spriteTopY = (spriteTopY < 0) ? 0 : spriteTopY;
                double spriteBottomY = (WINDOW_HEIGHT / 2) + (spriteHeight / 2);
                spriteBottomY = (spriteTopY > WINDOW_HEIGHT) ? WINDOW_HEIGHT : spriteBottomY;

                double spriteScreenPosX = Math.tan(spriteAngle) * playerDistance;

                double spriteLeftX = (WINDOW_WIDTH / 2) + spriteScreenPosX - (spriteWidth / 2);
                double spriteRightX = spriteLeftX + spriteWidth;

                int TexspriteWidth = visibleSprites.get(i).SIZE;
                int TexspriteHeight = visibleSprites.get(i).SIZE;

                for (int x = (int) spriteLeftX; x < spriteRightX; x++) {

                    double texelWidth = (TexspriteWidth / spriteWidth);
                    int offSetX = (int) ((x - spriteLeftX) * texelWidth);

                    for (int y = (int) spriteTopY; y < spriteBottomY; y++) {
                        if (x > 0 && x < WINDOW_WIDTH && y > 0 && y < WINDOW_HEIGHT) {
                            int distanceFromTop = (int) (y + (spriteHeight / 2) - (WINDOW_WIDTH / 2));
                            int offSetY = (int) (distanceFromTop * ((float) TexspriteHeight / spriteHeight));

                            int texelColor = visibleSprites.get(i).pixels[((TexspriteWidth * offSetY) + offSetX)];

                            // Filtering of magenta happens here, where Color.Magenta == r: 255, g: 0, b:255
                            // Many textures I have found online do not have this especific shade, but all have a green value of zero
                            // So maybe, in order to use those textures, change something over here so that it will filter any color
                            // that has a G value of 0 and the two other values above 150, so to not filter out textures with the color black
                            // Important: getRBG() returns a sRGB representation of the color, which is -65281
                            // This is also the only line of code here where we reference the rays, so that Sprites are not rendered when we
                            // are facing a wall and the sprite is placed behind it

                            if (texelColor != Color.MAGENTA.getRGB() && visibleSprites.get(i).distance < rays.get(x).distance)
                                pixels[x + (y * WINDOW_WIDTH)] = texelColor;
                        }
                    }
                }

            }
        }
    }

    public void renderingWalls(int[] pixels, ArrayList<Ray> rays){


        //This is where the magic happens
        int i = 0;
        for(Ray ray : rays) {
            int textNum = ray.hitWallColor - 1;
            double correctWallDistance = ray.distance * Math.cos(ray.rayAngle - player.rotationAngle);
            double wallStripHeight = playerDistance * (TILE_SIZE /correctWallDistance);

            int wallTopPixel = (int) ((WINDOW_HEIGHT/2) - (wallStripHeight/2));
            wallTopPixel = wallTopPixel < 0 ? 0 : wallTopPixel;
            int wallBottomPixel =(int) ((WINDOW_HEIGHT/2) + (wallStripHeight/2));
            wallBottomPixel = wallBottomPixel > WINDOW_HEIGHT ? WINDOW_HEIGHT : wallBottomPixel;

            int offSetX;
            if(ray.wasHitVertical){
                offSetX = (int)ray.wallHitY % textures.get(textNum).SIZE;
            }else{
                offSetX = (int)ray.wallHitX % textures.get(textNum).SIZE;
            }
            //Coloring the sky
            int c = 255;
            for(int n=0; n<wallTopPixel; n++) {
                pixels[((WINDOW_WIDTH*n)+i)] = new Color(c,c/2,125).getRGB();
                if( c > 0 && c/2 > 0 && n % 2 == 0)
                    c--;
            }

            for(int x = wallTopPixel; x < wallBottomPixel; x++){


                int distanceFromTop = (int) ( x + (wallStripHeight / 2) - (WINDOW_HEIGHT / 2));
                int offSetY = (int) (distanceFromTop * ((float)textures.get(textNum).SIZE) / wallStripHeight);

                int texColor;
                if(ray.wasHitVertical)
                    texColor = textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX];
                    //I have no idea why the fuck this "else" line works but it does
                    //it shades the horizontal sides of the walls a darker shade.
                else texColor = (textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX] >> 1) & 8355711;
                pixels[i + (x * WINDOW_WIDTH)] = texColor;

            }
            //Coloring the floor

            for(int n=wallBottomPixel; n<WINDOW_HEIGHT; n++) {
                pixels[(WINDOW_WIDTH * n)+i] = Color.DARK_GRAY.getRGB();

            }

            i++;
        }



    }



    private void render() {
        bs = getBufferStrategy();
        if (bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);


//        //////////////////
//        // MINIMAP CODE //
//        //////////////////
//        for(int i = 0; i <MAP_NUM_ROWS; i++) {
//            for (int j = 0; j < MAP_NUM_COLS; j++) {
//                int x = j * TILE_SIZE;
//                int y = i * TILE_SIZE;
//                Color tileColor = map[i][j] > 0 ? Color.PINK : Color.LIGHT_GRAY;
//                g.setColor(tileColor);
//                g.fillRect((int) (Math.ceil(MINIMAP_SCALE_FACTOR * x)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * y)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE))
//                );
//                g.setColor(Color.BLACK);
//                g.drawRect((int) (Math.ceil(MINIMAP_SCALE_FACTOR * x)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * y)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE)),
//                        (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE))
//                );
//            }
//        }
        //RAYS
//        g.setColor(Color.CYAN);
//        for(int i = 0; i < rays.size(); i++){
//            g.drawLine((int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitX)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitY))
//            );
//            //g.draw(new Line2D.Double(player.x, player.y, f.get(i).wallHitX, f.get(i).wallHitY));
//        }



//         // Attack vector
//        Ray test = new Ray (player.rotationAngle);
//        test.cast(player);
//        g.setColor(Color.BLACK);
//        g.setStroke(new BasicStroke(2));
//        g.draw(new Line2D.Double(MINIMAP_SCALE_FACTOR * player.x,
//                MINIMAP_SCALE_FACTOR * player.y,
//                MINIMAP_SCALE_FACTOR * test.wallHitX,
//                MINIMAP_SCALE_FACTOR * test.wallHitY));
//


        g.dispose();
        bs.show();
    }


    public ArrayList<Ray> createAllRays(){
        rays.clear();

        double rayAngle = player.rotationAngle - (FOV_ANGLE/2);
        for (int i = 0; i < NUM_RAYS; i++){
            //Formula for adjusting very slight wall distortion
            //double rayAngle = player.rotationAngle + Math.atan((i - NUM_RAYS/2)/((WINDOW_WIDTH / 2) / (Math.tan(FOV_ANGLE / 2))));
            Ray ray = new Ray(rayAngle);
            ray.cast(player);
            rays.add(ray);
            rayAngle += FOV_ANGLE/NUM_RAYS;

        }
        return rays;
    }

    public static boolean hasWallAt(double newX, double newY){
        if(newX > WINDOW_WIDTH || newX < 0 || newY <0 || newY > WINDOW_HEIGHT){
            return true;
        }
        int wallX = (int) (newX/TILE_SIZE);
        int wallY = (int) (newY/TILE_SIZE);
        return map[wallY][wallX] != 0;
    }
    public static int mapIndexAt (double x, double y){
        if(x < 0 || x > WINDOW_WIDTH || y < 0 || y > WINDOW_HEIGHT){
            return 0;
        }
        int wallX = (int) (x/TILE_SIZE);
        int wallY = (int) (y/TILE_SIZE);
        return map[wallY][wallX];
    }

    //Updating Player position 60 times per second (see timer delay)
    @Override
    public void actionPerformed(ActionEvent e) {
        renderingWalls(pixels,createAllRays());
        renderSpriteProjection();
        player.update(map);
        render();

    }

    // No one cares about main =D
    public static void main(String[] args){
        new Game();
    }
}
