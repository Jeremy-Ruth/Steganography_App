package StegoDesktop;

/* Created by: Jeremy Ruth - 04.25.2018
 
 * This class contains the methods related to creation and manipulation of image objects. Provides functionality
 * such as manipulation of the bit planes and color channels of the object as well setting image information like
 * dimensions and whether the image is color or grayscale.
 
 * The channels and bit planes include an array to store alpha values for future manipulation of images with transparency
 * Alphas can be stored in the current version but are not implemented in any way
 */

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageObject {
    private int[][] channelMatrix;
    private int[][][] bitPlaneMatList;
    private BufferedImage origImg;
    private int height, width;
    private boolean isColorImage, keyLocked;

    // Constructor for a blank image object when no image is provided
    public ImageObject(int blankWidth, int blankHeight, boolean wasColor) {
        height = blankHeight;
        width = blankWidth;
        isColorImage = wasColor;
        keyLocked = false;
        channelMatrix = new int[4][height * width];
        bitPlaneMatList = new int[4][8][height * width];

		// For the image bit planes set a black and white alternating pattern to assist with a more even appearance in the recovered image
        for (int i = 0; i < 4; i++) {                                   
            for (int j = 0; j < 8; j++) {                               
                if((i == 0) || (j <= 1) || (j%2 != 0))
                    Arrays.fill(bitPlaneMatList[i][j], 1);
            }
        }
    }//END ImageObject no image constructor

    // Constructor for an image object when an input image is provided
    public ImageObject(BufferedImage imgToProc, boolean wasColor) {
        height = imgToProc.getHeight();
        width = imgToProc.getWidth();
        if(imgToProc.getType() != BufferedImage.TYPE_INT_RGB) {
            // If the input image is a standard RGB image then convert it
			// Below is a "band-aid" to avoid complex handling of image types at this time. An external library would help here
			// for robust image detection and support for various file types
            BufferedImage convertor = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);       
            convertor.getGraphics().drawImage(imgToProc,0,0,null);                       
            convertor.getGraphics().dispose();                                                          
        }
		// Set the initial parameters based on the input image
        origImg = imgToProc;                            
        isColorImage = wasColor;
        keyLocked = false;
		// Break the image up into its respective color channels and bit planes and store all data
        setChannels();                       		
        setBitPlanes();                                 
    }//END ImageObject constructor with an initial image

    // Clears lower bits in an image in order to provide consistency for certain image conversions
    // Follows the same alternating pattern as in the image processing class
    public void embedBlanking (int numPlanesForSecret) {
        int tempCount = 0;
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if((numPlanesForSecret - tempCount) > 0) {
                    if ((j <= 1) || (j % 2 != 0))
                        Arrays.fill(bitPlaneMatList[i][j], 1);
                    else
                        Arrays.fill(bitPlaneMatList[i][j], 0);
                    tempCount++;
                }
            }
            tempCount = numPlanesForSecret;
        }
    }//END embedBlanking

    // Sets stored internal image and related channels and bit planes into current image object
    // Primarily for use with a blank image object created for secret embedding in which an image was not originally specified
    public void setImgObjVals (BufferedImage inputImage) {
        origImg = inputImage;
        setChannels();
        setBitPlanes();
    }//END setImageObjVals

    // Retrieves the initial image stored in an image object
    public BufferedImage getStoredImg () { return origImg; }

    // Sets all the color channels of the given image including an alpha channel if specified
    private void setChannels() {
		// The ARGB matrix
        channelMatrix = new int[4][width*height];                       
        int pixel, channelIndex;

        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++) {
                channelIndex = (j*width)+k;
				
				// Get the current pixel info: Alpha = index 0
				// Red = index 1 
				// Green = index 2
				// Blue  = index 3
                pixel = origImg.getRGB(k, j);                           
                channelMatrix[0][channelIndex] = (pixel>>24) & 0xff;    
                channelMatrix[1][channelIndex] = (pixel>>16) & 0xff;    
                channelMatrix[2][channelIndex] = (pixel>>8) & 0xff;     
                channelMatrix[3][channelIndex] = pixel & 0xff;          
            }
        }
    }//END setChannels

    // Provides the ability to set color channels values external to the current image object
    public void extSetChannels(int[][] channelsToUse) { channelMatrix = channelsToUse; }

    // Retrieves a color channel from the current image object for use in image manipulation
    public int[] getSingleChannel (int channelToGet) {
        int[] currChannel = channelMatrix[channelToGet];
        return currChannel;
    }//END getSingleChannel

    // Retrieves the specified color channel from the current image object for display purposes
    // Returned image is a grayscale raster image. Not directly suitable for use in image manipulation methods
    public BufferedImage getChannel(char channelIn) {
        BufferedImage returnChannel = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int level;

        if (channelIn == 'a') {
            level = 0;
        } else if (channelIn == 'r') {
            level = 1;
        } else if (channelIn == 'g') {
            level = 2;
        } else {
            level = 3;
        }
        returnChannel.getRaster().setPixels(0, 0, width, height, channelMatrix[level]);
        return returnChannel;
    }//END getChannel

    // Sets the values for all bit planes in the current image object. Requires that the color channels have already been created
    public void setBitPlanes() {
		// The entire bit plane matrix (ARGB and planes 0-7)
        bitPlaneMatList = new int[4][8][width*height];                  
        int channelPixel;
        int planeIndex = width*height;

		// For all ARGB channels (0 = A, 3 = B), set the current color channel and bit plane 0 through 7 values
        for (int i = 0; i < 4; i++) {                                   
            for(int j = 0; j < planeIndex; j++) {
                channelPixel = channelMatrix[i][j];                     
                bitPlaneMatList[i][0][j] = channelPixel & 0x01;         
                bitPlaneMatList[i][1][j] = (channelPixel>>1) & 0x01;    
                bitPlaneMatList[i][2][j] = (channelPixel>>2) & 0x01;    
                bitPlaneMatList[i][3][j] = (channelPixel>>3) & 0x01;    
                bitPlaneMatList[i][4][j] = (channelPixel>>4) & 0x01;    
                bitPlaneMatList[i][5][j] = (channelPixel>>5) & 0x01;    
                bitPlaneMatList[i][6][j] = (channelPixel>>6) & 0x01;    
                bitPlaneMatList[i][7][j] = (channelPixel>>7) & 0x01;    
            }
        }
    }//END setBitPlanes

    // Retrieves all bit planes in the current image object for the purpose of display
    // Returns an array of binary raster images. Not directly suitable for use in image manipulation methods
    public BufferedImage[] getBitPlanes(char channelIn) {
        BufferedImage[] bitPlanes = new BufferedImage[8];
        int planesFromChannel;

        if(channelIn == 'a') {
            planesFromChannel = 0;
        }else if(channelIn == 'r') {
            planesFromChannel = 1;
        }else if(channelIn == 'g') {
            planesFromChannel = 2;
        }else {
            planesFromChannel = 3;
        }
		// Brute force retrieval method to create all the needed binary image files. Needs to be re-evaluated in the future
        BufferedImage tempPlane0 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);              
        BufferedImage tempPlane1 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);              
        BufferedImage tempPlane2 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage tempPlane3 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);              
        BufferedImage tempPlane4 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage tempPlane5 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage tempPlane6 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage tempPlane7 = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);

		// Add the bit plane info to each image
        tempPlane0.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][0]);         
        bitPlanes[0] = tempPlane0;                                                                              
        tempPlane1.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][1]);
        bitPlanes[1] = tempPlane1;
        tempPlane2.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][2]);
        bitPlanes[2] = tempPlane2;
        tempPlane3.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][3]);
        bitPlanes[3] = tempPlane3;
        tempPlane4.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][4]);
        bitPlanes[4] = tempPlane4;
        tempPlane5.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][5]);
        bitPlanes[5] = tempPlane5;
        tempPlane6.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][6]);
        bitPlanes[6] = tempPlane6;
        tempPlane7.getRaster().setPixels(0,0,width,height,bitPlaneMatList[planesFromChannel][7]);
        bitPlanes[7] = tempPlane7;
        return bitPlanes;
    }//END getBitPlanes

    // Set a bit plane in the current image object. Primarily used for adding secret images to the cover
    // Takes into account smaller secret images and adds padding to match the size of the cover image
    // In future versions it would be beneficial to add random noise to the padding to hide the secret better
    public void setSinglePlane(int[] planeInput, int channel, int bitNum, int[] secretDims) {
        int pixelsLeft = planeInput.length;
        int widthGap = width - secretDims[1];
        int pixelFromSecret = 0;
        int pixelsInLine = 0;
        int pixelsInGap = 0;

        // Since the bit planes are stored as a 1D array it is necessary to track positioning:
		// For every bit in the plane, as long as there are pixels left and while the width of the secret image hasn't been reached,
		// add the secret's bit to the current cover's bit plane and adjust the sentry values
        for (int i = 0; i < bitPlaneMatList[channel][bitNum].length; i++) {                 
            if (pixelsLeft > 0) {                                                           
                if (pixelsInLine < secretDims[1]) {                                         
                    bitPlaneMatList[channel][bitNum][i] = planeInput[pixelFromSecret];      
                    pixelsLeft--;                                                           
                    pixelsInLine++;
                    pixelFromSecret++;
                } else {
					// The current row in the secret image is done, but the cover is larger, so add a "black" bit to the plane for padding
                    bitPlaneMatList[channel][bitNum][i] = 0;                                
                    pixelsInGap++;                                                          
                }

				// If the max expected amount of padding has been reached for the current width based row, reset the gap sentries
                if (pixelsInGap == widthGap){                                               
                    pixelsInGap = 0;                                                        
                    pixelsInLine = 0;
                }
            } else                                                                          
				// When all of the pixels in the secret image have been read, if the height of the cover is larger add padding
                bitPlaneMatList[channel][bitNum][i] = 0;                                    
        }
    }//END setSinglePlane

    // Returns a matrix output of a specified bit plane for display or processing
    public int[] getSinglePlane(int channel, int bitNum) {
        int[] planeArry = bitPlaneMatList[channel][bitNum];
        return planeArry;
    }//END getSinglePlane

    // Sets an indicator flag bit in the stego-Object that identifies whether the secret image is grayscale or color
    public void setColorFlag(boolean secretWasColor) {
		// Set the x:0, y:0 bit of plane 0 in the blue channel to zero to be interpreted as the flag bit
        if (secretWasColor)
            bitPlaneMatList[3][0][0] = 1;                               
        else
            bitPlaneMatList[3][0][0] = 0;
    }//END setColorFlag

    // Determines if the stored secret in a stego-object is color or grayscale by reading the bit flag from the LSB bit plane
    public boolean readColorFlag() {1
		// Assume a black and white secret initially
        boolean colorSecret= false;                                     

		// Read the bit flag and update result to color if needed
        if (bitPlaneMatList[3][0][0] == 1)                              
            colorSecret = true;
        return colorSecret;
    }//END readColorFlag

    // Sets the indicator flags in the stego-Object that identifies how many bit planes are being used by the secret
    public void setPlaneFlags(int numOfPlanes) {
        for(int j = 1; j < 7; j++) {
			// Bits 1-7 of plane 0 are used in the blue channel of the stego-Object 
			// in order to identify how many planes are dedicated to the secret image
            if(numOfPlanes != 0) {
                bitPlaneMatList[3][0][j] = 1;               
                numOfPlanes--;                              
            }else
                bitPlaneMatList[3][0][j] = 0;
        }
    }//END setPlaneFlags

    // Determines how many bit planes were used for the secret in a stego-object by reading the bit plane flags embedded into image
    public int readPlaneFlags() {
        int secretPlanes = 0;

        for(int i = 1; i < 7; i++) {
			// Determines the number of planes to get from the cover 
			// by reading bit 1-7 of plane 0 in the blue channel of the stego-object
            if(bitPlaneMatList[3][0][i] == 1)               
                secretPlanes++;                             
            else
                break;
        }
        return secretPlanes;
    }//END readPlaneFlags

    // Set the indicator flag for whether a key was used with the secret. Is set but not fully utilized in this version
    public void setKeyFlag(boolean flagStatus) {
		// Sets the last pixel in the blue channel of stego-object to be used as the flag bit
        if(flagStatus)
            bitPlaneMatList[3][0][(height*width)-1] = 1;            
        else
            bitPlaneMatList[3][0][(height*width)-1] = 0;
    }//END setKeyFlag

    // Determines if a key image was used on the embedded secret
    public boolean readKeyFlag() {
		// Reads the last pixel of the blue channel in the stego-object
        if (bitPlaneMatList[3][0][(height*width)-1] == 1)       
            return true;
        else
            return false;
    }// END readKeyFlag

    // Combine the 24-bit pixel values back together into the final image representation
    // The current version does not honor the alpha channel which is discarded in the final image output
    public BufferedImage combToRGB(ImageObject keyImage, int numPlanesSecret) {
        BufferedImage rgbImage;

		// Re-assemble the bit planes to 8-bit values
        channelMatrix = combBitPlanes();                                                            
        if(keyLocked) {
            removeKey(keyImage);
        } else
            this.embedBlanking(numPlanesSecret);

		// Re-assemble the 8-bit color channel values to 24-bit RGB values
        int[] pixelArry = combChannels(channelMatrix);           
			
		// Create an empty color or black and white image 	
        if(isColorImage)
            rgbImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);                  
        else
            rgbImage = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);                

		// For each pixel in the ARGB image dimensions write the RGB pixel value to the output image
        for(int i = 0; i < height; i++) {                                                           
            for(int j = 0; j < width; j++) {
                rgbImage.setRGB(j,i,pixelArry[(i*width)+j]);                                        
            }
        }
        return rgbImage;
    }//END combToRGB

    // Remove the key image from the secret when recovering the embedded secret
    private void removeKey(ImageObject keyIn) {
        int channelLength;
        int[] tempKeyChannel;
        int bitsToShift;
        int currChannelPix, currKeyPix;

        for(int i = 1; i < 4; i++){
			// Get all of the channels in the recovered secret image one by one, reusing a single channel from the key (since all are the same)
            channelLength = channelMatrix[i].length;                                                    
            tempKeyChannel = keyIn.getSingleChannel(1);

			// For each pixel reverse the locking steps:
			// Get a value to bit shift by using a prime number and shift the locked bits to the right
			// Subtract the key in a circular manner, range: 0 - 255
            for(int j = 0; j < channelLength; j++) {
                currChannelPix = channelMatrix[i][j];                                                   
                currKeyPix = tempKeyChannel[j];
                bitsToShift = currKeyPix % 7;                                                           
                currChannelPix = ImageProcs.rotateRight(currChannelPix,bitsToShift);                    
                currChannelPix = (currChannelPix - currKeyPix);

				// If the number is negative correct to the original value before storing the pixel
                if(currChannelPix < 0)                                                                  
                    channelMatrix[i][j] = 255 - Math.abs(currChannelPix);
                else                                                                                    
                    channelMatrix[i][j] = currChannelPix;
            }
        }
		// When done set the recovered image object flag to "no key"
        keyLocked= false;                                                                               
    }//END removeKey

    // Combines the 8-bit ARGB channel pixels of the current image object into the final 24bit PNG pixels
    // While the alpha channel is included it is not used in this version and will be discarded during final conversion
    private int[] combChannels(int[][] channelMatIn) {
        int[] tempARGBArry = new int[width*height];
        int argbPixel;
	
		// For each image pixel combine the ARGB values (currently interpreted ignoring transparency 'A') and store to the pixel image array
        for (int j = 0; j < width*height; j++) {                                                    
            argbPixel = (channelMatIn[0][j]<<24) | (channelMatIn[1][j]<<16) |                   
                    (channelMatIn[2][j]<<8) | channelMatIn[3][j];                               
            tempARGBArry[j] = argbPixel;                                                        
        }
        return tempARGBArry;
    }//END combChannels

    // Combines the separated bit planes of the image object back into the RGB 8-bit channel pixels
    // Currently neglects the alpha channel
    public int[][] combBitPlanes() {
        int[][] tempPlaneComp = new int[4][width * height];
        int imgIndex = width * height;
        int currPixel;

		// For all RGB channels (1 = R, 3 = B) and for each pixel of the image OR the bit planes together and assign them to a channel pixel
        for (int i = 1; i < 4; i++) {                                                               
            for (int j = 0; j < imgIndex; j++) {                                                    
                currPixel = (bitPlaneMatList[i][7][j] << 7) | (bitPlaneMatList[i][6][j] << 6) |     
                        (bitPlaneMatList[i][5][j] << 5) | (bitPlaneMatList[i][4][j] << 4) |
                        (bitPlaneMatList[i][3][j] << 3) | (bitPlaneMatList[i][2][j] << 2) |
                        (bitPlaneMatList[i][1][j] << 1) | bitPlaneMatList[i][0][j];
                tempPlaneComp[i][j] = currPixel;                                                    
            }
        }
        return tempPlaneComp;
    }//END combBitPlanes

    // Returns the height and width of the current image object
    public int[] getDims() {
        int[] currObjDims = new int[] {height, width};
        return currObjDims;
    }//END getDims

    /* MAY NOT BE NEEDED DEPENDING ON ADDED FUNCTIONALITY */
    // sets the stored dimensions of the image object externally
    public void setDims(int newHeight, int newWidth) {
        height = newHeight;
        width = newWidth;
    }//END setDims

    public void setOrigImg(BufferedImage imageToStore) {
        origImg = imageToStore;
    }

    // Return whether the current image object is specified as a color or black and white image
    public boolean getColorStat() { return isColorImage; }

    // Updates the color status indicator of the current ImageObject
    public void setColorStat(boolean colorStatus) { isColorImage = colorStatus; }

    // Read the color image indicator stored in the current ImageObject
    public boolean wasSecretColor() { return isColorImage; }

    // Set the flag variable to mark whether the image an ImageObject was created from had a key applied or not
    public void markAsLocked() { keyLocked = true; }
    public void markAsUnlocked() { keyLocked = false; }
}

