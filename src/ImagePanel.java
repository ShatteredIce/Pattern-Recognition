import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	private BufferedImage raw = null;
	private BufferedImage blurred = null;
	private BufferedImage outline = null;
	private BufferedImage mod_outline = null;
	private BufferedImage overlay = null;
	private Image raw_scaled = null;
	private Image blurred_scaled = null;
	private Image outline_scaled = null;
	private Image mod_outline_scaled = null;
	private Image overlay_scaled = null;
	private int imgType = 0;
	private int imgScaledHeight = 0;
	private int imgScaledWidth = 0;
	private int lastHeight = 0;
	private int lastWidth = 0;
	
	public ImagePanel(){
		super();
	}
	
	public void setImages(BufferedImage newraw, BufferedImage newblurred, BufferedImage newoutline, BufferedImage newmodoutline, BufferedImage newoverlay){
		raw = newraw;
		blurred = newblurred;
		outline = newoutline;
		mod_outline = newmodoutline;
		overlay = newoverlay;
		setImageDimensions(newraw);
		raw_scaled =  newraw.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		blurred_scaled =  newblurred.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		outline_scaled = newoutline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		mod_outline_scaled = newmodoutline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = newoverlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		//imgType = 0;
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
			return blurred;
		case 2:
			return outline;
		case 3:
			return mod_outline;
		case 4:
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
        	g.drawImage(blurred_scaled, 0, 0, this); 
        	break;
        case 2:
        	g.drawImage(outline_scaled, 0, 0, this); 
        	break;
        case 3:
        	g.drawImage(mod_outline_scaled, 0, 0, this); 
        	break;
        case 4:
        	g.drawImage(overlay_scaled, 0, 0, this); 
        	break;
        }         
    }
	 
	public void resizeImage() {
		setImageDimensions(raw);
		raw_scaled =  raw.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		outline_scaled = outline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		mod_outline_scaled = mod_outline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		blurred_scaled = blurred.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = overlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
	}

}