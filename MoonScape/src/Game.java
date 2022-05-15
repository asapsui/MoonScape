//Mary Czelusniak
//Ray Casting Engine

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame implements Runnable, ActionListener {
  //////////////////////////
  // RESOLUTION CONSTS   ///
  //////////////////////////
  public static final int TILE_SIZE = 64;
  public static final int MAP_NUM_ROWS = 15;
  public static final int MAP_NUM_COLS = 25;

  public static final int WINDOW_HEIGHT =  1000 ;//MAP_NUM_ROWS * TILE_SIZE;
  public static final int WINDOW_WIDTH =   1280 ;//MAP_NUM_COLS * TILE_SIZE;


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
  public static Player player;
  public ArrayList<Ray> rays;
  Timer timer;
  long test;
  BufferStrategy bs;

  private BufferedImage image;
  public int[] pixels;
  public ArrayList<Texture> textures;


  public static ArrayList<Mob> mobs;
  public static ArrayList<Sprite> sprites;
  public ArrayList<Sprite> visibleSprites;
  public double playerDistance = (WINDOW_WIDTH / 2) / (Math.tan(FOV_ANGLE / 2));



  public static Color guardColor = new Color(152,0,136,255);

  public enum STATE {
      MENU, GAME, WON, LOST;
  };

  public static STATE State = STATE.MENU;


  private Thread thread;
  private boolean running = false;
  public static int[][] map =
          {       {5  ,  14,  14,  14,  13,  14,  5,  5,   5,  5,  5,  8,  9,  8,  4,  8,  9,  4,  4,  8,  4,  8,  9,  4,  4},
                  {12  ,  0,  0,  0,  0,  0,  2,  0,   0,  1,  4,  0,  0,  0,  16,  0,  0,  0,  0,  0,  0,  0,  0, 0,  15},
                  {13  ,  0,  0,  0,  0,  0,  2,  0,   0,  4,  0,  0,  0,  0,  9,  9,  4,  4,  4,  8,  4,  9,  4, 4,  4},
                  {14  ,  0,  0,  2,  22, 7,  22,  0,  0,  4,  0,  0,  0,  0,  4,  0,  0,  0,  0,  0,  0,  0,  10, 12,  14},
                  {14  ,  0,  2,  0,  0,  0,  0,  0,   0,  4,  4,  4,  0,  0,  4,  0,  0,  0,  0,  0,  0,  0,  10,  0,  10},
                  {14  ,  0,  2,  0,  0,  0,  0,  0,   0,  0,  0,  0,  0,  0,  4,  0,  0,  0,  0,  0,  0,  0,  12,  0,  13},
                  {5  ,  1,  0,  0,  0,  0,  0,  0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  10,  10,13,  10, 0,  0,  14},
                  {5  ,  0,  0,  0,  0,  0,  0,  0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  10,  0,  0,  0,  0,  0,  14},
                  {5  ,  0,  0,  0,  0,  0,  0,  0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  12,  0,  0,  0,  0,  0,  10},
                  {5  ,  0,  0,  0,  0,  0,  0,  0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  14,  0,  0,  0,  0,  0,  11},
                  {3  ,  3 ,66,  7  ,66 ,3  ,3,  0  , 0,  0,  1,  10, 14, 10,  14 ,10, 12, 10, 0,  0,  0,  0,  0,  0,  10},
                  {3,    0,  0,  0,  0,  0,  69, 0,   0,  0,  100,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  14},
                  {3,    0,  0,  0,  0,  0,  7,  0,   0,  0,  7,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  12},
                  {3,    0,  0,  0,  0,  0,  69, 0,   0,  0,  100,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  13,  5},
                  {3  ,  3,  3,  3,  3,  3,  3,  5,   5,  5,  5,  14,  14,  14,  14,  14,  14,  14,  14,  14,  14,  14,  14,  14,  5}

          };

  Game(){
      thread = new Thread(this);

      image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
      pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

      textures = new ArrayList<Texture>();
      textures.add(Texture.blueBall); //1
      textures.add(Texture.greyPanel2); //2


      textures.add(Texture.bluestone); //3

      textures.add(Texture.spaceShip); //4
      textures.add(Texture.moon); // 5


      textures.add(Texture.doorWall);//6
      textures.add(Texture.door);//7

      textures.add(Texture.spaceShip2);//8
      textures.add(Texture.spaceShip1);//9
      textures.add(Texture.bigSpaceShip);//10
      textures.add(Texture.bigSpaceShip1);//11
      textures.add(Texture.bigSpaceShip2);//12
      textures.add(Texture.bigSpaceShip3);//13
      textures.add(Texture.bigSpaceShip4);//14
      textures.add(Texture.escape); //15
      textures.add(Texture.escapeDoor);//16

      // add more mobs
      mobs = new ArrayList<Mob>();
      mobs.add(Mob.guard1);
      mobs.add(Mob.guard2);
      mobs.add(Mob.guard3);
      mobs.add(Mob.guard4);
      mobs.add(Mob.guard5);
      mobs.add(Mob.bigGuy);

      sprites = new ArrayList<Sprite>();
      sprites.add(Sprite.candlestand);
      sprites.add(Sprite.candlestand2);
      sprites.add(Sprite.pillar);
      sprites.add(Sprite.pillar2);
      sprites.add(Sprite.bracelet);
      sprites.add(Sprite.pot);
      sprites.add(Sprite.barrel);
      sprites.add(Sprite.barrel2);
      sprites.add(Sprite.fountain);
      sprites.add(Sprite.fountain2);
      sprites.add(Sprite.box);

      sprites.add(Sprite.blueLight2);
      sprites.add(Sprite.blueLight3);
      sprites.add(Sprite.blueLight4);


      sprites.add(Sprite.plantInPot);
      sprites.add(Sprite.blueLight);
      sprites.add(Sprite.fancyBarrel);
      sprites.add(Sprite.fancyBarrel2);


      visibleSprites = new ArrayList<>();

      this.addMouseListener(new MouseInput());
      this.addKeyListener(new KeyInput());

      setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
      setResizable(false);
      setTitle("MoonScape");
      setLayout(new BorderLayout());
      rays = new ArrayList<Ray>();
      player = new Player();
      timer = new Timer(16,this);
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
      test = System.currentTimeMillis();

      while(running) {

          if (State == STATE.GAME) {
              if (System.currentTimeMillis() - test > 350) {
              	
              	for (int i = 0; i < mobs.size();) {
              		Mob mob = mobs.get(i);
              		enemies(mob);
              		
              		if (mob.isRemoved()) {
              			int lastMob = mobs.size()-1;
              			mobs.set(i, mobs.get(lastMob));
              			mobs.remove(lastMob);
              		}
              		else {
              			i++;
              		}
              	}

                  test = System.currentTimeMillis();
              }
              timer.start();
          }
          else if (State == STATE.MENU) {
              try {
                  renderMenu();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

      }

  }

  public void enemies(Mob mo){


          Ray mobToPlayer = mo.rangeOfView(player);
          mobToPlayer.cast(mo.xPosition, mo.yPosition);
          double mobToPlayerDistance = Ray.distanceBetweenPoints(mo.xPosition, mo.yPosition,player.x,player.y);
          double mobToWallDistance = Ray.distanceBetweenPoints(mo.xPosition, mo.yPosition, mobToPlayer.wallHitX, mobToPlayer.wallHitY);



          if(mobToPlayerDistance < mobToWallDistance) {

              mo.update(player);

            if(mo.angle <  Game.FOV_ANGLE/4){
              if (player.checkAttackRange(mo, mobToPlayerDistance)) {
                  if (mo.health == 1) {
                      mo.removed = true;
                  }
                  System.out.println(mo.health + " took damage");
                  mo.health -= 1;

                  player.attacking = false;
              }
          }

          }
  }


  public void renderSpriteProjection() {

      // Very similar to wall projection, with the added check of the sprite being visible or not to the player
      // Where the visible sprites are then added to the visibleSprites ArrayList, then cleared when the method is called again
      // In order to clear the screen

      visibleSprites.clear();
      for (Sprite sprite : sprites) {
          sprite.visibleSprite(player);
          if (sprite.visible && !sprite.isRemoved) {
              visibleSprites.add(sprite);
          }
      }


      for (Mob mob : mobs) {
          mob.visibleSprite(player);
          if (mob.visible) {
              visibleSprites.add(mob);
          }
      }


      if (visibleSprites.size() > 0) {
          for (int i = 0; i < visibleSprites.size(); i++) {
              for (int j = i + 1; j < visibleSprites.size(); j++) {
                  if (visibleSprites.get(i).distance < visibleSprites.get(j).distance) {
                      Sprite s = visibleSprites.get(i);
                      visibleSprites.set(i, visibleSprites.get(j));
                      visibleSprites.set(j, s);
                  }
              }
          }
      }

      if (visibleSprites.size() > 0){
          for (Sprite visibleSprite : visibleSprites) {


              double correctWallDistance = visibleSprite.distance * Math.cos(visibleSprite.angle);

              double spriteHeight = (TILE_SIZE / correctWallDistance) * playerDistance;
              double spriteWidth = spriteHeight;

              double spriteTopY = (WINDOW_HEIGHT / 2) - (spriteHeight / 2);
              spriteTopY = (spriteTopY < 0) ? 0 : spriteTopY;

              double spriteBottomY = (WINDOW_HEIGHT / 2) + (spriteHeight / 2);
              spriteBottomY = (spriteBottomY > WINDOW_HEIGHT) ? WINDOW_HEIGHT : spriteBottomY;

              double spriteAngle = Math.atan2(visibleSprite.yPosition - player.y,
                      visibleSprite.xPosition - player.x) - player.rotationAngle;

              double spriteScreenPosX = Math.tan(spriteAngle) * playerDistance;

              double spriteLeftX = (WINDOW_WIDTH / 2) + spriteScreenPosX - (spriteWidth / 2);
              double spriteRightX = spriteLeftX + spriteWidth;

              int TexspriteWidth = visibleSprite.SIZE;
              int TexspriteHeight = visibleSprite.SIZE;


              for (int x = (int) spriteLeftX; x < spriteRightX; x++) {

                  double texelWidth = (TexspriteWidth / spriteWidth);
                  int offSetX = (int) ((x - spriteLeftX) * texelWidth);

                  offSetX = offSetX < 0 ? 1: offSetX;

                  for (int y = (int) spriteTopY; y < spriteBottomY; y++) {
                      if (x > 0 && x < WINDOW_WIDTH && y > 0 && y < WINDOW_HEIGHT) {
                          int distanceFromTop = (int) (y + (spriteHeight / 2) - (WINDOW_HEIGHT / 2));
                          int offSetY = (int) (distanceFromTop * ((float) TexspriteHeight / spriteHeight));

                          int texelColor = visibleSprite.pixels[((TexspriteWidth * offSetY) + offSetX)];


                          if ((  texelColor != guardColor.getRGB() && texelColor != Color.MAGENTA.getRGB())  && visibleSprite.distance < rays.get(x).distance) {
                              pixels[x + (y * WINDOW_WIDTH)] = texelColor;

                          }


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
          // Door texture
          int vertWallTex = 5;
          int horzWallTex = 5;

          int textNum = ray.hitWallColor - 1;


          double correctWallDistance = ray.distance * Math.cos(ray.rayAngle - player.rotationAngle);
          double wallStripHeight = playerDistance * (TILE_SIZE /correctWallDistance);

          int wallTopPixel = (int) ((WINDOW_HEIGHT/2) - (wallStripHeight/2));
          wallTopPixel = wallTopPixel < 0 ? 0 : wallTopPixel;
          int wallBottomPixel =(int) ((WINDOW_HEIGHT/2) + (wallStripHeight/2));
          wallBottomPixel = wallBottomPixel > WINDOW_HEIGHT ? WINDOW_HEIGHT : wallBottomPixel;



          int offSetX;
          if(ray.wasHitVertical){
              if(textNum > textures.size()){
                  switch(textNum) {
                      case 65:
                          textNum = vertWallTex;
                          break;
                      case 68:
                          textNum = 2;
                          break;
                      case 21:
                          textNum = vertWallTex;
                          break;
                      case 99:
                          textNum = 9;
                          break;
                  }
              }
              offSetX = (int)ray.wallHitY % textures.get(textNum).SIZE;
          }else{
              if(textNum > textures.size()){
                  switch(textNum) {
                      case 65:
                          textNum = 2;
                          break;
                      case 68:
                          textNum = horzWallTex;
                          break;
                      case 21:
                          textNum = 1;
                          break;
                      case 99:
                          textNum = horzWallTex;
                          break;
                  }
              }
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

              if(ray.wasHitVertical) {
                  texColor = textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX];

              }
              //I have no idea why the fuck this "else" line works but it does
              //it shades the horizontal sides of the walls a darker shade.
              else {
                  texColor = (textures.get(textNum).pixels[((textures.get(textNum).SIZE) * offSetY) + offSetX] >> 1) & 8355711;
              }
              pixels[i + (x * WINDOW_WIDTH)] = texColor;

          }
          //Coloring the floor

          for(int n=wallBottomPixel; n<WINDOW_HEIGHT; n++) {
              pixels[(WINDOW_WIDTH * n)+i] = Color.DARK_GRAY.getRGB();

          }

          i++;
      }



  }
  private void renderMenu() throws IOException {

      bs = getBufferStrategy();
      if (bs == null){
          createBufferStrategy(2);
          return;
      }
      Graphics2D g = (Graphics2D) bs.getDrawGraphics();

      BufferedImage image = new BufferedImage(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
      BufferedImageLoader loader = new BufferedImageLoader();

      Graphics2D g2d = (Graphics2D) g;

      Rectangle playButton = new Rectangle(Game.WIDTH / 2 + 575, 450, 100, 50);
      Rectangle quitButton = new Rectangle(Game.WIDTH / 2 + 575, 550, 100, 50);

      try {
          image = loader.loadImage("res/MoonScapeMenu.png");
      } catch (IOException e) {
          e.printStackTrace();
      }

      g.drawImage(image, 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);

      Font fnt1 = new Font("arial", Font.BOLD, 30);
      g.setFont(fnt1);
      g.setColor(Color.white);
      g.drawString("Play", playButton.x + 19, playButton.y + 30);
      g2d.draw(playButton);

      g.drawString("Quit", quitButton.x + 19, quitButton.y + 30);
      g2d.draw(quitButton);

      g.dispose();
      bs.show();
  }


  public void winScreen() {
   	 bs = getBufferStrategy();
       if (bs == null){
           createBufferStrategy(2);
           return;
       }
       Graphics2D g = (Graphics2D) bs.getDrawGraphics();
   	
   	BufferedImage image = new BufferedImage(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
   	BufferedImageLoader loader = new BufferedImageLoader();
   	
   	try {
   		image = loader.loadImage("res/space.jpg");
   	} catch (IOException e) {
   		e.printStackTrace();
   	}
   	
   	g.drawImage(image, 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);		
   	
   	Font fnt0 = new Font("arial", Font.BOLD, 75);
   	g.setFont(fnt0);
   	g.setColor(Color.white);
   	g.drawString("YOU WIN!", Game.WIDTH / 2 + 450, 250);
   	
   	Font fnt1 = new Font("arial", Font.BOLD, 30);
   	g.setFont(fnt1);
   	g.drawString("Press ESC", 550, 550);

   	g.dispose();
   	bs.show();
   }
  
  public void loseScreen() {
	   	 bs = getBufferStrategy();
	       if (bs == null){
	           createBufferStrategy(2);
	           return;
	       }
	       Graphics2D g = (Graphics2D) bs.getDrawGraphics();
	   	
	   	BufferedImage image = new BufferedImage(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
	   	BufferedImageLoader loader = new BufferedImageLoader();
	   	
	   	try {
	   		image = loader.loadImage("res/space.jpg");
	   	} catch (IOException e) {
	   		e.printStackTrace();
	   	}
	   	
	   	g.drawImage(image, 0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, null);		
	   	
	   	Font fnt0 = new Font("arial", Font.BOLD, 75);
	   	g.setFont(fnt0);
	   	g.setColor(Color.white);
	   	g.drawString("GAME OVER", Game.WIDTH / 2 + 400, 250);
	   	
	   	Font fnt1 = new Font("arial", Font.BOLD, 30);
	   	g.setFont(fnt1);
		g.drawString("Press ESC", 550, 550);

	   	g.dispose();
	   	bs.show();
	   }


  private void render() {
      bs = getBufferStrategy();
      if (bs == null){
          createBufferStrategy(2);
          return;
      }
      Graphics2D g = (Graphics2D) bs.getDrawGraphics();
      g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
      player.drawHealthbar(g);
      player.drawWeapon(g);


//      //////////////////
//      // MINIMAP CODE //
//      //////////////////
      for(int i = 0; i <MAP_NUM_ROWS; i++) {
          for (int j = 0; j < MAP_NUM_COLS; j++) {
              int x = j * TILE_SIZE;
              int y = i * TILE_SIZE;
              Color tileColor = map[i][j] > 0 ? Color.PINK : Color.LIGHT_GRAY;
              g.setColor(tileColor);
              g.fillRect((int) (Math.ceil(MINIMAP_SCALE_FACTOR * x)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * y)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE))
              );
              g.setColor(Color.BLACK);
              g.drawRect((int) (Math.ceil(MINIMAP_SCALE_FACTOR * x)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * y)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE)),
                      (int) (Math.ceil(MINIMAP_SCALE_FACTOR * TILE_SIZE))
              );
          }
      }

      //       // Figuring out the MOB ray
//      for(Mob mob : mobs) {
//          Ray yy = mob.rangeOfView(player);
//          yy.cast(mob.xPosition, mob.yPosition);
//          g.setColor(Color.MAGENTA);
//          g.fillOval((int) mob.xPosition, (int) mob.yPosition, (int) 10, (int) 10);
//          g.drawLine((int) mob.xPosition, (int) mob.yPosition, (int) yy.wallHitX, (int) yy.wallHitY);
//
//
//
//      }


//      //RAYS
//      g.setColor(Color.CYAN);
//      for(int i = 0; i < rays.size(); i++){
//          g.drawLine((int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x)),
//                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y)),
//                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitX)),
//                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * rays.get(i).wallHitY))
//          );
//          //g.draw(new Line2D.Double(player.x, player.y, f.get(i).wallHitX, f.get(i).wallHitY));
//      }
      //PLAYER ICON
//        g.setColor(Color.BLACK);
//      g.fillOval((int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x-5)),
//              (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y-5)),
//              (int) (Math.ceil(MINIMAP_SCALE_FACTOR * 10)),
//              (int) (Math.ceil(MINIMAP_SCALE_FACTOR * 10))
//      );
//     // DIRECTION LINE
      g.setColor(Color.blue);
      g.drawLine( (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.x)),
                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.y)),
                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.vectorX())),
                  (int) (Math.ceil(MINIMAP_SCALE_FACTOR * player.vectorY()))
      );

       // Attack vector
//      Ray test = new Ray (player.rotationAngle);
//      test.cast(player.x, player.y);
//      g.setColor(Color.GREEN);
//      g.draw(new Line2D.Double(MINIMAP_SCALE_FACTOR * player.x,
//              MINIMAP_SCALE_FACTOR * player.y,
//              MINIMAP_SCALE_FACTOR * test.wallHitX,
//              MINIMAP_SCALE_FACTOR * test.wallHitY));




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
          ray.cast(player.x,player.y);
          rays.add(ray);
          rayAngle += FOV_ANGLE/NUM_RAYS;

      }
      return rays;
  }

  public static boolean hasWallAt(double newX, double newY){
      if(newX >= MAP_NUM_COLS * TILE_SIZE || newX < 0 || newY <0 || newY >= MAP_NUM_ROWS * TILE_SIZE){
          return true;
      }
      int wallX = (int)Math.floor(newX/TILE_SIZE);
      int wallY = (int)Math.floor(newY/TILE_SIZE);
      return (map[wallY][wallX] != 0 && map[wallY][wallX] != 77);
  }

  // for the mob, so it doesn't go through the sprite objects
