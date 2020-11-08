/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spirograph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import net.jafama.FastMath;
import static spirograph.GUIManager.brightness;

/**
 *
 * @author seanjhardy
 */
public class Pen {
  
  private Cog parent;
  private double initialAngle, distToCentre, angle;
  private double lastX, lastY;
  private double x, y;
  private Color colour;
  
  public Pen(Cog parent, Color colour, double distance, double angle){
    this.parent = parent;
    this.colour = colour;
    this.distToCentre = distance;
    this.initialAngle = angle;
    this.angle = angle;
    //set coords
    x = parent.getX() + FastMath.cos(angle)*distToCentre;
    y = parent.getY() + FastMath.sin(angle)*distToCentre;
  }
  
  public void draw(Graphics2D g, Graphics2D spirograph, double step){
    g.setColor(colour);
    g.fillOval((int) (x-5), (int) (y-5), 10, 10);
    
    if(spirograph != null){
      step(step);
      spirograph.setColor(colour);
      spirograph.setStroke(new BasicStroke(2));
      spirograph.drawLine((int)lastX, (int)lastY, (int)x, (int)y);
    }
    if(step == 0){
      resetPosition();
    }
  }
  
  public void step(double step){
    lastX = x;
    lastY = y;
    if(parent == null) return;
    angle -= step;
    x = parent.getX() + FastMath.cos(angle)*distToCentre;
    y = parent.getY() + FastMath.sin(angle)*distToCentre;
  }
  
  public void resetPosition(){
    angle = initialAngle;
    step(0);
    lastX = x;
    lastY = y;
  }
}
