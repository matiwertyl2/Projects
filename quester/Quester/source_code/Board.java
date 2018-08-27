import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class Board extends JPanel implements ActionListener, KeyListener {
  Timer t= new Timer(40, this);
  public Player player;
  public Vector <Mob> mobs;
  public Vector <Item> items;
  public Quest quest;
  public Terrain terrain;
  public Minimap minimap;
  public WeaponInterface weaponInterface;
  public ItemInterface itemInterface;

  public Vector<Integer> arrows;
  public boolean attackButton;
  public boolean speedButton;
  public boolean pauseButton;

  public Board () {

    addKeyListener(this);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);

    TerrainGenerator teren= new TerrainGenerator(Const.mapSize);
    this.terrain= teren.generate();
    teren.print();
    this.player=new Player(400, 300);
    this.mobs= new Vector<Mob> ();
    this.items= new Vector<Item> ();
    this.quest= new Quest(this.player);
    this.quest.ensureNotColided(this);
    this.minimap=new Minimap();
    this.weaponInterface= new WeaponInterface();
    this.itemInterface= new ItemInterface();

    this.arrows= new Vector <Integer> ();
    attackButton=false;
    speedButton=false;
    pauseButton=false;

    t.start();

  }

  public void paint(Graphics g) {
    super.paintComponent(g);
    paintBlackSpace(g);
    this.terrain.drawBackground(g, player, this);

    Vector<BoardObject> objects = this.terrain.prepareTrees(player, this);
    quest.draw(g, this);
    for (Mob mob : mobs)   objects.addElement(mob);
    for (Item item : items) item.draw(g);
    objects.addElement(player);
    Collections.sort(objects);
    for (BoardObject o : objects) o.draw(g);

    minimap.updateImage(terrain.map, player, quest);
    minimap.draw(g);
    quest.displayInterface(g, this);
    weaponInterface.updateImage(player.weapons);
    weaponInterface.draw(g, this);
    itemInterface.updateImage(player.items, player.itemsCount, player.activeItemNumber);
    itemInterface.draw(g, this);
  }

  public void paintBlackSpace (Graphics g) {
    g.setColor(Color.black);
    int w= getWidth();
    int h= getHeight();
    if (player.x-w/2 < -Const.gameSize) {
      g.fillRect(0, 0, -Const.gameSize-(player.x-w/2), h);
    }
    if (player.x+w/2 >Const. gameSize) {
      g.fillRect(Const.gameSize-player.x+w/2, 0, player.x+w/2-Const.gameSize, h);
    }
    if (player.y-h/2 < -Const.gameSize) {
      g.fillRect(0, 0, w, -Const.gameSize-(player.y-h/2));
    }
    if (player.y+h/2 >Const.gameSize) {
      g.fillRect(0, Const.gameSize-player.y+h/2, w, player.y+h/2- Const.gameSize);
    }
  }

  public void actionPerformed (ActionEvent e) {
      update();
      repaint();
  }

  public void keyPressed (KeyEvent e) {
    int code =e.getKeyCode();
    if (code==KeyEvent.VK_UP) {
      if (arrows.isEmpty() || arrows.lastElement()!=1) arrows.addElement(1);
    }
    if (code==KeyEvent.VK_DOWN) {
      if (arrows.isEmpty() || arrows.lastElement()!=2) arrows.addElement(2);

    }
    if (code==KeyEvent.VK_RIGHT) {
      if ( arrows.isEmpty() || arrows.lastElement()!=3) arrows.addElement(3);
    }
    if (code==KeyEvent.VK_LEFT) {
      if ( arrows.isEmpty() || arrows.lastElement()!=4) arrows.addElement(4);
    }
    if (code==KeyEvent.VK_SPACE) {
      speedButton=true;
    }
    if (code==KeyEvent.VK_1) {
      attackButton=true;
      player.setActiveWeapon(0);
    }
    if (code==KeyEvent.VK_2) {
      attackButton=true;
      player.setActiveWeapon(1);
    }
    if (code==KeyEvent.VK_3) {
      attackButton=true;
      player.setActiveWeapon(2);
    }
    if (code==KeyEvent.VK_4) {
      attackButton=true;
      player.setActiveWeapon(3);
    }
    if (code==KeyEvent.VK_R) player.removeItem();
    if (code==KeyEvent.VK_E) player.nextItem();
    if (code==KeyEvent.VK_W) player.useItem(this);
    if (code==KeyEvent.VK_Q) player.previousItem();
    if (code==KeyEvent.VK_P && pauseButton==false) {
        t.stop();
        pauseButton=true;
    }
  }

  public void keyTyped(KeyEvent e) {

  }

  public void keyReleased(KeyEvent e) {
    int code =e.getKeyCode();
    if (code==KeyEvent.VK_UP) {
      arrows.removeElement(1);
    }
    if (code==KeyEvent.VK_DOWN) {
      arrows.removeElement(2);
    }
    if (code==KeyEvent.VK_RIGHT) {
       arrows.removeElement(3);
    }
    if (code==KeyEvent.VK_LEFT) {
      arrows.removeElement(4);
    }
    if (code==KeyEvent.VK_SPACE) {
      speedButton=false;
    }
    if (code==KeyEvent.VK_1 || code==KeyEvent.VK_2 || code==KeyEvent.VK_3 || code==KeyEvent.VK_4) {
      attackButton=false;
    }
  }


  public void update () {
    player.updateVelocity(arrows, speedButton);
    updateBoardCoordinates(); // w razie zmieniania wielkosci ekranu
    spawnMobs();
    spawnItems();
    updateCounters();

    player.move();
    for (Mob mob : mobs) {
      mob.randomMove(player, this);
    }
    player.stoppedByObject(terrain.map, mobs, player);
    for (Mob mob : mobs) mob.stoppedByObject(terrain.map, mobs, player);
    boardMove(-player.velx, -player.vely);

    if (attackButton) player.attack(mobs, this);
    for (Mob mob : mobs) mob.attack(player, this);
    player.collectItems(items);
  }

  public void boardMove (int dx, int dy) {
    for (Mob mob : mobs) mob.boardMove(dx, dy);
    for (Item item : items) item.boardMove(dx, dy);
    quest.boardMove(dx, dy);
  }

  public void updateBoardCoordinates() {
    int oldx=player.boardX;
    int oldy=player.boardY;
    player.updateBoardCoordinates(this);
    for (Mob mob : mobs){
      mob.updateBoardCoordinates(player.boardX-oldx, player.boardY-oldy);
    }
    for (Item item : items) {
      item.updateBoardCoordinates(player.boardX-oldx, player.boardY-oldy);
    }
    quest.updateBoardCoordinates(player.boardX-oldx, player.boardY-oldy);
  }

  public void updateCounters() {
    for (Weapon weapon : player.weapons) {
      weapon.updateCounter();
    }
    for (Mob mob : mobs) {
      mob.weapon.updateCounter();
    }
  }

  public void spawnMobs() {
     Mob mobsToSpawn[]= {new Minion(0, 0, player), new Golem(0, 0, player), new Dragon(0, 0, player)};

    for (int i=0; i<mobsToSpawn.length; i++) {
      Mob mob=mobsToSpawn[i];
      int diff = mob.rarity - mob.count(mobs);
      for (int j=0; j<diff; j++) {
        Mob newMob=mob.copy();
        newMob.ensureNotColided(this);
        mobs.addElement(newMob);
      }
    }
  }

  public void spawnItems() {
    for (int i=0; i<Const.boardItems.length; i++) {
      Item item=Const.boardItems[i];
      int diff = item.rarity - item.count(items);
      for (int j=0; j<diff; j++) {
        Item newItem=item.copy();
        newItem.ensureNotColided(this);
        items.addElement(newItem);
      }
    }
  }



}
