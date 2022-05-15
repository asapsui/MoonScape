/* Sam Imose
 * The mobs can currently shoot through walls, but I just need to implement the hasWallAt method.
 * The mobs aren't the best at finding the player, once it hits a wall. (Need to fix the math) 
 */
import java.util.Hashtable;

public class Mob extends Sprite {


	// this will never change
	enum Movement{
		Standing, 
		Walk_1, Walk_2, Walk_3, Walk_4,
		Shoot_1, Shoot_2
	}


	public static Hashtable<Movement, Texture> guardMovement1 = new Hashtable<>(){{
		put(Movement.Standing, new Texture("res/guard/mguard_s_1.bmp", 64));
		put(Movement.Walk_1, new Texture("res/guard/mguard_w1_1.bmp", 64));
		put(Movement.Walk_2, new Texture("res/guard/mguard_w2_1.bmp", 64));
		put(Movement.Walk_3, new Texture("res/guard/mguard_w3_1.bmp", 64));
		put(Movement.Walk_4, new Texture("res/guard/mguard_w4_1.bmp", 64));
		put(Movement.Shoot_1, new Texture("res/guard/mguard_shoot2.bmp", 64));
		put(Movement.Shoot_2, new Texture("res/guard/mguard_shoot3.bmp", 64));
	}};


	public static Hashtable<Movement, Texture> guardMovement2 = new Hashtable<>(){{
		put(Movement.Standing, new Texture("res/guard/difEnemy/9.bmp", 64));
		put(Movement.Walk_1, new Texture("res/guard/difEnemy/1.bmp", 64));
		put(Movement.Walk_2, new Texture("res/guard/difEnemy/2.bmp", 64));
		put(Movement.Walk_3, new Texture("res/guard/difEnemy/3.bmp", 64));
		put(Movement.Walk_4, new Texture("res/guard/difEnemy/4.bmp", 64));
		put(Movement.Shoot_1, new Texture("res/guard/difEnemy/10.bmp", 64));
		put(Movement.Shoot_2, new Texture("res/guard/difEnemy/11.bmp", 64));
	}};

	public static Hashtable<Movement, Texture> guardMovement3 = new Hashtable<>(){{
		put(Movement.Standing, new Texture("res/guard/bigBoy/trans_aim.bmp", 64));
		put(Movement.Walk_1, new Texture("res/guard/bigBoy/trans_w1.bmp", 64));
		put(Movement.Walk_2, new Texture("res/guard/bigBoy/trans_w2.bmp", 64));
		put(Movement.Walk_3, new Texture("res/guard/bigBoy/trans_w3.bmp", 64));
		put(Movement.Walk_4, new Texture("res/guard/bigBoy/trans_w4.bmp", 64));
		put(Movement.Shoot_1, new Texture("res/guard/bigBoy/trans_fire1.bmp", 64));
		put(Movement.Shoot_2, new Texture("res/guard/bigBoy/trans_fire2.bmp", 64));
	}};


	public  Hashtable<Movement, Texture> guardMovement = new Hashtable<>();
	public double speed;
	public double constantSpeed;
	public int health;
    public int maxHealth;
	int walk = 1;
	int beginWalking = 1;
	
	// every shot we decrease the enemies speed by like .10
	public static Mob guard2 = new Mob(guardMovement1.get(Movement.Standing).location, 64, 775, 130, 6,2, guardMovement1);

    public static Mob guard1 = new Mob(guardMovement2.get(Movement.Standing).location, 64, 110, 128, 6,5, guardMovement2);


	public static Mob guard3 = new Mob(guardMovement1.get(Movement.Standing).location, 64, 896, 820, 6,1, guardMovement1);
	public static Mob guard4 = new Mob(guardMovement1.get(Movement.Standing).location, 64, 1152, 800, 6,2, guardMovement1);
	public static Mob guard5 = new Mob(guardMovement2.get(Movement.Standing).location, 64, 1188, 317, 6,5, guardMovement2);


