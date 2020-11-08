/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spirograph;

import java.awt.Color;
import static spirograph.GUIManager.getColour;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import net.jafama.FastMath;
import static spirograph.GUIManager.tintImage;

/**
 *
 * @author seanjhardy
 */
public final class MainPanel extends JPanel implements MouseMotionListener,
      MouseListener{

  private GUIManager parent;
  private MainUI mainUI;
  private Rectangle frameBounds;
  private BufferedImage image;

  private ArrayList<Cog> cogs;
  private Cog mainCog;
  private Cog currentCog, selectedCog, highlightedCog;
  
  private boolean canAddCog = true;
  private ButtonVariable addMode = new ButtonVariable(false);
  private ButtonVariable playState = new ButtonVariable(false);
  private ButtonVariable reset = new ButtonVariable(false);
  private ButtonVariable speedup = new ButtonVariable(false);
  private ButtonVariable slowdown = new ButtonVariable(false);
  
  private ButtonVariable red = new ButtonVariable(false);
  private ButtonVariable green = new ButtonVariable(false);
  private ButtonVariable blue = new ButtonVariable(false);
  
  private double mouseX;
  private double mouseY;
  private double stepSize = 0.01;

  public MainPanel(GUIManager parent){
    this.parent = parent;
    cogs = new ArrayList<>();

    mainCog = new Cog(null, 225, 225 + 100, 225, 20);
    cogs.add(mainCog);

    mainUI = new MainUI(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    setBindings();
    setBackground(getColour("background"));
  }
  
  public void setBindings(){
    InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap am = getActionMap();
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "random");
    am.put("cancel", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
          if(currentCog != null){
            currentCog = null;
            canAddCog = false;
          }
          if(selectedCog != null){
            selectedCog = null;
            highlightedCog.setHighlighted(false);
            highlightedCog = null;
          }
        }
    });
    am.put("delete", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
          if(selectedCog != null){
            cogs = selectedCog.delete(cogs);
            selectedCog.getParent().removeCog(selectedCog);
            selectedCog = null;
            highlightedCog = null;
          }
        }
    });
    am.put("random", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
          reset();
          Random rand = new Random();
          while(mainCog.getPensInBranch() == 0){
            for(int i = 0; i < 3; i++){
              double randAngle = rand.nextDouble() * 2 * Math.PI;
              double randX = FastMath.cos(randAngle)*mainCog.getRadius() + mainCog.getX();
              double randY = FastMath.sin(randAngle)*mainCog.getRadius() + mainCog.getY();
              double randRadius = rand.nextInt((int) mainCog.getRadius());
              double randSpeed = rand.nextInt(8)+2;
              Cog c = new Cog(mainCog, randX, randY, randRadius, randSpeed);
              ArrayList<Cog> newCogs = c.randomise(cogs);
              if(c.getPensInBranch() != 0){
                cogs.add(c);
                mainCog.addCog(c);
                cogs = newCogs;
              }
            }    
          }
        }
    });
  }

  @Override
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    //draw background
    g.setColor(getColour("background"));
    g.fillRect(0,0,1920,1080);
    //render components
    processComponentUpdates();
    renderSpirograph(g2);
    //draw the menu
    mainUI.resize(0, 0, (int)frameBounds.getWidth(), 90); 
    mainUI.render(g2);
    //repaint
    revalidate();
    repaint();
  }

  public void renderSpirograph(Graphics2D g2){
    frameBounds = this.getBounds();
    double step = 0;
    if(playState.getValue() != playState.getLastState()){
      playState.setLastState(playState.getValue());
      image = new BufferedImage((int)frameBounds.getWidth(), (int)frameBounds.getHeight(),
      BufferedImage.TYPE_INT_ARGB);
    }
    if(playState.getValue()){
      step = stepSize;
      g2.drawImage(image, null, 0, 0);
      if(image != null){
        image = tintImage(image, 255, 255, 255, 254);
      }
    }
    Graphics2D spirograph = image != null ? (Graphics2D) image.getGraphics() : null;
    mainCog.draw(g2, spirograph, step);
    if(currentCog != null){
      currentCog.draw(g2, spirograph, step);
    }
  } 
  
  public void processComponentUpdates(){
    if(reset.getValue()){
      reset();
      playState.setValue(false);
    }
    
    if(speedup.getValue()){
      speedup.setValue(false);
      stepSize *= 1.5;
    }
    if(slowdown.getValue()){
      slowdown.setValue(false);
      stepSize /= 1.5;
    }
  }
  
  public void reset(){
    cogs = new ArrayList<>();
      cogs.add(mainCog);
      reset.setValue(false);
      mainCog.reset();
      selectedCog = null;
      image = new BufferedImage((int)frameBounds.getWidth(), (int)frameBounds.getHeight(),
      BufferedImage.TYPE_INT_ARGB);
  }
  
  public ButtonVariable getModeBool(){
    return addMode;
  }
  
  public ButtonVariable getPlayStateBool(){
    return playState;
  }
  
  public ButtonVariable getResetBool(){
    return reset;
  }
  
  public ButtonVariable getSpeedupBool(){
    return speedup;
  }
  
  public ButtonVariable getSlowdownBool(){
    return slowdown;
  }
  
  public ButtonVariable getRedBool(){
    return red;
  }
  
  public ButtonVariable getGreenBool(){
    return green;
  }
  
  public ButtonVariable getBlueBool(){
    return blue;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    if(selectedCog != null){
      mouseX = e.getPoint().getX();
      mouseY = e.getPoint().getY();
      double distance = FastMath.sqrt(FastMath.pow(mouseX - selectedCog.getX(), 2) + 
              FastMath.pow(mouseY - selectedCog.getY(), 2));
      if(distance < selectedCog.getRadius()){
        double rotation = FastMath.atan2(mouseY - selectedCog.getY(), mouseX - selectedCog.getX());
        
        int r = ((JSlider)mainUI.getComponent("red")).getValue();
        int g = ((JSlider)mainUI.getComponent("green")).getValue();
        int b = ((JSlider)mainUI.getComponent("blue")).getValue();
        if(r == 0 && g == 0 && b == 0){
          Random rand = new Random();
          r = rand.nextInt(255);
          g = rand.nextInt(255);
          b = rand.nextInt(255);
        }
        Color colour = new Color(r,g,b, 150);
        Pen pen = new Pen(selectedCog, colour, distance, rotation);
        selectedCog.addPen(pen);
      }
    }
    if(highlightedCog != null){
      selectedCog = highlightedCog;
    }
  }
  
  @Override
  public void mouseDragged(MouseEvent e) {
    if (addMode.getValue() || playState.getValue()){
      return;
    }
    if(currentCog != null){
      double newMouseX = e.getPoint().getX();
      double newMouseY = e.getPoint().getY();
    
      double radius = FastMath.sqrt(FastMath.pow(newMouseX - mouseX, 2) + FastMath.pow(newMouseY - mouseY, 2));
      
      radius = FastMath.min(radius, currentCog.getParent().getRadius());
      currentCog.setRadius(radius);
    }else if(canAddCog){
      mouseX = e.getPoint().getX();
      mouseY = e.getPoint().getY();

      //get the closest cog to the mouse
      Cog closestCog = null;
      double[] position = new double[2];
      double minDistance = -1;

      for(Cog cog : cogs){
        double distance = cog.getSquaredDistToRadius(mouseX, mouseY);
        if(distance < minDistance || minDistance == -1){
          minDistance = distance; 
          position = cog.getClosestPointOnRadius(mouseX, mouseY);
          closestCog = cog;
        }
      }
      double speed = 3;
      currentCog = new Cog(closestCog, position[0], position[1], 10, speed);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (playState.getValue() || selectedCog != null){
      return;
    }
    if(!addMode.getValue()){
      return;
    }
    mouseX = e.getPoint().getX();
    mouseY = e.getPoint().getY();

    //get the closest cog to the mouse
    Cog closestCog = null;
    double minDistance = -1;
    if(highlightedCog != null){
      highlightedCog.setHighlighted(false);
    }

    for(Cog cog : cogs){
      double distance = FastMath.sqrt(FastMath.pow(mouseX - cog.getX(), 2) + 
              FastMath.pow( mouseY - cog.getY(), 2));
      if(cog == mainCog){
        continue;
      }
      if(distance < cog.getRadius()){
        if((closestCog != null && closestCog.getLevel() <= cog.getLevel()) ||
           closestCog == null){
          minDistance = distance; 
          closestCog = cog;
        }
      }
    }
    highlightedCog = closestCog;
    if(highlightedCog != null){
      highlightedCog.setHighlighted(true);
    }
  }

  
  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    canAddCog = true;
    if(currentCog != null){
      if(currentCog.getRadius() > 15){
        cogs.add(currentCog);
        currentCog.getParent().addCog(currentCog);
      }
      currentCog = null;
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  
  }
}
