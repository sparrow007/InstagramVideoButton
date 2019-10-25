# InstagramVideoButton
This library is inspired by the instagram video button having the same animation, look and feel.

<p align="center">
<img  src="https://user-images.githubusercontent.com/49305594/67561061-cada2000-f739-11e9-9beb-42700cd224d6.jpg" 
width = 700 height = 300/>
</p>

[![Platform](https://img.shields.io/badge/platform-android-blue.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-13%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=13)


USAGE
-----
To add instagramVideoButton in your project, just add the below code in your dependencies section inside the app module gradle file:

Gradle
------
```
dependencies {
    ...
    implementation 'com.jackandphantom.android:instagramvideobutton:1.0.0'
}
```
NOTE
-----
In order to enable video and photo animation you have to add enableVideoRecording and enablePhotoTaking true either in kotlin or in xml as 
an attribute.

XML
-----

```xml
<!-- <a> instagram video button xml</a> -->
    <com.jackandphantom.instagramvideobutton.InstagramVideoButton
            android:layout_width="130dp"
            android:layout_height="130dp"
            android:id="@+id/component"
            app:enableVideoRecording="true"
            android:layout_marginBottom="30dp"
            app:enablePhotoTaking="true"
            app:progressColor="#FF5722"
            android:layout_centerHorizontal="true"
            android:layout_alignParentBottom="true"/>
```

### xml attributes

Xml attribute | Description
---|---
  app:innerCircleColor | represents the inner circle inside the button 
  app:outerCircleColor | represents the outer circle in the button
  app:progressColor | represents a color of circular progress bar 
  app:outerCircleWidth | outer circle width
  app:enableVideoRecording | for enable the video on long press
  app:enablePhotoTaking | for enabling the photo on single tap 
  
   Kotlin
-----
  ```xml
  val instagramVideoButton = findViewById<InstagramVideoButton>(R.id.instagram_video_button)
  instagramVideoButton.enablePhotoTaking(true)
  instagramVideoButton.enableVideoRecording(true)
  instagramVideoButton.setVideoDuration(10000)
 ```
   
Observe !!
   
 ```kotlin
 instagramVideoButton.actionListener =  object : InstagramVideoButton.ActionListener {
            override fun onStartRecord() {
                Log.e("MY TAG", "CALL the on start record ")
             
            }
            override fun onEndRecord() {
                Log.e("MY TAG", "CALL the on end record ")
            }

            override fun onSingleTap() {
                Log.e("MY TAG", "CALL the on single tap record ")
            }

            override fun onDurationTooShortError() {
                Log.e("MY TAG", "CALL the on on duration record ")

            }

            override fun onCancelled() {
                Log.e("MY TAG", "CALL the on on cancel record ")
            }

        
       }
       
   ```
   
    
  ### Public Methods
Method | Description
---|---
 void fun enableVideoRecording(boolean enable) | In order to perform video animation you have to make it true.
 void fun enablePhotoTaking(boolean enable) | In order to perform  single tap photo click animation you have to make it true. 
 void fun setVideoDuration(long duration) | set the video duration.
 void fun setMinimumVideoDuration(long duration) | set the minimum video duration.
 void fun cancelRecording() | Cancel the video recording.
 void fun setInnerCircleColor(int color) | set the color of inner circle.
 void fun setOuterCircleColor(int color) | set the color of outer circle.
 void fun setOuterCircleWidth(int color) | set the width of outer circle.
 void fun setProgressColor(int color) | set the color of circular progressbar.
 
 
  ### Contribution
 If you want to add feature and find a bug feel free to contribute , you can  create issue related to bug , feature and send a pull.
 
 LICENCE
-----

 Copyright 2019 Ankit kumar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
  Thanks to iammert.
