/*
 * Created by: Jeremy Ruth - 04.25.2018
 *
 * This is the JavaFX contoller. It is used by JavaFX to interact with the UI (MVC architecture). 
 * UI is built with the FXML file "UserInterface." 
 * The error messages included in the code below are not yet UI facing. A future version will implement
 * pop-ups or another method to help guide the user when an unavailable choice is made. Currently the program
 * should catch most incorrect user actions, but doesn't notify the user.
 */

package StegoDesktop;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;

public class Controller {

    private File coverToUse, secretToUse, keyToUse, stegoInToUse, stegoFileOut, recoveredFileOut;
    private BufferedImage coverImage, secretImage, keyImage, stegoImageIn, stegoImageOut, recoveredImage;
    private ImageObject coverObject, secretObject, keyObject, recoveredObject, stegoObject;
    private int numOfPlanesForSecret=0, singleBitPlaneToView = 10;
    boolean usingKey=false, coverWasColor = true, secretWasColor = true, haveFile=false;
    boolean stegoWasMade = false, recoveredWasMade = false, userCancelled = false;
    char advWhichImageToView = 'x', advColorChanToView = 'x', processImageWas = 'x';


    @FXML TextField coverImgField;
    @FXML TextField secretImgField;
    @FXML TextField keyImgField;
    @FXML TextField stegoImgField;

    @FXML Button recoverPreviewBttn;
    @FXML Button recoverSaveImgBttn;
    @FXML Button stegoPreviewBttn;
    @FXML Button stegoSaveBttn;
    @FXML Button showAdvancedBttn;

    @FXML RadioButton recoverSecretBttn;
    @FXML RadioButton makeStegoBttn;
    @FXML RadioButton stegUse1PlaneBttn;
    @FXML RadioButton stegUse2PlaneBttn;
    @FXML RadioButton stegUse3PlaneBttn;
    @FXML RadioButton stegUse4PlaneBttn;
    @FXML RadioButton advRedChanBttn;
    @FXML RadioButton advGreenChanBttn;
    @FXML RadioButton advBlueChanBttn;
    @FXML RadioButton advWholeImgBttn;
    @FXML RadioButton advChooseCoverBttn;
    @FXML RadioButton advChooseSecretBttn;
    @FXML RadioButton advChooseKeyBttn;
    @FXML RadioButton advChooseCurrentBttn;
    @FXML RadioButton advWholeChannelBttn;
    @FXML RadioButton advViewBit1Bttn;
    @FXML RadioButton advViewBit2Bttn;
    @FXML RadioButton advViewBit3Bttn;
    @FXML RadioButton advViewBit4Bttn;
    @FXML RadioButton advViewBit5Bttn;
    @FXML RadioButton advViewBit6Bttn;
    @FXML RadioButton advViewBit7Bttn;
    @FXML RadioButton advViewBit8Bttn;

    @FXML ToggleButton enableKeyImgToggle;
    @FXML ToggleButton coverBWToggle;
    @FXML ToggleButton secretBWToggle;
    @FXML ToggleGroup advPlaneGrp;

    @FXML ImageView imageViewer;

    public void startUpRoutine() {
        recoverPreviewBttn.setDisable(true);
        recoverSaveImgBttn.setDisable(true);
        stegoPreviewBttn.setDisable(true);
        stegoSaveBttn.setDisable(true);
        showAdvancedBttn.setDisable(true);
        setStatSingleBits(true);
    }

    @FXML
    public void getCoverAction(ActionEvent getCoverImg) {
        coverToUse = getAFile();
        if(coverToUse != null) {
            coverToUse.getAbsolutePath();
            coverImgField.setText(coverToUse.getName());

            coverImgField.setDisable(true);
            coverImage = convertImg(coverToUse);
            checkForAdvanced();
            stegoWasMade = false;
            System.out.println("The cover has been retrieved and the file path was:" + coverToUse.getName());
        }
    }

    @FXML
    public void getStegoAction(ActionEvent getStegoImg) {
        stegoInToUse = getAFile();
        if(stegoInToUse != null) {
            stegoInToUse.getAbsolutePath();
            stegoImgField.setText(stegoInToUse.getName());

            stegoImgField.setDisable(true);
            stegoImageIn = convertImg(stegoInToUse);
            checkForAdvanced();
            recoveredWasMade = false;
            System.out.println("The cover has been retrieved and the file path was:" + stegoInToUse.getName());
        }
    }

