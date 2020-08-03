# Steganography_App Ver 1.0

Steganography is the practice of concealing secret information within data or information that is not secret. This method can offer some interesting alternatives to the more common modern day use of cryptography in that, rather than seeking to protect information that is intended to remaing secret, stegonography seeks to conceal the existence of sensitive information altogether. Enhancing stegonagraphy with cryptography offers an even more secure method of passing secret information. This application offers a means to apply stgenography to images using the common LSB (least significant bit) method. Additionally, it offers an optional layer of protection through the use of "key images" that can be applied to mask the underlying secret information even if its existence is suspected. A more detailed description about the process used can be found in the "docs" folder.

## Getting Started / Installation

This program is a self-contained JAR file. There is no installation necessary and the only requirement to run the application is to have Java installed ([JRE](https://www.java.com/en/download/)). See the prerequisites section below for some terminology that will make using the application easier.

## Prerequisites 

Below is list of definitions to help clarify the app usage. Some of these terms are commonly used in stegonography:

* **Cover Image** - The image that will be used to hide the secret image/information.
* **Secret Image** - The image/information that is concealed by the cover image.
* **Stego-Object** - The combined image that contains the cover image and the secret. This is the output file of the application, but is also an input when recovering a secret.
* **Key Image** - The image that will be used to "scramble" the secret, providing a simple cryptography layer for protection. The correct key image must also be loaded when recovering a secret that used a key image during creation.
* **Bit Plane** - In a digital image the bit plane is a sequence of bits representing the pixels in a bitmap image. This application can read and output 24bit images, meaning that there are 8 bit planes for each color channel (Red, Green, Blue). The higher the bit plane (1-8) the more information or "weight" it carries in the image, and the greater effect changing that plane's bits will have on the output image.

## Testing

To assist with testing and learning to use this program, I have included some images to work with which can be found in the "test" folder. These photos were taken by myself and so are safe to distribute and use freely for these purposes. There is a small assortment of image types including some text converted to image files that can be used to test text based secrets.

If desired, the source code for the JavaFX package includes a basic JFrame panel structure found in the file "DisplayPanel.Java." This panel can be used for testing and experimenting with new functionality, test drivers, etc. if you desire to add to the program. There are comments in the panel code on how to use it in general. While there are no included testing cases there a couple of other methods included in the source code such as "closePanelSet" which can be found in the "ImageProcs.java" file which may help when developing new functionality.

## Important Note

TLDR: If you are running out of memory try allocating more to the JAR by running it from a command line: java -Xmx1024m -jar StegoDesktopApp.jar 
where "1024" is the size of the memory you want to allocate to the runtime environment in multuples of 1024 (the "m" afterwards means MB). Google "-Xmx usage in java" for more details and options. 

The current version of this application may experience a crash due to running out of allocated RAM if you attempt to manipulate very large images or run too many image manipulations through the program in one sitting. This is at least partially because of the method used to break apart and recombine images. Every image that is loaded is essentially split into several matrices. Those for the bit planes, color channels, and the combined images themselves. If you make use of a Key image as well, this means that each creation of an image with a secret can have up to 112 2-D matrices including those of the output image. Considering even an average phone camera these days can easily take 12 megapixel pictures (about 36MB in file size), it's easy to see a lot of memory can be used up fast.

The means to accomplish the image manipulation is hand coded and is more of a brute force means as it made doing the manipulation that was needed simpler to accomplish. It also had the added tinkerers benefit of having the various bit planes and color channels viewable for every image in use (which can be very interesting to look through in itself). I suspect my approach isn't ideal from a garbage collection point of view either, and the Java garbage collector is failing to clean up resources that are no longer needed in a timely manner. Efficient image processing and manipulation can be quite complex, and so a big goal for future versions is to move away from the hand made methods found in the current version to making use of an open source option such as [OpenCV](https://opencv-java-tutorials.readthedocs.io/en/latest/#). This should offer the advantage of not only being more efficient for image manipulation, but also offers much more robust image type support and could lead to the possibility of a mobile version of the app down the road.

## Version History

Version 1.0 - Initial release

## Author

**Jeremy Ruth**

## License

This project is licensed under the Creative Commons Zero v1.0 Universal license. See [License.md](https://github.com/Jeremy-Ruth/Stegonography_App/blob/master/LICENSE) for more details.
