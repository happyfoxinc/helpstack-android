<p align="center" >
  <a href="http://www.helpstack.io/"><img src="https://dl.dropboxusercontent.com/u/55774910/HelpStack/Helpstack%20by%20Happyfox%20logos.png" alt="HelpStack" title="Logo" /></a>
</p>


**HelpStack** is a library to provide in-app support for your app's users, by connecting with the helpdesk of your choice.

With HelpStack, you can:
- Show (local/remote) FAQ articles
- Let users report issues within your app

HelpStack supports the following helpdesk solutions: 
- HappyFox
- Zendesk
- Desk.com

If you don't have a helpdesk solution, you can also configure HelpStack, for users to raise requests via email.

Visit [helpstack.io](http://www.helpstack.io) to learn more.

<p align="left" >
  <img src="Images/hs_preview.png" alt="HelpStackthemes" title="screenshots">
</p>

## Installation

Installating the HelpStack library is fairly straight-forward.

### [Eclipse/ADT]:
1. Clone or download the library along with its dependencies from the Git repository. 
2. Import it as a library project into your Application. 
3. Set the flag for *manifestmerger.enabled* to *true* in your *project.properties* file:

        manifestmerger.enabled=true
    
### [Android Studio]:

1. Add jcenter as a repository to your app's build.gradle
2. Add com.tenmiles:helpstack:1.0 as a dependency

        repositories {
          jcenter()
        }
        
        dependencies {
          compile 'com.tenmiles:helpstack:1.0'
        }
    
You might face a few issues, depending on your app.

1. **Attribute is already present**: Follow the instructions suggested by Android Studio, which is along the lines of adding

        tools:replace="android:label"
        
  to your *application* tag.
2. **Duplicate files copied in APK**, listing some library jars: HelpStack uses some libraries and your app might be using the same. To avoid using the library from HelpStack, exclude it when you add the dependency. For example, if the library is *httpmime*, import the library in the manner below:


    compile ('com.tenmiles:helpstack:1.0') {
      exclude group: 'org.apache.httpcomponents', module: 'httpmime'
    }


## Using the Library

Broadly speaking, there are just 3 steps to begin using HelpStack within your app:

1. Choose a helpdesk solution of your choice and obtain the necessary credentials. These helpdesk solutions will be referred to as **Gears**.
2. Configure HelpStack to work with the Gear of your choice.
2. Add an entry-point for HelpStack in the UI and connect it with HelpStack.
3. Customize the theme of HelpStack according to your choice.


#### Step 1 - Choose a Gear and obtain credentials:

Obtain the necessary credentials for the gear of your choice and create a suitable Gear object.

##### i. HappyFox Gear credentials:
*HappyFox Account URL*, *API Key*, *Auth Code*, *Category ID* and *Priority ID*.

    HSHappyfoxGear happyfoxGear = new HSHappyfoxGear(
                "<Account URL>",
                "<Your API Key>",
                "<Your Auth Code>",
                "<Category ID>",
                "<Priority ID>");
                
The API key and Auth code can be found in your HappyFox account under *Manage* > *Integrations*. You can generate an API key and Auth code by clicking on the API configure link.

HappyFox requires that the Priority ID and Category ID cannot be nil. This is the ID of the priority and the category with which tickets will be created when a customer reports an issue. 

##### ii. Zendesk Gear credentials:
*Zendesk Account URL*, *Staff Email address* and *API token*.

    HSZendeskGear zenDeskGear = new HSZendeskGear(
            "<Account URL>",
            "<Staff Email Address>",
            "<API Token");

The token can be found in your Zendesk account under Settings > Channels > API.

##### iii. Desk Gear credentials:
*Desk Account URL*, *To Help Email address*, *Staff Email address* and *Staff password*

    HSDeskGear deskGear = new HSDeskGear(
            "<Account URL>",
            "<To Help email address>",
            "<Staff email address>",
            "<Staff password");

##### iv. Email:
*Email address* and *Articles in xml format*


    HSEmailGear emailGear = new HSEmailGear( 
                "example@happyfox.com",
                R.xml.articles);

#### Step 2 - Configure HelpStack with the Gear:
i. Set the Gear object with *HSHelpStack* instance only once. You can do this in the **OnCreate()** method of your app's Main Activity, but it is suggested that you create a custom *Application* class which extends the **Application** class:
  
  
     public class HSApplication extends Application {
      
      HSHelpStack helpStack;
      
      @Override
      public void onCreate() {
        super.onCreate();
        
        // Get the HSHelpStack instance
        helpStack = HSHelpStack.getInstance(this);
        
        // Insert Gear object creation from previous step here
        <GearType> <Gear Object> = new <GearType> (<Credentials>)
        
        // Set the Gear
        helpStack.setGear(<Gear Object>);
      }
      
    }	  
      
ii. Now open your Application Android manifest and set the Application name as your custom application class name. 

     <application
        android:name="HSApplication"
        ...
      />	

#### Step 3 - Entry point in UI:
Add a clickable item (probably a button) in your UI, wherever appropriate. Set a *click listener* to it. Within the *click listener*, use the **showGear** API to open up the HelpStack UI:

    HSHelpStack.getInstance(getActivity()).showGear(getActivity());


#### Step 4 - Theming/Skinning:

It is very easy to customize the HelpStack UI. You might want to do so to make it go along with your app's UI.

We ship sample themes along with the HelpStack library. You can find them in 
**/helpstack/Themes/**, where you will find 5 sample themes - **HSLightTheme** (Default), **HSDarkTheme**, **HSFacebookTheme**, **HSPathTheme** and  **HSPinterestTheme**.

Each theme comes with the following:
- A *colors.xml* and a **hs_custom_theme.xml** defined in **../values/**
- Chat bubble drawables defined in **../drawables/**.


##### Using the sample themes

- Decide which sample theme you want to use
- Include the *theme* and *colors* xml files in your application under **values**
- Include the theme's drawables under your application's **drawables**
- Now you can simply build and run the application. The HelpStack UI will use the styles specified in the chosen theme.


Below is the list of parameters you can configure to change the looks of HelpStack:

**Main List View** 
 <img src="Images/mainlist_style.png" alt="HelpStackthemes" title="screenshots">
 
**Issue Details View**
 <img src="Images/issuedetail_style.png" alt="HelpStackthemes" title="screenshots">


- **hs_backgroundStyle** - Background of all screens
- **hs_listViewStyle** - Articles and issues list
- **hs_listView_headerBackgroundStyle** - Header background of main list view
- **hs_listView_childBackgroundStyle** - Child view background of main list view
- **hs_listView_headerTextStyle** - ListView header text
- **hs_listView_childTextStyle** - ListView child text
- **hs_leftChatBubbleStyle** - Chat screen - left chat bubble style
- **hs_rightChatBubbleStyle** - Chat screen - right chat bubble style
- **hs_left_messageTextStyle** - Chat screen message text style for left chat bubble
- **hs_right_messageTextStyle** - Chat screen message text style for right chat bubble
- **hs_smallTextStyle** - Chat screen more info text style - applied to the sender name and time
- **hs_buttonStyle** - Button Style - applied for report issue button 
- **hs_editTextStyle** - Edit text Style in new user and new issue screen
- **hs_messageEditTextStyle** - Edit text Style used in chat screen - add reply


Override the styles specified in **/values/hs_custom_theme.xml** 

##### Icons and Images

  All the icons used in the HelpStack UI are defined under **Drawables** in the **hs_custom_theme.xml** file. In order to include your own icons, download and add the icons in your applications resources, and override the drawables specified in the theme for the UI to take up your own icons.
  
  Below are the icons used in HelpStack UI  :-

  - **hs_attachment_icon** - Attachment icon used in issue detail screen and New Issue screen
  - **hs_search_icon** - Search icon used in the action bar
  - **hs_disclosure_next** - Disclosure icon used in the main list view child item
  - **hs_add_attachment** - Add attachment icon used in issue detail screen, to add an attachment


##### Note

  If you want the complete look-and-feel of a theme, you might also want to update the color of your app's Action bar.

  
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
