import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	private BufferedImage raw = null;
	private BufferedImage blurred = null;
	private BufferedImage outline = null;
	private BufferedImage endpoints = null;
	private BufferedImage overlay = null;
	private Image raw_scaled = null;
	private Image blurred_scaled = null;
	private Image outline_scaled = null;
	private Image endpoints_scaled = null;
	private Image overlay_scaled = null;
	private int imgType = 0;
	private int selectedShape = 0;
	private int imgScaledHeight = 0;
	private int imgScaledWidth = 0;
	private int lastHeight = 0;
	private int lastWidth = 0;
	final Color endPointColor = new Color(0, 255, 0);
	final Color selectedPointColor = new Color(0, 0, 255);
	ArrayList<ArrayList<int[]>> allEndpoints = new ArrayList<>();
	
	public ImagePanel(){
		super();
	}
	
	public void setImages(BufferedImage newraw, BufferedImage newblurred, BufferedImage newoutline, BufferedImage newmodoutline, BufferedImage newoverlay){
		raw = newraw;
		blurred = newblurred;
		outline = newoutline;
		endpoints = newmodoutline;
		overlay = newoverlay;
		setImageDimensions(newraw);
		raw_scaled =  newraw.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		blurred_scaled =  newblurred.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		outline_scaled = newoutline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		endpoints_scaled = newmodoutline.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = newoverlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		//imgType = 0;
		selectedShape = 0;
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
	
	public void drawVertex(){
		for (int p = 0; p < allEndpoints.size(); p++) {
			ArrayList<int[]> vertices = allEndpoints.get(p);
			for (int i = 0; i < vertices.size(); i++) {
				int endpointWidth = 1;
				for (int j = -endpointWidth; j <= endpointWidth; j++) {
					for (int k = -endpointWidth; k <= endpointWidth; k++) {
						if(p == selectedShape){
							endpoints.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, selectedPointColor.getRGB());
							overlay.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, selectedPointColor.getRGB());
						}
						else{
							endpoints.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, endPointColor.getRGB());
							overlay.setRGB(vertices.get(i)[0]+j, vertices.get(i)[1]+k, endPointColor.getRGB());
						}
					}
				}
			}
		}
	}
	
	public void setEndpoints(ArrayList<ArrayList<int[]>> newAllEndpoints){
		allEndpoints = newAllEndpoints;
	}
	
	public void setImgType(int newImgType){
		imgType = newImgType;
	}
	
	public void setSelectedShape(int newShape){
		selectedShape = newShape;
	}
	
	public int getSelectedShape(){
		return selectedShape;
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
			return endpoints;
		case 4:
			return overlay;
		}
		return null;
	}
	
	 @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawVertex();
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
        	g.drawImage(endpoints_scaled, 0, 0, this); 
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
		endpoints_scaled = endpoints.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		blurred_scaled = blurred.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
		overlay_scaled = overlay.getScaledInstance(imgScaledWidth,imgScaledHeight,BufferedImage.SCALE_SMOOTH);
	}

}