    @FXML
    public void getSecretAction(ActionEvent getSecretImg) {
        secretToUse = getAFile();
        if(secretToUse != null) {
            secretToUse.getAbsolutePath();
            secretImgField.setText(secretToUse.getName());

            secretImgField.setDisable(true);
            secretImage = convertImg(secretToUse);
            checkForAdvanced();
            stegoWasMade = false;
            System.out.println("The secret has been retrieved and the file path was:" + secretToUse.getName());
        }
    }

    @FXML
    public void getKeyAction(ActionEvent getSecretImg) {
        keyToUse = getAFile();
        if(keyToUse != null) {
            keyToUse.getAbsolutePath();
            keyImgField.setText(keyToUse.getName());

            keyImgField.setDisable(true);
            keyImage = convertImg(keyToUse);
            checkForAdvanced();
            stegoWasMade = false;
            recoveredWasMade = false;
            System.out.println("The key has been retrieved and the file path was:" + keyToUse.getName());
        }
    }

    @FXML
    public void updateUsingKeyAction(ActionEvent usingKeyToggle) {
        if(enableKeyImgToggle.isSelected())
            usingKey = true;
        else
            usingKey = false;
        recoveredWasMade = false;
        stegoWasMade = false;
        System.out.println("Will the key image be used?:" + usingKey);
    }

    @FXML
    public void recoverSecretRadioAction(ActionEvent recoverRadio) {
        recoverPreviewBttn.setDisable(false);
        recoverSaveImgBttn.setDisable(false);
        stegoPreviewBttn.setDisable(true);
        stegoSaveBttn.setDisable(true);
    }

    @FXML
    public void previewRecoveredAction(ActionEvent previewRecoveredBttn) {
        if(stegoImageIn != null) {
            processImageWas = 'r';
            if (!recoveredWasMade)
                processRecoverSecret();
            doAdvView(recoveredImage);
            recoveredWasMade = true;
            System.out.println("The recovered image will be previewed");
        }
    }

    @FXML
    public void saveRecoveredAction(ActionEvent saveRecoveredBttn) {
        if(stegoImageIn != null) {
            processImageWas = 'r';
            if (!recoveredWasMade)
                processRecoverSecret();
            saveAFile();
            recoveredWasMade = true;
            System.out.println("Will save the recovered secret image");
        }
    }

    @FXML
    public void makeStegoRadioAction(ActionEvent stegoRadio) {
        recoverPreviewBttn.setDisable(true);
        recoverSaveImgBttn.setDisable(true);
        stegoPreviewBttn.setDisable(false);
        stegoSaveBttn.setDisable(false);
    }

    @FXML
    public void updateCoverColorAction(ActionEvent coverColorToggle) {
        if (coverBWToggle.isSelected())
            coverWasColor = false;
        else
            coverWasColor = true;
        stegoWasMade = false;
        System.out.println("The cover was color?:" + coverWasColor);
    }

    @FXML
    public void updateSecretColorAction(ActionEvent secretColorToggle) {
        if (secretBWToggle.isSelected())
            secretWasColor = false;
        else
            secretWasColor = true;
        stegoWasMade = false;
        System.out.println("The secret was color?:" + secretWasColor);
    }

    @FXML
    public void stegoPlanesForSecretAction(ActionEvent stegoPlaneRadio) {
        if(stegUse1PlaneBttn.isSelected())
            numOfPlanesForSecret = 1;
        else if(stegUse2PlaneBttn.isSelected())
            numOfPlanesForSecret = 2;
        else if(stegUse3PlaneBttn.isSelected())
            numOfPlanesForSecret = 3;
        else if(stegUse4PlaneBttn.isSelected())
            numOfPlanesForSecret = 4;
        else
            numOfPlanesForSecret = 0;
        stegoWasMade = false;
        System.out.println("The number of planes that will be used for the secret is: " + numOfPlanesForSecret);
    }

