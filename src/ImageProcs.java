package StegoDesktop;

/* Created by: Jeremy Ruth - 04.25.2018

 * This class contains the Image Processing methods for the stegongraphy app.
 * Includes functionality for performing tasks such as applying a key and viewing color channels etc
 * Not all methods are user accessible in the GUI version and are instead useful during testing.
 */

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;

public class ImageProcs {

    // Converts a given image into an ImageObject for processing and bit manipulation
    public static ImageObject bakeImage(BufferedImage inputImage, boolean wasColor) {
        ImageObject currImage = new ImageObject(inputImage, wasColor);
        return currImage;
    }//END bakeImage

    // Displays the specified image with the option to view the color channels if needed
    public static DisplayPanel[] showImage(BufferedImage inputImage, ImageObject currImgObj, boolean viewChannels, String usrTitle) {
        DisplayPanel[] currPanelSet;

        // If viewing the channels, create the necessary display panels and titles then add them to the panel set
        if (viewChannels) {
            currPanelSet = new DisplayPanel[4];
            BufferedImage redCH = currImgObj.getChannel('r');
            DisplayPanel dispRedCH = new DisplayPanel(redCH, "Red Channel");
            BufferedImage greenCH = currImgObj.getChannel('g');
            DisplayPanel dispGreenCH = new DisplayPanel(greenCH, "Green Channel");
            BufferedImage blueCH = currImgObj.getChannel('b');
            DisplayPanel dispBlueCH = new DisplayPanel(blueCH, "Blue Channel");
            currPanelSet[1] = dispRedCH;
            currPanelSet[2] = dispGreenCH;
            currPanelSet[3] = dispBlueCH;
        } else
            currPanelSet = new DisplayPanel[1];

		// Always assign the primary image as the first image in the panel set
        DisplayPanel currDisp = new DisplayPanel(inputImage, usrTitle);         
        currPanelSet[0] = currDisp;
        return currPanelSet;
    }//END showImage

    // Displays a bit plane for the specified image
    public static BufferedImage showSinglePlane(ImageObject currImgObj, char channelToUse, int bitPlaneToGet) {
        BufferedImage[] imagePlaneSet;

        // Get the user preference of which color channel's bit planes to display
        if (channelToUse == 'r')
            imagePlaneSet = currImgObj.getBitPlanes('r');
        else if (channelToUse == 'g')
            imagePlaneSet = currImgObj.getBitPlanes('g');
        else
            imagePlaneSet = currImgObj.getBitPlanes('b');

        return imagePlaneSet[bitPlaneToGet];
    }// END showSinglePlane

    // Displays all of the bit planes for the specified image
    public static DisplayPanel[] showPlanes(ImageObject currImgObj, char channelToUse) {
        BufferedImage[] imagePlaneSet;
        DisplayPanel[] planePanelSet = new DisplayPanel[8];

        // Get the user preference of which color channel's bit planes to display
        if (channelToUse == 'r')
            imagePlaneSet = currImgObj.getBitPlanes('r');
        else if (channelToUse == 'g')
            imagePlaneSet = currImgObj.getBitPlanes('g');
        else
            imagePlaneSet = currImgObj.getBitPlanes('b');

        // Brute force method to assign and display all panels of the bit planes 
		// A more efficient means should be implemented at a later date, possibly with an external library 
        DisplayPanel dispPlane0 = new DisplayPanel(imagePlaneSet[0], "Plane 0 Channel: " + channelToUse);
        planePanelSet[0] = dispPlane0;
        DisplayPanel dispPlane1 = new DisplayPanel(imagePlaneSet[1], "Plane 1 Channel: " + channelToUse);
        planePanelSet[1] = dispPlane1;
        DisplayPanel dispPlane2 = new DisplayPanel(imagePlaneSet[2], "Plane 2 Channel: " + channelToUse);
        planePanelSet[2] = dispPlane2;
        DisplayPanel dispPlane3 = new DisplayPanel(imagePlaneSet[3], "Plane 3 Channel: " + channelToUse);
        planePanelSet[3] = dispPlane3;
        DisplayPanel dispPlane4 = new DisplayPanel(imagePlaneSet[4], "Plane 4 Channel: " + channelToUse);
        planePanelSet[4] = dispPlane4;
        DisplayPanel dispPlane5 = new DisplayPanel(imagePlaneSet[5], "Plane 5 Channel: " + channelToUse);
        planePanelSet[5] = dispPlane5;
        DisplayPanel dispPlane6 = new DisplayPanel(imagePlaneSet[6], "Plane 6 Channel: " + channelToUse);
        planePanelSet[6] = dispPlane6;
        DisplayPanel dispPlane7 = new DisplayPanel(imagePlaneSet[7], "Plane 7 Channel: " + channelToUse);
        planePanelSet[7] = dispPlane7;
        return planePanelSet;
    }// END showPlanes

