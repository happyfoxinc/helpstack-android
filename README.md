<p align="center" >
  <img src="https://dl.dropboxusercontent.com/u/55774910/HelpStack/Helpstack%20by%20Happyfox%20logos.png" alt="HelpStack" title="Logo">
</p>


**HelpStack** provides you with a simple way of adding a great in-App support for your Android App users. You can integrate any of your favorite HelpDesk solution at ease. It currently comes with *HappyFox* helpdesk solution and *Email* support along with customizable and simple UI to interact with the user. 


<p align="left" >
  <img src="https://raw.githubusercontent.com/happyfoxinc/helpstack-android/master/Images/hs_preview.png?token=6688351__eyJzY29wZSI6IlJhd0Jsb2I6aGFwcHlmb3hpbmMvaGVscHN0YWNrLWFuZHJvaWQvbWFzdGVyL0ltYWdlcy9oc19wcmV2aWV3LnBuZyIsImV4cGlyZXMiOjE0MDQ5ODk4NTB9--fde1f0cc11a7f0d71ee389e9dd76f76fbb5340a9" alt="HelpStackthemes" title="screenshots">
</p>

## How to use HelpStack

You can add HelpStack In-App support in four simple steps-

###Step 1 - Getting HelpStack library

Clone HelpStack for android from the git repository or download the zip file. It contains the HelpStack library along with its dependancy libraries. 

###Step 2 - Adding HelpStack into your android project

Once you have the latest HelpStack, you need to import it into your *Android Application Project*. Please follow the steps:

* File > Import
* Android > Existing Android Code into Workspace, then click *Next*
* Browse and select *helpstack* and click *Finish*

It is important that you set the flag for manifestmerger.enabled to true in your project.properties:

	manifestmerger.enabled=true
	
* HelpStack comes with its own customisable user interface which uses *appcompat_v7* library for ActionBar support. Make sure to add appcombat_v7 library under HelpStack dependency libraries in its Project Properties. 

* Once HelpStack and its dependancies are successfully imported, build it as a Library project and include it in your *Android Application* Dependancy libraries :

	* Right click on your project and go to *Properties*
	* Go to *Android* and under *Library*, add *HelpStack*
	
###Step 3 - Integrating HelpStack in your app

You can integrate your existing *HappyFox* account with HelpStack, or you can integrate HelpStack with a simple *Email* support. In HelpStack, we call the supported helpdesk solutions as *Gears*.

Ideally, you will need to create a gear of your choice and set it up with *HSHelpStack* only once. You can do this in *OnCreate* method of your app's Main Activity, but we suggest you do it the preferred way :-

1. Create a custom *Application* class which extends *Application* class
	
		 public class HSApplication extends Application {
			@Override
			public void onCreate() {
				super.onCreate();
				/* Do the helpstack integration here */
		    }
		  }	  
		  
2. Now open your Application Android manifest and set the Application name as your custom application class name. 

		 <application
			android:name="HSApplication"
			.../>	
	  