	public static Mob bigGuy = new Mob(guardMovement3.get(Movement.Standing).location, 64, 1400, 700, 10,15, guardMovement3);


	public final int textureOffset = 10;
	public int SHOOT_RANGE = 190;

    // this'll be used for when the player has killed a mob
		// 	we will remove it from the arraylist and stop rendering it
	public boolean removed;

	double startingPosX, startingPosY;
	// all mobs will start out with the standing image
	public Mob(String location, int size, double x, double y, double speed, int health, Hashtable<Movement, Texture> animation) {
		super(location, size, x, y);
		this.speed = speed;
		this.constantSpeed = speed;
		this.health = health;
		this.maxHealth = health;
		this.guardMovement.putAll(animation);
		this.removed = false;
		
	}
		
	public void update(Player player) {
		//wait(60);
		double moveX = player.getX();
		double moveY = player.getY();

		double diffX = moveX - xPosition;

		double diffY = moveY - yPosition;


		// for the change in position we need to be getting the players angle
		double angle = Math.atan2(diffY, diffX);

		// maybe play a sound to indicate that the mob is inflicting damage
		if (checkAttackRange(diffY, diffX)) {
			speed = 0;
			this.changeTexture(this.guardMovement.get(Movement.Shoot_2));
			// should be != 0
			if (player.health != 0) {
				// update the players health bar
				player.health -= 1;
				//player.drawHealthbar(g);
				System.out.println(player.health);
			}
		} else {
			// change the walk between the 4 walk types
			speed = constantSpeed;
			chooseWalk();
			if (walk != 4) {
				walk++;
			} else {
				walk = beginWalking;
			}
		}
		double newXPos = speed * Math.cos(angle) + this.xPosition;
		double newYPos = speed * Math.sin(angle) + this.yPosition;

		// this doesn't work correctly
		if (!Game.hasWallAt(newXPos , newYPos ) && (xPosition != player.getX() || yPosition != player.getY()) ) {
			this.xPosition = newXPos;
			this.yPosition = newYPos;
			//System.out.println("guard is walking");
		}else{
			this.changeTexture(this.guardMovement.get(Movement.Standing));

		}

//		else if (!Game.hasWallAt(newXPos, newYPos)) {
//			if (xPosition != player.getX() || yPosition != player.getY()) {
//				this.xPosition = newXPos;
//				this.yPosition = newYPos;
//
//
//			}
//		}

	}
	
	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	// they are shooting guns, so the range can be further away
	public boolean checkAttackRange(double distFromPlayerY, double distFromPlayerX) {
		return Math.abs(distFromPlayerY) <= SHOOT_RANGE && Math.abs(distFromPlayerX) <= SHOOT_RANGE;
	}
	public void setRange (int range){
		this.SHOOT_RANGE = range;
	}
	
	public void chooseWalk() {
		if (walk == 1) {
			this.changeTexture(this.guardMovement.get(Movement.Walk_1));
		}
		if (walk == 2) {
			this.changeTexture(this.guardMovement.get(Movement.Walk_2));
		}
		if (walk == 3) {
			this.changeTexture(this.guardMovement.get(Movement.Walk_3));
		}
		if (walk == 4) {
			this.changeTexture(this.guardMovement.get(Movement.Walk_4));
		}
		
	}
	
	// a method for the health
	// if the mob is shot x times, is has no health, so we remove from the game
	
	
	public boolean isRemoved() {
        return removed;
    }

    public void setVisibility(Boolean removed) {
        this.removed = removed;
    }

	public Ray rangeOfView (Player player){

		double moveX = player.getX();
		double moveY = player.getY();
		double diffX = moveX - xPosition;
		double diffY = moveY - yPosition;
		double angle = Math.atan2(diffY, diffX);
		return new Ray(angle);
	}
  
}
