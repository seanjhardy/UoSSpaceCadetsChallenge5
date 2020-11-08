/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spirograph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import static spirograph.GUIManager.getColour;

/**
 *
 * @author seanjhardy
 */
public class MainUI extends Menu{
    
    public MainUI(MainPanel parent){
        super(parent);
        createComponents();
    }
    
    public final void createComponents(){
      createImageButton("modeBtn","pen", "penHighlighted", "cogHighlighted", "cog", 
              ((MainPanel)parent).getModeBool(), getColour("banner"));
      createImageButton("playBtn","pause", "pauseHighlighted", "playHighlighted", "play", 
              ((MainPanel)parent).getPlayStateBool(), getColour("banner"));
      createImageButton("resetBtn","reset", "resetHighlighted", "resetHighlighted", "reset", 
              ((MainPanel)parent).getResetBool(), getColour("banner"));
      
      createImageButton("speedupBtn","speedup", "speedupHighlighted", "speedupHighlighted", "speedup", 
              ((MainPanel)parent).getSpeedupBool(), getColour("banner"));
      createImageButton("slowdownBtn","slowdown", "slowdownHighlighted", "slowdownHighlighted", "slowdown", 
              ((MainPanel)parent).getSlowdownBool(), getColour("banner"));
      
      createSlider("red","red", 0, 255, 0, 5, false,
              ((MainPanel)parent).getRedBool(), getColour("red"));
      createSlider("green","green", 0, 255, 0, 5, false,
              ((MainPanel)parent).getGreenBool(), getColour("green"));
      createSlider("blue","blue", 0, 255, 0, 5, false,
              ((MainPanel)parent).getBlueBool(), getColour("blue"));
    }
    
    public void render(Graphics2D g){
        g.setColor(getColour("background"));
        g.fillRect(0,0,width, 30);
        
        getComponent("modeBtn").setBounds(startX, startY + 5, 80, 80);
        getComponent("red").setBounds(startX + 80, startY + 5, 90, 26);
        getComponent("green").setBounds(startX + 80, startY + 5 + 26, 90, 26);
        getComponent("blue").setBounds(startX + 80, startY + 5 + 26*2, 90, 26);
        
        getComponent("slowdownBtn").setBounds(startX + 170, startY + 5, 80, 80);
        getComponent("playBtn").setBounds(startX + 250, startY + 5, 80, 80);
        getComponent("speedupBtn").setBounds(startX + 330, startY + 5, 80, 80);
        getComponent("resetBtn").setBounds(startX + 410, startY + 5, 80, 80);
        
        g.setStroke(new BasicStroke(5));
        g.setColor(getColour("banner"));
        g.fillRect(startX, startY, width, height);
        g.setColor(getColour("banner2"));
        g.drawLine(startX, startY + height, startX + width, startY + height);
    }
}
