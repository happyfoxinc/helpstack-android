### Introduction
  
It is very easy to customize the HelpStack UI. You might want to do so to make it go along with your app's UI.

HelpStack comes with a default theme found in **values/hs_default_theme.xml**. It contains the styles used by the UI layouts and the drawables. It makes use of the base styles of UI elements defined in **values/hs_default_theme_base.xml**. 

We ship sample themes along with the HelpStack library. You can find them in 
**/helpstack/Themes/**, where you will observe 3 sample themes - **HSDarkTheme**, **HSPathTheme** and **HSFacebookTheme**. 

Each theme comes with the following:
- A *colors.xml* and a **hs_custom_theme.xml** defined in **../values/**
- Chat bubble drawables defined in **../drawables/**.


### Using the sample themes

- Decide which sample theme you want to use
- Include the *theme* and *colors* xml files in your application under **values**
- Include the theme's drawables under your application's **drawables**
- Now you can simply build and run the application. The HelpStack UI will use the styles specified in the chosen theme.

### Customizing the UI further

- #### Styles
  In order to customize the UI further, override the styles specified in **/values/hs_custom_theme.xml** 

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

  Below are illustrations of what each style affects:

   **Main List View** 
   
   <p align="center" >
    <img src="../Images/mainlist_style.png" alt="HelpStackthemes" title="screenshots">
  </p>

  **Issue Details View**

  <p align="center" >
  <img src="../Images/issuedetail_style.png" alt="HelpStackthemes" title="screenshots">
  </p>


- #### Icons and Images

  All the icons used in the HelpStack UI are defined under **Drawables** in the **hs_custom_theme.xml** file. In order to include your own icons, download and add the icons in your applications resources, and override the drawables specified in the theme for the UI to take up your own icons.
  
  Below are the icons used in HelpStack UI  :-

  - **hs_attachment_icon** - Attachment icon used in issue detail screen and New Issue screen
  - **hs_search_icon** - Search icon used in the action bar
  - **hs_disclosure_next** - Disclosure icon used in the main list view child item
  - **hs_add_attachment** - Add attachment icon used in issue detail screen, to add an attachment


- #### Note

  If you want the complete look-and-feel of a theme, you might want to update the color of your app's Action bar, as well.
