# SmearDetectionJava


### What you’ll need
+ A favorite text editor or IDE (Intellij or Ecliplse IDE)
+ JDK 8 or later
+ Install Gradle


### Steps to Run the project 
+ Import the project in Intellij as a  [gradle project](https://www.jetbrains.com/help/idea/gradle.html#gradle_import_project_start). Gradle downloads all the necessary dependencies for us and this might take a while.
+ Open SmearDetection.java and update the smearDirectory in line 28(this is the path with all the sample images specific to a camera)
+ Run the SmearDetection.java file 

### Notes
+ The smearDirectory path should be specific to a particular camera images. In our case it was "C:/Users/Nikhil/Downloads/sample_drive/sample_drive/cam_4/"
+ The smearDirectory should be updated to test images from a different folder. In the above case the path was specific to cam_4.
+ Each folder would need about 15-20 minutes to detect smears and save output images to the smearDirectory
+ Gradle would need internet connection to download the required opencv dependencies

### Output from the code is copied into the smearDirectory folder with the below names
+ Intermediate.jpg
+ FinalImage.jpg
+ averageimage.jpg

### Team members
+ Venkata Akshith Reddy Kasireddy (A20455209)
+ Sai Vishal Kodimela (A20453006)
+ Nikhil Sarika (A20470289)
+ Souporno Ghosh (A20439047)