    @FXML
    public void previewStegoAction(ActionEvent previewStegoBttn) {
        if(coverImage != null && secretImage != null) {
            processImageWas = 's';
            if (numOfPlanesForSecret != 0 || usingKey) {
                if (!stegoWasMade)
                    processMakeStego();
                doAdvView(stegoImageOut);
                stegoWasMade = true;
                System.out.println("The stego image will be previewed");
            } else
                System.out.println("No bit planes were chosen. You must choose at least one bit plane unless using a key image");
        }
    }

    @FXML void saveStegoAction(ActionEvent saveStegoBttn) {
        if(coverImage != null && secretImage != null) {
            processImageWas = 's';
            if (numOfPlanesForSecret != 0 || usingKey) {
                if (!stegoWasMade)
                    processMakeStego();
                saveAFile();
                stegoWasMade = true;
                System.out.println("The stego image will be saved");
            } else
                System.out.println("No bit planes were chosen. You must choose at least one bit plane unless using a key image");
        }
    }

    @FXML
    public void advChooseImageAction(ActionEvent advChooseImgRadio) {
        if(advChooseCoverBttn.isSelected())
            advWhichImageToView = 'c';
        else if(advChooseSecretBttn.isSelected())
            advWhichImageToView = 's';
        else if(advChooseKeyBttn.isSelected())
            advWhichImageToView = 'k';
        else if(advChooseCurrentBttn.isSelected())
            advWhichImageToView = 'p';
        else
            advWhichImageToView = 'x';
        checkForAdvanced();
        System.out.println("The image that advanced preview will be used on is: " + advWhichImageToView);
    }

    @FXML
    public void advChooseChannelAction(ActionEvent advChooseChanRadio) {
        if(advRedChanBttn.isSelected())
            advColorChanToView = 'r';
        else if(advGreenChanBttn.isSelected())
            advColorChanToView = 'g';
        else if(advBlueChanBttn.isSelected())
            advColorChanToView = 'b';
        else if(advWholeImgBttn.isSelected())
            advColorChanToView = 'a';
        else
            advColorChanToView = 'x';
        checkForAdvanced();

        if(advColorChanToView == 'a' || advColorChanToView == 'x')
            setStatSingleBits(true);
        else
            setStatSingleBits(false);
        System.out.println("The channel that will be used for advanced viewing is: " + advColorChanToView);
    }

    @FXML
    public void advBitPlaneToViewAction(ActionEvent advBitRadio) {
        if(advWholeChannelBttn.isSelected())
            singleBitPlaneToView = -1;
        else if(advViewBit1Bttn.isSelected())
            singleBitPlaneToView = 0;
        else if(advViewBit2Bttn.isSelected())
            singleBitPlaneToView = 1;
        else if(advViewBit3Bttn.isSelected())
            singleBitPlaneToView = 2;
        else if(advViewBit4Bttn.isSelected())
            singleBitPlaneToView = 3;
        else if(advViewBit5Bttn.isSelected())
            singleBitPlaneToView = 4;
        else if(advViewBit6Bttn.isSelected())
            singleBitPlaneToView = 5;
        else if(advViewBit7Bttn.isSelected())
            singleBitPlaneToView = 6;
        else if(advViewBit8Bttn.isSelected())
            singleBitPlaneToView = 7;
        else
            singleBitPlaneToView = 10;
        checkForAdvanced();
        System.out.println("The bit plane to view in the advanced options is: " + singleBitPlaneToView);
    }