To integrate HelpStack with your application, you will need an instance of the HSHelpStack class. Inside your onCreate method of your custom application class or your main activity class, do the following :-

		 public class HSApplication extends Application{

			HSHelpStack helpStack;
	
			@Override
			public void onCreate() {
				super.onCreate();
				helpStack = HSHelpStack.getInstance(this);
				/* Create a happyfox or an email gear and set it to 'helpStack' */
			}


#### 1. Happyfox gear

To integrate your existing HappyFox account into HelpStack, include the following lines of code inside your 'onCreate' method :-
	
		helpStack = HSHelpStack.getInstance(this);
		HSHappyfoxGear happyfoxGear = new HSHappyfoxGear("https://example.happyfox.com",
									   "<YOUR API KEY>",
									   "<YOUR AUTH CODE>", 
									   "<CATEGORY ID>",
									   "<PRIORITY ID>");
		helpStack.setGear(happyfoxGear);

* Getting the API Key and Auth code

	Configuring HappyFox in HelpStack primarily requires the base URL, API Key and Auth code for authenticating the registered HappyFox user. 

	You will find the API key and Auth code in the ‘*Integrations*’ page of your HappyFox account under ‘*Manage*’. You can generate an API key and Auth code by clicking on the API configure link.

* Getting the Priority ID and Category ID

	HappyFox requires that the Priority ID and Category ID cannot be nil. This is the ID of the priority and the category with which tickets will be created when a customer reports an issue. 

		For Priorities and its IDs:
		<base_uri> / <response_format> /priorities/
	
		For categories and its IDs:
		<base_uri> / <response_format> /categories/

	Use API Key and Auth code for authentication.

	*Example:*
	
		https://example.happyfox.com/api/1.1/json/priorities/ 
		https://example.happyfox.com/api/1.1/json/categories/

That is all is required to integrate your existing Happyfox account with helpstack and include it in your Android application. 

#### 2. Email Gear

If you do not use any of the help desk solutions, you can still use HelpStack to provide efficient customer support by configuring with just your email. You can configure email support in Helpstack by including the below lines of code within your onCreate method :-

		HSEmailGear emailGear = new HSEmailGear( "example@happyfox.com",R.xml.articles);
		helpStack.setGear(emailGear);		
		
You need to provide the support email address, and a local xml file where you have defined your FAQs/ Articles. 

You can include Local Articles by creating a xml file (eg: articles.xml) in your application resources and defining the articles within the xml file in the following way :- 

		<articles>

    		<article subject="How do I get started with the app" text=
        	"You can take the app tour from your settings anytime to get a brief overview of how to use the app" />
        	
        	....
        	
        </articles>
  
You can provide an array of articles, where each article contains a *subject* and *text* 

You then have to mention the local article file path as *R.xml.articles* when you set up the Email gear (Please refer above). HelpStack will take care of reading from the provided xml file and displaying your FAQs on the *Help* screen. 

##### Note 
Even if you are using a HelpDesk solution such as HappyFox, you can still opt to show your FAQs/KnowledgeBase articles using a local xml file, instead of letting HelpStack fetch the KBArticles from the server, as there is a possibility that the user may not have a network connection and hence will not be able to view the FAQs all the time. In order to use locally defined Articles, you can create an *articles.xml* file as defined above and after setting up your desired gear (HappyFox gear), include the following lines of code :- 

		helpStack.setGear(happyfoxGear);
		helpStack.ovverideGearArticlesWithLocalArticlePath(R.xml.articles); 
		

### Step 4 - Showing the Help screen

Once you have integrated your helpStack, use the **'showGear'** API call to open up HelpStack UI to show up the FAQs or to report an Issue.

		HSHelpStack.getInstance(getActivity()).showGear(getActivity());
		
## Theming/Skinning 


You can customize the HelpStack UI screens to go with your application's UI. HelpStack comes with its own default theme. We have defined the base styles of the UI elements in **values/hs_default_theme_base.xml**. 

HelpStack comes with a default theme found in **values/hs_default_theme.xml**. It contains the styles used by the UI layouts and the drawables used. 

It is very easy to customize the HelpStack UI. You can refer to the sample themes which we ship along with the HelpStack library. You will find it in 
**/helpstack/Themes/**, where you will find three sample themes - **HSDarkTheme**, **HSPathTheme** and **HSFacebookTheme**. 

Each theme comes with a colors.xml and hs_custom_theme.xml defined in **../values/** and the chat bubble drawables defined in **../drawables/**.


1. Choose one of the sample Themes, include the theme and colors xml in your application under values. Include the specified drawables under your application's drawables. Now you can simply build and run the application and the HelpStack UI shall take the styles specified in the chosen theme.

2. Inorder to customize the UI, override the styles specified in **/values/hs_custom_theme.xml** 

Below is an illustration of the styles you need to override to customize the UI properties :-

 **Main List View** 
 
 <p align="right" >
  <img src="https://raw.githubusercontent.com/happyfoxinc/helpstack-android/master/Images/mainlist_style.png?token=6688351__eyJzY29wZSI6IlJhd0Jsb2I6aGFwcHlmb3hpbmMvaGVscHN0YWNrLWFuZHJvaWQvbWFzdGVyL0ltYWdlcy9tYWlubGlzdF9zdHlsZS5wbmciLCJleHBpcmVzIjoxNDA0OTg5OTE4fQ%3D%3D--7dbb624e2aead4658ebd19705182a3589fd567fa" alt="HelpStackthemes" title="screenshots">
</p>


**Issue Details View**

<p align="right" >
<img src="https://raw.githubusercontent.com/happyfoxinc/helpstack-android/master/Images/issuedetail_style.png?token=6688351__eyJzY29wZSI6IlJhd0Jsb2I6aGFwcHlmb3hpbmMvaGVscHN0YWNrLWFuZHJvaWQvbWFzdGVyL0ltYWdlcy9pc3N1ZWRldGFpbF9zdHlsZS5wbmciLCJleHBpcmVzIjoxNDA0OTg5OTQ4fQ%3D%3D--49119f48c9d3211e5d2e1ec908a4526e7b49fec5" alt="HelpStackthemes" title="screenshots">
</p>




#### Icons and Images

All the icons used in the HelpStack UI are defined under **Drawables** in the theme file. In order to include your own icons, download and add the icons in your applications resources, and override the drawables specified in the theme for the UI to take up your own icons. Below are the icons used in HelpStack UI  :-

**hs_attachment_icon** -
Attachment icon used in issue detail screen and New Issue screen

**hs_search_icon** -
Search icon used in the action bar

**hs_disclosure_next** -
Disclosure icon used in the main list view child item

**hs_add_attachment** -
Add attachment icon used in issue detail screen, to add an attachment

### License

HelpStack is available under the MIT license. See the LICENSE file for more info.







		









