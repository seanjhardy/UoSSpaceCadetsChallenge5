/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spirograph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Random;
import net.jafama.FastMath;
import static spirograph.GUIManager.addAlpha;
import static spirograph.GUIManager.getColour;

/**
 *
 * @author seanjhardy
 */
public class Cog {
  
  private Cog parent;
  private ArrayList<Cog> cogs;
  private ArrayList<Pen> pens;
  private double parentX, parentY;
  private double x, y, initialAngle, radius, angle, speed;
  private boolean highlighted = false;
  private int level = 0;

  
  public Cog(Cog parent, double x, double y, double radius, double speed){
    cogs = new ArrayList<>();
    pens = new ArrayList<>();
    this.parent = parent;
    this.parentX = x;
    this.parentY = y;
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.speed = speed;
    if(parent != null){
      level = parent.getLevel() + 1;
      this.angle = FastMath.atan2(parent.getY() - y, parent.getX() - x);
      initialAngle = angle;
    }
    step(0);
  }
  
  public void draw(Graphics2D g, Graphics2D spirograph, double step){
    float[] dash = new float[]{(float) Math.max(speed,1)};
    float phase = (float)(Math.atan2(Math.sin(angle), Math.cos(angle)) + Math.PI*2)*20;
    Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, 
              BasicStroke.JOIN_BEVEL, 0, dash, phase);
    g.setStroke(dashed);
    if(highlighted && step == 0){
      g.setColor(getColour("highlight"));
      g.fillOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
    }
    Color colour = getColour("foreground");
    if(step != 0){
      colour = addAlpha(colour, 40);
    }
    g.setColor(colour);
    g.drawOval((int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2));
    
    step(step);
    if(step == 0){
      resetPosition();
    }
    
    for(Pen pen : pens){
      pen.draw(g, spirograph, step);
    }
    for(Cog c : cogs){
      c.draw(g, spirograph, step);
    }
  }
  
  public void step(double step){
    if(parent == null) return;
    double radDiff = parent.getRadius() - radius;
    angle += ((radDiff)/radius) * step;
    x = parent.getX() - FastMath.cos(angle)*radDiff;
    y = parent.getY() - FastMath.sin(angle)*radDiff;
  }
  
  public void resetPosition(){
    if(parent == null) return;
    angle = initialAngle;
  }
  
  public double getSquaredDistToRadius(double px, double py){
    double vX = px - x;
    double vY = py - y;
    double magV = FastMath.sqrt(vX*vX + vY*vY);
    if(magV <= radius) return radius - magV;
    return magV - radius;
  }
  
  public double[] getClosestPointOnRadius(double px, double py){
    double vX = px - x;
    double vY = py - y;
    //distance to centre
    double magV = FastMath.sqrt(FastMath.pow(vX, 2) + FastMath.pow(vY, 2));
    double ax = x + (vX / magV) * radius;
    double ay = y + (vY / magV) * radius;
    
    return new double[]{ax, ay};
  }
  
  public ArrayList<Cog> randomise(ArrayList<Cog> mainCogs){
    Random rand = new Random();
    
    if(radius <= 20) return mainCogs;
    
    if(parent != null){
      int numPens = (int) (rand.nextGaussian()*3);
      for(int i = 0; i < numPens; i++){
        double randAngle = rand.nextDouble() * 2 * Math.PI;
        double randDist = rand.nextInt((int) radius);

        Color randColour = new Color(rand.nextInt(255), 
                                      rand.nextInt(255), 
                                      rand.nextInt(255), 150);
        Pen c = new Pen(this, randColour, randDist, randAngle);
        pens.add(c);
      }
    }
    
    if(level > 4) return mainCogs;
    
    int numCogs = (int) (rand.nextGaussian()*3);
    for(int i = 0; i < numCogs; i++){
      double randAngle = rand.nextDouble() * 2 * Math.PI;
      double randX = FastMath.cos(randAngle)*radius + x;
      double randY = FastMath.sin(randAngle)*radius + y;
      double randRadius = rand.nextInt((int) radius - 20) + 20;
      double randSpeed = rand.nextInt(18) + 2;
      Cog c = new Cog(this, randX, randY, randRadius, randSpeed);
      ArrayList<Cog> tempCogs = c.randomise(mainCogs);
      if(c.getPensInBranch() != 0){
        cogs.add(c);
        mainCogs = tempCogs;
      }
    }
    return mainCogs;
  }
  
  public int getPensInBranch(){
    int numPens = pens.size();
    for(Cog cog : cogs){
      numPens += cog.getPensInBranch();
    }
    return numPens;
  }
  
  public ArrayList<Cog> delete(ArrayList<Cog> mainCogs){
    mainCogs.remove(this);
    for(Cog cog : cogs){
      mainCogs = cog.delete(mainCogs);
    }
    return mainCogs;
  }
  
  public void setHighlighted(boolean h){
    this.highlighted = h;
  }
  
  public void setRadius(double radius){
    this.radius = radius;
    angle = FastMath.atan2(parent.getY() - parentY, parent.getX() - parentX);
    this.x = parentX + FastMath.cos(angle)*radius;
    this.y = parentY + FastMath.sin(angle)*radius;
  }
  
  public void removeCog(Cog cog){
    cogs.remove(cog);
  }
  
  public void addCog(Cog cog){
    cogs.add(cog);
  }
  
  public void addPen(Pen pen){
    pens.add(pen);
  }
  
  public void reset(){
    cogs = new ArrayList<>();
    pens = new ArrayList<>();
  }
  
  public Cog getParent(){
    return parent;
  }
  
  public double getRadius(){
    return radius;
  }
  
  public double getX(){
    return x;
  }
  
  public double getY(){
    return y;
  }
  
  public int getLevel(){
    return level;
  }
  
  public double getSpeed(){
    return speed;
  }
}
