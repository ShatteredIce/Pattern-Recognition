import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	private Main program;
	private BufferedImage raw = null;
	private BufferedImage outline = null;
	private BufferedImage overlay = null;
	private Image raw_scaled = null;
	private Image outline_scaled = null;
	private Image overlay_scaled = null;
	private int imgType = 0;
	private int imgScaledHeight = 0;
	private int imgScaledWidth = 0;
	private int lastHeight = 0;
	private int lastWidth = 0;
	
	public ImagePanel(){
		super();
	}
	
	public void setImages(BufferedImage newraw, BufferedImage newoutline, BufferedImage newoverlay){
		raw = newraw;
		outline = newoutline;
		overlay = newoverlay;
		setImageDimensions(newraw);
		raw_scaled =  newraw.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		outline_scaled = newoutline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = newoverlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		imgType = 0;
	}
	
	public void setImageDimensions(BufferedImage image){
		int myWidth = this.getWidth();
		int myHeight = this.getHeight();
		double widthScalar = ((double) myWidth) / image.getWidth();
		double heightScalar = ((double) myHeight) / image.getHeight();
		if(widthScalar <= heightScalar){
			imgScaledWidth = myWidth;
			imgScaledHeight = (int) (image.getHeight() * widthScalar);
		}
		else{
			imgScaledHeight = myHeight;
			imgScaledWidth = (int) (image.getWidth() * heightScalar);
		}
	}
	
	public void setImgType(int newImgType){
		imgType = newImgType;
	}
	
	public BufferedImage getCurrentImage(){
		switch(imgType){
		case 0:
			return raw;
		case 1:
			return outline;
		case 2:
			return overlay;
		}
		return null;
	}
	
	 @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if((raw != null) && (lastWidth != this.getWidth() || lastHeight != this.getHeight())){
        	resizeImage();
        }
        switch(imgType) {
        case 0:
        	g.drawImage(raw_scaled, 0, 0, this); 
        	break;
        case 1:
        	g.drawImage(outline_scaled, 0, 0, this); 
        	break;
        case 2:
        	g.drawImage(overlay_scaled, 0, 0, this); 
        	break;
        }         
    }
	 
	public void resizeImage() {
		setImageDimensions(raw);
		raw_scaled =  raw.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		outline_scaled = outline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = overlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
	}

}