    // Creates the "stego-object" by placing a specified number of bit planes from the secret into the lower bit planes of 
    // the cover image. The number of planes choice is overidden in the special case of using a key image due to it's requirements 
    public static ImageObject embedSecret(ImageObject coverToUse, ImageObject secretToUse, int numPlanesForSecret, boolean usingKey) {
        int[] coverDims = coverToUse.getDims();		
        ImageObject tempImage = new ImageObject(coverDims[1], coverDims[0], coverToUse.getColorStat());     
        int planeOffset = 8 - numPlanesForSecret;
        int[] tempPlaneArry;

		// set the internal image, channels, and planes of the temp image object
        tempImage.setImgObjVals(coverToUse.getStoredImg()); 
		
        /* If a key was used, embed specifically for the key method, else do regular LSB embedding
		 * Brute force copying of key-locked secret to LSB of cover was done to save time. Needs optimized at a later time
		 * Copies entire grayscale bit planes of key locked secret into the lowest 3 bit planes of the cover image
		 */
        if(usingKey) {
            tempPlaneArry = secretToUse.getSinglePlane(1,0);                        
            tempImage.setSinglePlane(tempPlaneArry,1,0,secretToUse.getDims());      
            tempPlaneArry = secretToUse.getSinglePlane(1,1);                        
            tempImage.setSinglePlane(tempPlaneArry,2,0,secretToUse.getDims());      
            tempPlaneArry = secretToUse.getSinglePlane(1,2);
            tempImage.setSinglePlane(tempPlaneArry,3,0,secretToUse.getDims());
            tempPlaneArry = secretToUse.getSinglePlane(1,3);
            tempImage.setSinglePlane(tempPlaneArry,1,1,secretToUse.getDims());
            tempPlaneArry = secretToUse.getSinglePlane(1,4);
            tempImage.setSinglePlane(tempPlaneArry,2,1,secretToUse.getDims());
            tempPlaneArry = secretToUse.getSinglePlane(1,5);
            tempImage.setSinglePlane(tempPlaneArry,3,1,secretToUse.getDims());
            tempPlaneArry = secretToUse.getSinglePlane(1,6);
            tempImage.setSinglePlane(tempPlaneArry,1,2,secretToUse.getDims());
            tempPlaneArry = secretToUse.getSinglePlane(1,7);
            tempImage.setSinglePlane(tempPlaneArry,2,2,secretToUse.getDims());

			// Set the needed flags in the stego-object to indicate a key image was used
            tempImage.setColorFlag(false);                                                          
            tempImage.setPlaneFlags(3);                                                            
            tempImage.setKeyFlag(true);
        }else {
			// Clean up call to remove data from the cover before embedding. May not be needed any longer
            tempImage.embedBlanking(numPlanesForSecret);

			// For every color channel copy the number of bit planes specified from the MSB of the secret into the LSB of the cover
            for (int i = 1; i < 4; i++) {                                                       
                for (int j = (numPlanesForSecret - 1); j >= 0; j--) {                           
                    tempPlaneArry = secretToUse.getSinglePlane(i, j + planeOffset);     
                    tempImage.setSinglePlane(tempPlaneArry, i, j, secretToUse.getDims());       
                }
            }
			
			// Set the recovery flag that indicates whether secret image is color or not, to determine the number of planes the secret
			// uses in the stego-object, and specify basic LSB embedding was used
            tempImage.setColorFlag(secretToUse.getColorStat());                             
            tempImage.setPlaneFlags(numPlanesForSecret);                                    
            tempImage.setKeyFlag(false);                                                    
        }
        return tempImage;
    }// END embedSecret

