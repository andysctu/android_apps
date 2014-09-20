
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
 
import javax.imageio.ImageIO;
 
public class GrayMyImage {
 
    public static void main(String args[]) {
        GrayMyImage imageGrayer = new GrayMyImage();
        String inputImageFilePath="C:/Users/AndyT/Pictures/icon.png";
        String outputImageFilePath="C:/Users/AndyT/Pictures/iconGrey.png";
         
        System.out.println("Reading input image...");
        BufferedImage inputImage = imageGrayer.readImage(inputImageFilePath);
        System.out.println("Successfully Read Image: "+inputImageFilePath);
 
        System.out.println("\nConverting the image to Gray colors.");
        BufferedImage grayedOut = imageGrayer.grayOut(inputImage);
        System.out.println("Successful...");
 
        System.out.println("\nWriting gray image to filesystems.");
        imageGrayer.writeImage(grayedOut,outputImageFilePath , "jpg");
        System.out.println("Successfully Wrote Image To: "+outputImageFilePath);
    }
     
    /**
     * This method converts any input BufferedImage
     * object to the gray color and returns it.
     */
    public BufferedImage grayOut(BufferedImage img) {
        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace
                .getInstance(ColorSpace.CS_GRAY), null);
        colorConvert.filter(img, img);
 
        return img;
    }
 
    /**
     * This method reads an image from the file
     * @param fileLocation -- > eg. "C:/testImage.jpg"
     * @return BufferedImage of the file read
     */
    public BufferedImage readImage(String fileLocation) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
 
    /**
     * This method writes a buffered image to a file
     * @param img -- > BufferedImage
     * @param fileLocation --> e.g. "C:/testImage.jpg"
     * @param extension --> e.g. "jpg","gif","png"
     */
    public void writeImage(BufferedImage img, String fileLocation,
            String extension) {
        try {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            ImageIO.write(bi, extension, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}