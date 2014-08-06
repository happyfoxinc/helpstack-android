### Introduction

HelpStack comes with a default theme found in **values/hs_default_theme.xml**. It contains the styles used by the UI layouts and the drawables. It makes use of the base styles of UI elements defined in **values/hs_default_theme_base.xml**. 

You can override the default theme with this theme.

### Theme Details

- Name: HSFacebookTheme
- Creator: Tenmiles

This theme comes with the following files:
- drawables
  - hs_fb_leftchatbubble.xml
  - hs_fb_rightchatbubble.xml
- values
  - hs_custom_theme.xml
  - hs_fb_colors.xml

### Using this theme

- Include **hs_custom_theme.xml** and **hs_fb_colors.xml** in your application, under **values**
- Include **hs_fb_leftchatbubble.xml** and **hs_fb_rightchatbubble.xml** in your application, under **drawables**

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


- #### Icons and Images

  All the icons used in the HelpStack UI are defined under **Drawables** in the **hs_custom_theme.xml** file. In order to include your own icons, download and add the icons in your applications resources, and override the drawables specified in the theme for the UI to take up your own icons.
  
  Below are the icons used in HelpStack UI  :

  - **hs_attachment_icon** - Attachment icon used in issue detail screen and New Issue screen
  - **hs_search_icon** - Search icon used in the action bar
  - **hs_disclosure_next** - Disclosure icon used in the main list view child item
  - **hs_add_attachment** - Add attachment icon used in issue detail screen, to add an attachment


- #### Action Bar

  You might want to update the color of your app's Action bar to match the look and feel of this theme.
