//  NewUserActivity
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.tenmiles.helpstack.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.model.HSAttachment;

import java.io.FileNotFoundException;
import java.util.UUID;

public class EditAttachmentActivity extends HSActivityParent {

    private final int REQUEST_CODE_PHOTO_PICKER = 100;

    private DrawingView drawView;

    private ImageButton currentPaint;
    private HSAttachment selectedAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hs_activity_edit_attachment);

        drawView = (DrawingView)findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currentPaint = (ImageButton)paintLayout.getChildAt(0);
        currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.hs_select_picture)), REQUEST_CODE_PHOTO_PICKER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hs_edit_attachment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void configureActionBar(ActionBar actionBar) {
        super.configureActionBar(actionBar);
        actionBar.setTitle(getString(R.string.hs_attachment_edit));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            HSActivityManager.finishSafe(this);
            return true;
        }
        else if (id == R.id.save) {
            onSaveClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case REQUEST_CODE_PHOTO_PICKER:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = intent.getData();

                    //User had pick an image.
                    Cursor cursor = this.getContentResolver().query(selectedImage, new String[] {
                            MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.MIME_TYPE }, null, null, null);
                    cursor.moveToFirst();

                    String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));

                    cursor.close();

                    selectedAttachment = HSAttachment.createAttachment(selectedImage.toString(), display_name, mime_type);

                    try {
                        Uri uri = Uri.parse(selectedAttachment.getUrl());
                        Bitmap selectedBitmap;
                        selectedBitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, null);
                        drawView.setCanvasBitmap(selectedBitmap);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
        }
    }

    public void paintClicked(View view) {

        if (view != currentPaint) {
            ImageButton imageButton = (ImageButton)view;
            String color = imageButton.getTag().toString();

            drawView.setColor(color);

            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));

            currentPaint = (ImageButton) view;
        }
    }


    private void onSaveClick() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save");
        saveDialog.setMessage("Save drawing?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                drawView.setDrawingCacheEnabled(true);

                String imageSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        drawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png",
                        "drawing");

                if(imageSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("URI", imageSaved);
                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }

                drawView.destroyDrawingCache();


            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Do you want to discard your changes?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        EditAttachmentActivity.super.onBackPressed();
                    }
                }).create().show();
    }

}
