<p align="center" >
  <a href="http://www.helpstack.io/"><img src="https://dl.dropboxusercontent.com/u/55774910/HelpStack/Helpstack%20by%20Happyfox%20logos.png" alt="HelpStack" title="Logo" /></a>
</p>

**HelpStack** is a library to provide in-app support for your app's users, by connecting with the helpdesk of your choice. Your app’s users can easily raise requests or report issues from within your app.

With HelpStack, you can:

- Let users report issues within your app, along with device & app information
- Provide self-support by showing FAQ articles
- Customise your HelpStack screen to blend with your app theme

HelpStack supports the following leading helpdesk solutions: 
- [HappyFox](https://www.happyfox.com/)
- [Zendesk](https://www.zendesk.com/)
- [Desk.com](http://www.desk.com/)
- Email - If you don't have a helpdesk solution, you can still configure HelpStack for users to raise requests via email.

You can choose any 1 of the 4 options mentioned above, to get started with HelpStack.

Check out [helpstack.io](http://www.helpstack.io) for more information.

<p align="left" >
  <img src="Images/hs_preview.png" alt="HelpStackthemes" title="screenshots">
</p>

## App Showcase
Have you made something awesome with HelpStack? Add yourself here: [App Showcase](https://github.com/happyfoxinc/helpstack-android/wiki/App-Showcase)

## Installation

Installing the HelpStack library is fairly straight-forward. 

1. Add jcenter as a repository to your app's build.gradle

        repositories {
          jcenter()
        }
        
2. Add *com.tenmiles:helpstack:1.1.2* as a dependency in the same build.gradle
        
        dependencies {
          compile 'com.tenmiles:helpstack:1.1.2'
        }

        
For more information on installation, such as Installing in Eclipse and common installation errors, visit this Wiki page: [Installation Instructions](https://github.com/happyfoxinc/helpstack-android/wiki/Installation-Instructions)

## Using the Library

Broadly speaking, there are just 3 steps to begin using HelpStack within your app:

The helpdesk solutions will be referred to as **Gears**.

1. Configure HelpStack to work with the Gear of your choice.
2. Add an entry-point for HelpStack in the UI and connect it with HelpStack.
3. Customize the theme of HelpStack according to your choice.


#### Step 1 - Configure HelpStack with the Gear of your choice:

Create a custom *Application* class which extends the **Application** class. Be sure to mention it in your manifest as well.

    <application
      android:name="HSApplication"
      ...
    />    
    
Obtain the necessary credentials for the gear of your choice and create a suitable Gear object. Set the Gear object with *HSHelpStack* instance only once. 

##### Setting the <Gear>

    public class HSApplication extends Application {
      HSHelpStack helpStack;
      
      @Override
      public void onCreate() {
        super.onCreate();
        
        helpStack = HSHelpStack.getInstance(this); // Get the HSHelpStack instance
        
        <GearType> <gear> = new <GearType>(<Gear credentials as parameters>); // Create the Gear object 
          
        helpStack.setGear(<gear>); // Set the Gear
      }
    }
    
  Follow the instructions below for configuring the gear of your choice:
    
  - [HappyFox gear](https://github.com/happyfoxinc/helpstack-android/wiki/Configuring-gears-for-HelpStack#i-happyfox)
  - [Zendesk gear](https://github.com/happyfoxinc/helpstack-android/wiki/Configuring-gears-for-HelpStack#ii-zendesk)
  - [Desk gear](https://github.com/happyfoxinc/helpstack-android/wiki/Configuring-gears-for-HelpStack#iii-desk)
      
  - [Email gear](https://github.com/happyfoxinc/helpstack-android/wiki/Configuring-gears-for-HelpStack#iv-email)
                

#### Step 2 - Entry point in UI:
Add a clickable item (probably a button) in your UI, wherever appropriate. Set a *click listener* to it. Within the *click listener*, use the **showHelp** API to open up the HelpStack UI:

    HSHelpStack.getInstance(getActivity()).showHelp(getActivity());


#### Step 3 - Theming/Skinning:

It is very easy to customize the HelpStack UI, if you want it go along with your app's UI.

HelpStack comes with built-in screens and a default theme. It also comes with a set of pre-configured themes. You can download them from the link below:

#### [Download Themes](./Themes/)

Each theme comes with the following:
- A *colors.xml* and a **hs_custom_theme.xml** defined in **../values/**
- Chat bubble drawables defined in **../drawables/**.


##### Using the sample themes
- Decide which sample theme you want to use
- Include the *theme* and *colors* xml files in your application under **values**
- Include the theme's drawables under your application's **drawables**
- Now you can simply build and run the application. The HelpStack UI will use the styles specified in the chosen theme.

List of parameters for configuring the look and feel of HelpStack: [Parameters for configuring Themes](https://github.com/happyfoxinc/helpstack-android/wiki/Parameters-for-configuring-themes)

**Main List View** 
<img src="https://raw.githubusercontent.com/happyfoxinc/helpstack-android/master/Images/mainlist_style.png" alt="HelpStackthemes" title="Main List View screenshot">

**Issue Details View**
<img src="https://raw.githubusercontent.com/happyfoxinc/helpstack-android/master/Images/issuedetail_style.png" alt="HelpStackthemes" title="Issue Detail screenshot">

##### Note

  If you want the complete look-and-feel of a theme, you might also want to update the color of your app's Action bar.

For more information, refer to the documents in the [Wiki section](https://github.com/happyfoxinc/helpstack-android/wiki).

## External Dependencies

HelpStack depends on the following libraries:

    com.android.support:appcompat-v7:20.0.0
    com.google.code.gson:gson:2.2.4
    org.apache.httpcomponents:httpmime:4.2.6
    com.mcxiaoke.volley:library-aar:1.+
  
  
## Video

[![HelpStack for Android](http://img.youtube.com/vi/bmI3dXFMUuI/0.jpg)](http://www.youtube.com/watch?v=bmI3dXFMUuI)

## About
For more information about HelpStack, visit [helpstack.io](http://www.helpstack.io).

HelpStack is maintained by the folks at [HappyFox](http://www.happyfox.com/). Being an open source project, it also contains work from the HelpStack community.

<div align="center">
  <a href="http://www.happyfox.com" target="_blank"><img src="http://www.helpstack.io/startup/common-files/img/logos/happyfox.png" alt="HappyFox" width="160" ></a>
</div>

## Contact

Reach out to us on Twitter at [@HelpStack](https://twitter.com/HelpStackSDK).

## License

HelpStack is available under the MIT license. See the LICENSE file for more info.
