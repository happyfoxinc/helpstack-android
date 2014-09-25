<p align="center" >
  [<img src="https://dl.dropboxusercontent.com/u/55774910/HelpStack/Helpstack%20by%20Happyfox%20logos.png" alt="HelpStack" title="Logo" />](http://www.helpstack.io/)
</p>


**HelpStack** is a library to provide in-app support for your app's users. 

With HelpStack, you can:
- Show Knowledge Base articles
- Let users create tickets from your app

HelpStack supports the following helpdesk solutions: 
- HappyFox
- Zendesk
- Desk.com

If you don't have a helpdesk solution, you can also configure HelpStack, for users to raise requests via email.

<p align="left" >
  <img src="Images/hs_preview.png" alt="HelpStackthemes" title="screenshots">
</p>

## Installation

Installating the HelpStack library is fairly straight-forward.

#### Eclipse/ADT:
1. Clone or download the library along with its dependencies from the Git repository. 
2. Import it as a library project into your Application. 
3. Set the flag for *manifestmerger.enabled* to *true* in your *project.properties* file:

        manifestmerger.enabled=true

#### Android Studio:
We are working on including HelpStack as a gradle dependency in your Android Studio project. It will be available soon. 
    
## Using the Library

Broadly speaking, there are just 3 steps to begin using HelpStack within your app:

1. Choose a helpdesk solution of your choice and obtain the necessary credentials. These helpdesk solutions will be referred to as **Gears**.
2. Configure HelpStack to work with the Gear of your choice.
2. Add an entry-point for HelpStack in the UI and connect it with HelpStack.
3. Customize the theme of HelpStack according to your choice.


#### Step 1 - Choose and obtain Gear credentials:

Obtain the necessary credentials for the gear of your choice and create a suitable Gear object.

##### i. HappyFox Gear credentials:
*HappyFox Account URL*, *API Key*, *Auth Code*, *Category ID* and *Priority ID*.

    HSHappyfoxGear happyfoxGear = new HSHappyfoxGear(
                "<Account URL>",
                "<Your API Key>",
                "<Your Auth Code>",
                "<Category ID>",
                "<Priority ID>");

##### ii. Zendesk Gear credentials:
*Zendesk Account URL*, *Staff Email address* and *API token*.

    HSZendeskGear zenDeskGear = new HSZendeskGear(
            "<Account URL>",
            "<Staff Email Address>",
            "<API Token");

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
        
        // Insert Gear object creation from previous step here
        <GearType> <Gear Object> = new <GearType> (<Credentials>)
        
        // Setting the Gear
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
**/helpstack/Themes/**, where you will observe 3 sample themes - **HSDarkTheme**, **HSPathTheme** and **HSFacebookTheme**. 

Each theme comes with the following:
- A *colors.xml* and a **hs_custom_theme.xml** defined in **../values/**
- Chat bubble drawables defined in **../drawables/**.


##### Using the sample themes

- Decide which sample theme you want to use
- Include the *theme* and *colors* xml files in your application under **values**
- Include the theme's drawables under your application's **drawables**
- Now you can simply build and run the application. The HelpStack UI will use the styles specified in the chosen theme.


Below is the list of parameters you can configure to change the looks of HelpStack:

<p>
 <img src="Images/mainlist_style.png" alt="HelpStackthemes" title="screenshots">
 <img src="Images/issuedetail_style.png" alt="HelpStackthemes" title="screenshots">
</p>

Look at the [Theming documentation](Themes/Themes.md) for more information.
  
## About

HelpStack is maintained by the folks at [HappyFox](http://www.happyfox.com/). Being an open source project, it also contains work from the HelpStack community.

## Contact

Reach out to us at [@HelpStack](https://twitter.com/HelpStackSDK).

## License

HelpStack is available under the MIT license. See the LICENSE file for more info.







    
