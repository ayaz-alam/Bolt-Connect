# Bolt-Connect
# Overview
It is always great to control a microcontroller from an android app .
So this project focuses on controlling BotIot (Microcontroller) from android app.
I have built an android app named Bot-Connect which can control Bolt-Iot in very easy and efficient way.
   

## Implementation 
We can connect our Bolt-Cloud using Bolt Cloud Api .
so what i have done i used volley to make HTTP Request and Response.
for every event of Bolt it generates a HTTP url and make a HTTP request ,
BoltCloud api respond to the request and give it back to the Android app.
so in this way we can do anything with bolt with this Mobile App.
## Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

### Setting up the Android Project

1. Download the *Bolt-connect* project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Install the NDK in Android Studio.

3. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select *Import Project (Eclipse ADT, Gradle, etc.)*
4. Navigate to the directory where you saved the Bolt-connect-android project, select the root folder of the project (the folder named "Bolt-connect-android"), and hit OK. Android Studio should now begin building the project with Gradle.

5. Once this process is complete and Android Studio opens, check the Console for any build errors.

    - *Note:* If you receive a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if available) that says *Install missing platform(s) and sync project* and allow Android studio to fetch you what is missing.





   
   