//  public static boolean hasObjectAt(double newX, double newY) {
//
//  }

  public static int mapIndexAt (double x, double y){
      if(x < 0 || x > MAP_NUM_COLS * TILE_SIZE || y < 0 || y >  MAP_NUM_ROWS * TILE_SIZE){
          return 0;
      }
      int wallX = (int)Math.floor(x/TILE_SIZE);
      int wallY = (int)Math.floor(y/TILE_SIZE);
      return map[wallY][wallX];
  }
  public static int getYMapIndex (double y){
      return (int)(y/TILE_SIZE);
  }
  public static int getXMapIndex(double x){
      return (int)(x/TILE_SIZE);
  }

  //Updating Player position 60 times per second (see timer delay)
  @Override
  public void actionPerformed(ActionEvent e) {

      if (State == State.GAME) {
      	renderingWalls(pixels,createAllRays());
          renderSpriteProjection();
          player.update(map);
         // enemies();

          render();
      }
      else if (State == State.MENU) {
      	try {
  			renderMenu();
  		} catch (IOException e1) {
  			// TODO Auto-generated catch block
  			e1.printStackTrace();
  		}
      }
      else if (State == State.WON) {
    	  winScreen();
      }
      else if (State == State.LOST) {
    	  loseScreen();
      }
  }

  // No one cares about main =D
  public static void main(String[] args){
      new Game();
  }
}