    @FXML
    public void advShowSelectionAction(ActionEvent showSelectionBttn) {
        if(advWhichImageToView == 'c' && advColorChanToView == 'a' && coverToUse != null)
            doAdvView(coverImage);
        else if (advWhichImageToView == 's' && advColorChanToView == 'a' && secretToUse != null)
            doAdvView(secretImage);
        else if(advWhichImageToView == 'k' && advColorChanToView == 'a' && keyToUse != null)
            doAdvView(keyImage);
        else if(advWhichImageToView == 'p' && advColorChanToView == 'a' && processImageWas == 's' && stegoImageOut != null)
            doAdvView(stegoImageOut);
        else if(advWhichImageToView == 'p' && advColorChanToView == 'a' && processImageWas == 'r' && recoveredImage != null)
            doAdvView(recoveredImage);
        else if(advWhichImageToView == 'c' && advColorChanToView != 'a' && coverToUse != null && singleBitPlaneToView == -1)
            doAdvChanView(coverObject, coverImage, advColorChanToView);
        else if (advWhichImageToView == 's' && advColorChanToView != 'a' && secretToUse != null && singleBitPlaneToView == -1)
            doAdvChanView(secretObject, secretImage, advColorChanToView);
        else if(advWhichImageToView == 'k' && advColorChanToView != 'a' && keyToUse != null && singleBitPlaneToView == -1)
            doAdvChanView(keyObject, keyImage, advColorChanToView);
        else if(advWhichImageToView == 'p' && advColorChanToView != 'a' && processImageWas == 's' && stegoImageOut != null && singleBitPlaneToView == -1)
            doAdvChanView(stegoObject, stegoImageOut, advColorChanToView);
        else if(advWhichImageToView == 'p' && advColorChanToView != 'a' && processImageWas == 'r' && recoveredImage != null && singleBitPlaneToView == -1)
            doAdvChanView(recoveredObject, recoveredImage, advColorChanToView);
        else if(advWhichImageToView == 'c' && advColorChanToView != 'a' && coverToUse != null && singleBitPlaneToView != -1)
            doAdvPlaneView(coverObject, coverImage, advColorChanToView, singleBitPlaneToView);
        else if (advWhichImageToView == 's' && advColorChanToView != 'a' && secretToUse != null && singleBitPlaneToView != -1)
            doAdvPlaneView(secretObject, secretImage, advColorChanToView, singleBitPlaneToView);
        else if(advWhichImageToView == 'k' && advColorChanToView != 'a' && keyToUse != null && singleBitPlaneToView != -1)
            doAdvPlaneView(keyObject, keyImage, advColorChanToView, singleBitPlaneToView);
        else if(advWhichImageToView == 'p' && advColorChanToView != 'a' && processImageWas == 's' && stegoImageOut != null && singleBitPlaneToView != -1)
            doAdvPlaneView(stegoObject, stegoImageOut, advColorChanToView, singleBitPlaneToView);
        else if(advWhichImageToView == 'p' && advColorChanToView != 'a' && processImageWas == 'r' && recoveredImage != null && singleBitPlaneToView != -1)
            doAdvPlaneView(recoveredObject, recoveredImage, advColorChanToView, singleBitPlaneToView);
        else
            System.out.println("There was an error trying to retrieve the selected view");

        System.out.println("Will show the advanced selection view");
    }

    public void checkForAdvanced() {
        if(coverToUse != null || secretToUse != null || keyToUse != null)
            haveFile = true;
        else
            haveFile = false;

        if(advWhichImageToView != 'x' && advColorChanToView != 'x' && haveFile)
            showAdvancedBttn.setDisable(false);
        else
            showAdvancedBttn.setDisable(true);
    }

    public File getAFile() {
        File currFile = null;
        String tempPath = System.getProperty("user.home");
        JFileChooser findImage = new JFileChooser(tempPath+"/Desktop");
        findImage.setDialogTitle("Choose an image to load");
        JFrame fileFrame = fileChooserFrame();
        int didChoose = findImage.showOpenDialog(fileFrame);
        if(didChoose == findImage.APPROVE_OPTION)
            currFile = findImage.getSelectedFile();

        if(didChoose == findImage.CANCEL_OPTION)
            userCancelled = true;

        fileFrame.dispatchEvent(new WindowEvent(fileFrame, WindowEvent.WINDOW_CLOSING));
        return currFile;
    }