    // Retrieves the secret image from the stego-object
    public static BufferedImage getSecret(ImageObject stegObjIn, boolean bypassKey, BufferedImage keyImage) {
        /* NEED TO ADD DETECTION STAGE FOR KEY IMAGE IF THAT FEATURE IS LATER DESIRED */
        
		// Get the number of bit planes from the secret image that were embedded
		int numPlanesToGet = stegObjIn.readPlaneFlags();                                                
        BufferedImage tempRecovered;
        int[] tempPlane;
		
		// Get stego-object dims: [0]=height [1]=width
        int[] stegoDims = stegObjIn.getDims();                                                          
        int planeOffset = 8 - numPlanesToGet;
        ImageObject keyToUse = null;
		
		// Create a blank image object for secret retrieval
        ImageObject tempImage = new ImageObject(stegoDims[1], stegoDims[0], stegObjIn.readColorFlag()); 
        boolean colorSecret;

        /* If a keyImage was used follow the specific procedure for recovery of the secret, else do generic LSB embedding recovery.
		 * Brute force copying method of secret image from LSB of cover image. Needs optimized at a later time.
		 * Copies the entire grayscale bit planes of a key locked secret from the red channel into the all RGB channels
		 * of recovered image to get original grayscale image back
		 */
        if( stegObjIn.readKeyFlag()) {
            for (int k = 1; k < 4; k++) {
                tempPlane = stegObjIn.getSinglePlane(1, 0);                 
                tempImage.setSinglePlane(tempPlane, k, 0, stegoDims);               
                tempPlane = stegObjIn.getSinglePlane(2, 0);                 
                tempImage.setSinglePlane(tempPlane, k, 1, stegoDims);               
                tempPlane = stegObjIn.getSinglePlane(3, 0);
                tempImage.setSinglePlane(tempPlane, k, 2, stegoDims);
                tempPlane = stegObjIn.getSinglePlane(1, 1);
                tempImage.setSinglePlane(tempPlane, k, 3, stegoDims);
                tempPlane = stegObjIn.getSinglePlane(2, 1);
                tempImage.setSinglePlane(tempPlane, k, 4, stegoDims);
                tempPlane = stegObjIn.getSinglePlane(3, 1);
                tempImage.setSinglePlane(tempPlane, k, 5, stegoDims);
                tempPlane = stegObjIn.getSinglePlane(1, 2);
                tempImage.setSinglePlane(tempPlane, k, 6, stegoDims);
                tempPlane = stegObjIn.getSinglePlane(2, 2);
                tempImage.setSinglePlane(tempPlane, k, 7, stegoDims);
            }
            colorSecret = false;                                                            
        }else {
			// For every color channel copy the number of bit planes specified from the LSB of the stego-object into the MSB of the recovered image
            for (int i = 1; i < 4; i++) {                                                           
                for (int j = (numPlanesToGet - 1); j >= 0; j--) {                                   
                    tempPlane = stegObjIn.getSinglePlane(i, j);                                     
                    tempImage.setSinglePlane(tempPlane, i, j + planeOffset, stegoDims);     
                }
            }
			// Determine if the original secret was color or grayscale
            colorSecret = stegObjIn.readColorFlag();                                                
        }

        tempImage.setColorStat(colorSecret);
		
		// If a key image is specified and is not an unusual file format convert the key to grayscale and process
		// (Unknown image types should be grayscale natively to avoid potential issues). Attempt to process the key 
		// image directly when it is not a recognized file format
		if(!bypassKey && keyImage != null) {                                                                       
            tempImage.markAsLocked();                                                                                     
            if (keyImage.getType() != BufferedImage.TYPE_CUSTOM) {                                                        
                BufferedImageOp convertor = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);   
                BufferedImage temp = convertor.filter(keyImage, null);                                              
                keyToUse = bakeImage(temp, false);                                                              
            }else
                keyToUse = bakeImage(keyImage,false);                                                           
        }
		// Process recovered temp to get bit planes and channels back
        tempRecovered = imgObjToRGB(tempImage, keyToUse, numPlanesToGet);                                               
        return tempRecovered;
    }//END getSecret

    // Converts the specified image object to an RGB image compatible with the PNG file format
    public static BufferedImage imgObjToRGB(ImageObject objToConvert, ImageObject keyImage, int numPlanes) {
        BufferedImage tempBuffImg = objToConvert.combToRGB(keyImage, numPlanes);
        return tempBuffImg;
    }//END imgObjToRGB

    // Close all the opened panels from a given set in the test system
    public static void closePanelSet(DisplayPanel[] panelSetToClose) {
        int numOfPanels = panelSetToClose.length;
        for (int i = 0; i < numOfPanels; i++) {
            panelSetToClose[i].closePanel();
        }
    }//END closePanelSet

    // Verifies the size of an image in relation to another and resizes if needed.
    // Secret must be same size or smaller than the cover, and key images must be the same size of the cover or larger
    public static BufferedImage checkImageSize(BufferedImage parentImg, BufferedImage childImg, boolean childWasKey) {
        BufferedImage updatedImage;
		
		// Used to process images when a resize is needed
        Graphics2D resizer;                                                                         
        int[] parentDims = new int[2], childDims = new int[2], compareDims = new int[4];
		// Flag to determine what type of resizing should be done
        int resizeFlag = 1;                                                                         
        double scalePercentH, scalePercentW;

		// Get the initial dimensions of the parent and child image
        parentDims[0] = parentImg.getHeight();                                                      
        parentDims[1] = parentImg.getWidth();
        childDims[0] = childImg.getHeight();                                                        
        childDims[1] = childImg.getWidth();

		// Perform a special size check of key images for non-proportional resize
        if (childWasKey) {                                                                          
            if ((childDims[0] < parentDims[0]) || (childDims[1] < parentDims[1])) {
                System.out.println("They key image was too small for the cover and will be resized to exact dimensions");
                resizeFlag = 5;
            }
        } else if ((childDims[0] > parentDims[0]) || (childDims[1] > parentDims[1])) {              
            System.out.println("The secret image is larger than cover image and will be resized proportionally");
            System.out.println("Some loss of quality may occur.");

			// If either dimension of the child is larger than the matching parent dimension, get the proportional scaling ratio of the width
			// and height of the child in relation to the parent. Get dimensions for the height wise and width wise ratio
            scalePercentH = (float) parentDims[0] / childDims[1];                                   
            scalePercentW = (float) parentDims[1] / childDims[1];                                   
            compareDims[0] = (int) Math.floor(scalePercentH * childDims[0]);                        
            compareDims[1] = (int) Math.floor(scalePercentH * childDims[1]);
            compareDims[2] = (int) Math.floor(scalePercentW * childDims[0]);                        
            compareDims[3] = (int) Math.floor(scalePercentW * childDims[1]);

			// If the height percentage ratio is larger than the width percentage ratio, the height of the scaled child is smaller or equal
			// to the parent size, AND if the width of the scaled child is also smaller or equal set the scaling to use height percentage.
			// Otherwise use width based percentage
            if (((compareDims[0] >= compareDims[2]) || compareDims[2] > parentDims[0])              
                    && (compareDims[0] <= parentDims[0]) && (compareDims[1] <= parentDims[1])) {    
                resizeFlag = -1;                                                                    
            } else
                resizeFlag = 0;                                                                     
        }

		// Resize by height = -1 
		// Resize by width = 0
		// Non-proportional scaling (for key images) = 5 
        if (resizeFlag == -1) {                                                                                               
            updatedImage = new BufferedImage(compareDims[1], compareDims[0], BufferedImage.TYPE_INT_RGB);                     
            resizer = updatedImage.createGraphics();
            resizer.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            resizer.drawImage(childImg, 0, 0, compareDims[1], compareDims[0], null);
            resizer.dispose();
        } else if (resizeFlag == 0) {                                                                                          
            updatedImage = new BufferedImage(compareDims[3], compareDims[2], BufferedImage.TYPE_INT_RGB);                      
            resizer = updatedImage.createGraphics();
            resizer.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            resizer.drawImage(childImg, 0, 0, compareDims[3], compareDims[2], null);
            resizer.dispose();
        } else if (resizeFlag == 5) {
            updatedImage = new BufferedImage(parentDims[1], parentDims[0], BufferedImage.TYPE_INT_RGB);                         
            resizer = updatedImage.createGraphics();                                                                            
            resizer.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            resizer.drawImage(childImg, 0, 0, parentDims[1], parentDims[0], null);
            resizer.dispose();
        }else
            updatedImage = childImg;                                                                                            

        return updatedImage;
    }//END checkImageSize

    // Performs the scrambling procedure to apply a key image to a secret image. 
	// Auto converts to gray scale in order to allow sufficient manipulation of the image to be useful in hiding the secret
    public static ImageObject applyKey(BufferedImage keyToUse, BufferedImage secretInput, BufferedImage coverToUse) {
        int[] tempSecretChannel;
        int[] tempKeyChannel;
        int[] coverDims = new int[2];
        int[] secretDims = new int[2];
        int bitsToShift;
        int currChannelPix, currSecretPix;

		// Get the dimension of both input images
        coverDims[0] = coverToUse.getHeight();                                                                          
        coverDims[1] = coverToUse.getWidth();
        secretDims[0] = secretInput.getHeight();
        secretDims[1] = secretInput.getWidth();
		
		// Create a blank image the size of the cover
        ImageObject lockedImage = new ImageObject(coverDims[1], coverDims[0], false);
		// Secret images if using a key cannot be color at this time
        BufferedImageOp convertor = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		// Always convert before processing to avoid color channel issues
        BufferedImage temp = convertor.filter(secretInput, null); 
		ImageObject initial = bakeImage(temp,false);                                                          
        
		// For every color channel copy the bit planes from the initialized secret image object into the resized locked image object
		for (int i = 1; i < 4; i++) {                                                                                   
            for (int j = 0; j < 8; j++) {                                                                               
                tempSecretChannel = initial.getSinglePlane(i, j);                                                       
                lockedImage.setSinglePlane(tempSecretChannel, i, j, secretDims);                                        
            }
        }
		// Recombine the bit planes into channels
        lockedImage.extSetChannels(lockedImage.combBitPlanes());                                                        


		// If the original key image is a recognized file type, convert to the standard grayscale format to ensure consistency
		// Do not convert unrecognized file types
        ImageObject key;
        if(keyToUse.getType() != BufferedImage.TYPE_CUSTOM) {                                 
            temp = convertor.filter(keyToUse, null);                                    
            key = bakeImage(temp, false);                                           
        }else
            key = bakeImage(keyToUse,false);                                        

		// Get the red color channel of the key and secret image (could really be any channel since all are equal in grayscale)
		// For every pixel in the grayscale channel, if the pixel value was pure white (255) reduce the color value by one to avoid 
		// issues with recovery (unable to differentiate pure black (0) and pure white in some recovery scenarios)		
        tempSecretChannel = lockedImage.getSingleChannel(1);                     
        tempKeyChannel = key.getSingleChannel(1);                                
        for (int j = 0; j < tempSecretChannel.length; j++) {                                  
            currChannelPix = tempSecretChannel[j];
            if (currChannelPix == 255)                                                        
                currChannelPix = 254;                                                         
            currSecretPix = tempKeyChannel[j];
			
			// Add the two pixels together in a circular manner, range: 0 - 255. Get a value for shifting bits using a prime number 
			// and the key image pixel. Assign the manipulated pixel to the key "locked" image
            currChannelPix = (currChannelPix + currSecretPix) % 255;                          
            bitsToShift = currSecretPix % 7;                                                  
            tempSecretChannel[j] = rotateLeft(currChannelPix, bitsToShift);                   
        }
		// Update the bit plane marker in the locked image and set the flag to indicate a key image was used (not fully utilized at this time)
        lockedImage.setBitPlanes();                                                           
        lockedImage.markAsLocked();                                                           
        return lockedImage;
    }//END applyKey

    // Applies a rotational bit shift to the right for 8 bit integers. Maintains the sign of the integer
    public static int rotateRight(int bits, int shift) {
        int temp = (((bits & 0xff)  >>> shift) | ((bits & 0xff) << (8 - shift)));
        return temp & 0xff;
    }

    // Applies a rotational bit shit to the left for 8 bit integers. Maintains the sign of the integer
    public static int rotateLeft(int bits, int shift) {
        int temp = (((bits & 0xff) << shift) | ((bits & 0xff) >>> (8 - shift)));
        return temp & 0xff;
    }
}
