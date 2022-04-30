package main;

//Mary Czelusniak
//Ray Casting Engine
import javax.sound.sampled.Line;
import javax.swing.*;

import entity.Block;
import entity.Player;
import entity.Sprite;
import graphics.Ray;
import graphics.Texture;

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
    final double FOV_ANGLE = 60 * (Math.PI/180); //60 in radians
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

    public Block block;
    public ArrayList<Sprite> sprites;
    
    //public Screen screen;
    private enum STATE {
        MENU, GAME;
    };

    private STATE State = STATE.MENU;

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
                    {2,0,0,0,0,0,0,2,0,0,0,0,4,4,1},
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
        textures.add(Texture.guard);
        
     
        sprites = new ArrayList<Sprite>();
        block = new Block(300, 250);
        sprites.add(block);


        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Ray Casting Engine");
        setLayout(new BorderLayout());
        rays = new ArrayList<Ray>();
        player = new Player();
        timer = new Timer (15,this);
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
            renderingWalls(pixels,createAllRays());
            renderSprites(pixels, createAllRays());
            render();
            timer.start();

        }

    }
    
    /* References: 
     * https://www.youtube.com/watch?v=A9OsBSHFza8&list=PL656DADE0DA25ADBB&index=36
     * 
     */
    // 2D sprites are always facing the camera 
    public void renderSprites(int[] pixels, ArrayList<Ray> rays) {
    	//sprites.get(0).getTexture().pixels[( (sprites.get(0).getTexture().SIZE) * offSetY) + offSetX]

    	 for(Ray ray : rays) {
             int textNum = ray.getHitWallColor() - 1; 
             double correctWallDistance = ray.getDistance() * Math.cos(ray.getRayAngle() - player.rotationAngle);
             double playerDistance = (WINDOW_WIDTH / 2) / (Math.tan(FOV_ANGLE / 2));
             double wallStripHeight = playerDistance * (TILE_SIZE /correctWallDistance);
             
             int wallTopPixel = (int) ((WINDOW_HEIGHT/2) - (wallStripHeight/2));
             wallTopPixel = wallTopPixel < 0 ? 0 : wallTopPixel;
             int wallBottomPixel =(int) ((WINDOW_HEIGHT/2) + (wallStripHeight/2));
             wallBottomPixel = wallBottomPixel > WINDOW_HEIGHT ? WINDOW_HEIGHT : wallBottomPixel;
    	 
             
             for(int x = wallTopPixel; x < wallBottomPixel; x++){
            	 
             }
    	 }
    }

    //TODO: FUCKING TEXTURES AHHFISHFIDUHFISHDFUI
    public void renderingWalls(int[] pixels, ArrayList<Ray> rays){


        //This is where the magic happens
        int i = 0;
        for(Ray ray : rays) {
            int textNum = ray.getHitWallColor() - 1; 
            double correctWallDistance = ray.getDistance() * Math.cos(ray.getRayAngle() - player.rotationAngle);
            double playerDistance = (WINDOW_WIDTH / 2) / (Math.tan(FOV_ANGLE / 2));
            double wallStripHeight = playerDistance * (TILE_SIZE /correctWallDistance);

            int wallTopPixel = (int) ((WINDOW_HEIGHT/2) - (wallStripHeight/2));
            wallTopPixel = wallTopPixel < 0 ? 0 : wallTopPixel;
            int wallBottomPixel =(int) ((WINDOW_HEIGHT/2) + (wallStripHeight/2));
            wallBottomPixel = wallBottomPixel > WINDOW_HEIGHT ? WINDOW_HEIGHT : wallBottomPixel;

            int offSetX;
            if(ray.isWasHitVertical()){
                offSetX = (int)ray.getWallHitY() % textures.get(textNum).SIZE;
            }else{
                offSetX = (int)ray.getWallHitX() % textures.get(textNum).SIZE;
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
                if(ray.isWasHitVertical())
                
                    texColor = textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX];
                    //I have no idea why the fuck this "else" line works but it does
                    //it shades the horizontal sides of the walls a darker shade.
                else texColor = (textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX] >> 1) & 8355711;
                pixels[i + x*(WINDOW_WIDTH)] = texColor;

            }
            
            //Coloring the floor
            for(int n=wallBottomPixel; n<WINDOW_HEIGHT; n++) {
                pixels[(WINDOW_WIDTH * n)+i] = Color.DARK_GRAY.getRGB();

            }

            i++;
        }



    }


    //TODO: the first for loop (it draws the 2d grid),
    // is using a fuck ton of memory, gotta find a way to make
    // it render only once. No minimap for now.
    private void render() {
        bs = getBufferStrategy();
        if (bs == null){
            createBufferStrategy(2);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        //ArrayList<Ray> f = new ArrayList<>();




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
//        //RAYS
//        g.setColor(Color.CYAN);
//        for(int i = 0; i < rays.size(); i++){
//            g.drawLine((int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitX)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitY))
//            );
//            //g.draw(new Line2D.Double(player.x, player.y, f.get(i).wallHitX, f.get(i).wallHitY));
//        }
//        //PLAYER ICON
//          g.setColor(Color.BLACK);
//        g.fillOval((int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x-5)),
//                (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y-5)),
//                (int) (Math.ceil(MINIMAP_SCALE_FACTOR * 10)),
//                (int) (Math.ceil(MINIMAP_SCALE_FACTOR * 10))
//        );
//       // DIRECTION LINE
//        g.setColor(Color.blue);
//        g.drawLine( (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y)),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.vectorX())),
//                    (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.vectorY()))
//        );
//
//         // Attack vector
//        Ray test = new Ray (player.rotationAngle);
//        test.cast(player);
//        g.setColor(Color.GREEN);
//        g.draw(new Line2D.Double(MINIMAP_SCALE_FACTOR * player.x,
//                                 MINIMAP_SCALE_FACTOR * player.y,
//                                 MINIMAP_SCALE_FACTOR * test.wallHitX,
//                                 MINIMAP_SCALE_FACTOR * test.wallHitY));
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        
        g.drawImage(image, 0, 0, this);
        
        if (State == STATE.GAME) {
            p.render(g);
        }
        else if (State == State.MENU) {
            menu.render(g);
        }
        
        g.drawImage(block.getTexture().getImage(), (int) block.getX(), (int) block.getY(), null);
        g.dispose();
        bs.show();
    }


    public ArrayList<Ray> createAllRays(){
        rays.clear();

        double rayAngle = player.rotationAngle - (FOV_ANGLE/2);
        for (int i = 0; i < NUM_RAYS; i++){
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
        player.update(map);
        
        // this'll be where the moving sprites will update

    }

    // No one cares about main =D
    public static void main(String[] args){
        new Game();
    }
}