    public BufferedImage convertImg(File imageToConvert) {
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(imageToConvert);
            if(tempImage == null) {
                System.out.println("The does not appear to be an image format or is a format that cannot be read. Please choose another file.");
            }
        }catch(IOException fileErr) {
            System.out.println("There was an error reading the selected file. Please try again");
            fileErr.printStackTrace();
        }
        return tempImage;
    }

    public void saveAFile() {
        String tempPath = System.getProperty("user.home");
        JFileChooser saveImage = new JFileChooser(tempPath+"/Desktop");
        saveImage.setDialogTitle("Specify the PNG file name and location:");
        JFrame saveFrame = fileChooserFrame();
        int userChoice = saveImage.showSaveDialog(saveFrame);

        if(userChoice == JFileChooser.APPROVE_OPTION) {
            File fileNameToUse = saveImage.getSelectedFile();
            String checkName = fileNameToUse.getAbsolutePath();
            if(!checkName.toLowerCase().endsWith(".png")) {
                fileNameToUse = new File(checkName + ".png");
            }
            System.out.println("The filepath that will be used is: " + fileNameToUse.getName());
            try {

                if(processImageWas == 's') {
                    stegoFileOut = fileNameToUse;
                    ImageIO.write(stegoImageOut, "PNG", stegoFileOut);
                }else if(processImageWas == 'r') {
                    recoveredFileOut = fileNameToUse;
                    ImageIO.write(recoveredImage, "PNG", recoveredFileOut);
                }
            }catch(IOException writeErr) {
                System.out.println("There was an error when attempting to write the file");
                writeErr.printStackTrace();
            }
        }
        saveFrame.dispatchEvent(new WindowEvent(saveFrame, WindowEvent.WINDOW_CLOSING));
    }

    public JFrame fileChooserFrame () {
        JFrame temp = new JFrame();
        temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        temp.pack();
        temp.setLocationRelativeTo(null);
        temp.setVisible(false);

        return temp;
    }

    public void setStatSingleBits (boolean currStat) {
       advWholeChannelBttn.setDisable(currStat);
       advViewBit1Bttn.setDisable(currStat);
       advViewBit2Bttn.setDisable(currStat);
       advViewBit3Bttn.setDisable(currStat);
       advViewBit4Bttn.setDisable(currStat);
       advViewBit5Bttn.setDisable(currStat);
       advViewBit6Bttn.setDisable(currStat);
       advViewBit7Bttn.setDisable(currStat);
       advViewBit8Bttn.setDisable(currStat);
    }

    public void doAdvPlaneView(ImageObject objToView, BufferedImage imgToView, char channelToView, int bitPlaneToView) {
        BufferedImage tempChannel;
        ImageObject tempObjectForChannel;
        if(objToView == null) {
            tempObjectForChannel = ImageProcs.bakeImage(imgToView, true);
            tempChannel = ImageProcs.showSinglePlane(tempObjectForChannel, channelToView, bitPlaneToView);
        }else
            tempChannel = ImageProcs.showSinglePlane(objToView, channelToView, bitPlaneToView);
        doAdvView(tempChannel);
    }

    public void doAdvChanView(ImageObject objToView, BufferedImage imgToView, char channelToView) {
        BufferedImage tempChannel;
        ImageObject tempObjectForChannel;
        if(objToView == null) {
            tempObjectForChannel = ImageProcs.bakeImage(imgToView, true);
            tempChannel = tempObjectForChannel.getChannel(channelToView);
        }else
            tempChannel = objToView.getChannel(channelToView);
        doAdvView(tempChannel);
    }

    public void doAdvView(BufferedImage imgToView) {
        Image currImage = SwingFXUtils.toFXImage(imgToView,null);
        imageViewer.setImage(currImage);
    }

    public void processMakeStego() {
        if(coverImage != null)
            coverObject = ImageProcs.bakeImage(coverImage,coverWasColor);

        if(usingKey && keyImage != null && secretImage != null) {
            keyImage = ImageProcs.checkImageSize(coverImage,keyImage, true);
            secretObject = ImageProcs.applyKey(keyImage,secretImage, coverImage);
        }else
            if(secretImage != null)
                secretObject = ImageProcs.bakeImage(secretImage,secretWasColor);

        if(coverObject != null && secretObject != null) {
            stegoObject = ImageProcs.embedSecret(coverObject, secretObject, numOfPlanesForSecret, usingKey);
            stegoImageOut = ImageProcs.imgObjToRGB(stegoObject, null, stegoObject.readPlaneFlags());
            stegoObject.setOrigImg(stegoImageOut);
        }
    }

    public void processRecoverSecret() {
        BufferedImage tempKey = keyImage;
        if(stegoImageIn != null)
            recoveredObject = ImageProcs.bakeImage(stegoImageIn, true);

        if(usingKey && keyImage != null)
            tempKey = ImageProcs.checkImageSize(stegoImageIn, tempKey,true);
        else tempKey = null;

        if(recoveredObject != null) {
            recoveredImage = ImageProcs.getSecret(recoveredObject, !usingKey, tempKey);
            recoveredObject = ImageProcs.bakeImage(recoveredImage, recoveredObject.readColorFlag());
        }
    }
}
