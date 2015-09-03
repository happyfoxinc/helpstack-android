//  NewIssueFragment
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

package com.tenmiles.helpstack.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.HSHelpStack;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.activities.EditAttachmentActivity;
import com.tenmiles.helpstack.activities.HSActivityManager;
import com.tenmiles.helpstack.activities.NewIssueActivity;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.HSUtils;
import com.tenmiles.helpstack.logic.OnNewTicketFetchedSuccessListener;
import com.tenmiles.helpstack.model.HSAttachment;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSUser;

import java.io.FileNotFoundException;

public class NewIssueFragment extends HSFragmentParent {

    private final int REQUEST_CODE_PHOTO_PICKER = 100;

    public static final int REQUEST_CODE_NEW_TICKET = HomeFragment.REQUEST_CODE_NEW_TICKET;

    public static final String EXTRAS_USER = NewIssueActivity.EXTRAS_USER;
    public static final String RESULT_TICKET = NewIssueActivity.RESULT_TICKET;
    public static final String EXTRAS_SUBJECT = NewIssueActivity.EXTRAS_SUBJECT;
    public static final String EXTRAS_MESSAGE = NewIssueActivity.EXTRAS_MESSAGE;
    public static final String EXTRAS_ATTACHMENT = NewIssueActivity.EXTRAS_ATTACHMENT;

    private HSUser userDetails;
    EditText subjectField, messageField;
    ImageView imageView1;
    HSAttachment selectedAttachment;
    HSSource gearSource;

    public static NewIssueFragment createNewIssueFragment(HSUser user) {
        NewIssueFragment frag = new NewIssueFragment();

        if(user != null) {
            Bundle args = new Bundle();
            args.putSerializable(EXTRAS_USER, user);
            frag.setArguments(args);
        }

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.hs_fragment_new_issue, container, false);

        this.subjectField = (EditText) rootView.findViewById(R.id.subjectField);
        this.messageField = (EditText) rootView.findViewById(R.id.messageField);
        this.imageView1 = (ImageView) rootView.findViewById(R.id.imageView1);
        this.imageView1.setOnClickListener(attachmentClickListener);

        // Read user value
        Bundle args = savedInstanceState;
        if (args == null) {
            args = getArguments();

            if (args != null) {
                userDetails = (HSUser) args.getSerializable(EXTRAS_USER);
            }
        }

        gearSource = HSSource.getInstance(getActivity());

        this.subjectField.setText(gearSource.getDraftSubject());
        this.messageField.setText(gearSource.getDraftMessage());

        if (gearSource.getDraftAttachments() != null && gearSource.getDraftAttachments().length > 0) {
            this.selectedAttachment = gearSource.getDraftAttachments()[0];
            resetAttachmentImage();
        }

        if (!HSHelpStack.getInstance(getActivity()).getShowCredits()) {
            rootView.findViewById(R.id.footerTextLabel).setVisibility(View.GONE);
            rootView.findViewById(R.id.footerDivider).setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRAS_USER, userDetails);
        outState.putString("subject", subjectField.getText().toString());
        outState.putString("message", messageField.getText().toString());
        outState.putSerializable("attachment", selectedAttachment);
    }

    @Override
    public void onPause() {
        super.onPause();

        HSAttachment[] attachmentArray = null;

        if (selectedAttachment != null) {
            attachmentArray = new HSAttachment[1];
            attachmentArray[0] = selectedAttachment;
        }

        gearSource.saveTicketDetailsInDraft(subjectField.getText().toString(), messageField.getText().toString(), attachmentArray);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            subjectField.setText(savedInstanceState.getString("subject"));
            messageField.setText(savedInstanceState.getString("message"));
            selectedAttachment = (HSAttachment) savedInstanceState.getSerializable("attachment");
        }

        resetAttachmentImage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.hs_issue_menu, menu);

        MenuItem clearMenu = menu.findItem(R.id.clearItem);
        MenuItemCompat.setShowAsAction(clearMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItem doneMenu = menu.findItem(R.id.doneItem);

        if (gearSource.isNewUser()) {
            doneMenu.setIcon(getResources().getDrawable(R.drawable.hs_action_forward));
            doneMenu.setTitle(getResources().getText(R.string.hs_next));
        }

        MenuItemCompat.setShowAsAction(doneMenu, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.doneItem) {

            if(getMessage().trim().length() == 0 || getSubject().trim().length() == 0) {
                HSUtils.showAlertDialog(getActivity(), getResources().getString(R.string.hs_error), getResources().getString(R.string.hs_error_subject_message_empty));
                return false;
            }

            HSAttachment[] attachmentArray = null;

            if (selectedAttachment != null) {
                attachmentArray = new HSAttachment[1];
                attachmentArray[0] = selectedAttachment;
            }

            String formattedBody = getMessage();

            if(!gearSource.isNewUser()) {
                getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(true);

                gearSource.createNewTicket("NEW_TICKET", userDetails, getSubject(), formattedBody, attachmentArray,
                        new OnNewTicketFetchedSuccessListener() {

                            @Override
                            public void onSuccess(HSUser udpatedUserDetail, HSTicket ticket) {

                                getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
                                sendSuccessSignal(ticket);
                                clearFormData();
                                Toast.makeText(getActivity(), getResources().getString(R.string.hs_issue_created_raised), Toast.LENGTH_LONG).show();
                            }

                        }, new ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                HSUtils.showAlertDialog(getActivity(), getResources().getString(R.string.hs_error_reporting_issue), getResources().getString(R.string.hs_error_check_network_connection));
                                getHelpStackActivity().setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });
            }
            else {
                HSActivityManager.startNewUserActivity(this, REQUEST_CODE_NEW_TICKET, getSubject(), formattedBody, attachmentArray);
            }

            return true;
        }
        else if(id == R.id.clearItem) {
            clearFormData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {

                Uri selectedImage = Uri.parse(intent.getStringExtra("URI"));

                //User had pick an image.
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        new String[]{
                                ImageColumns.DATA,
                                ImageColumns.DISPLAY_NAME,
                                ImageColumns.MIME_TYPE
                        }, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                }

                String display_name = cursor.getString(cursor.getColumnIndex(ImageColumns.DISPLAY_NAME));
                String mime_type = cursor.getString(cursor.getColumnIndex(ImageColumns.MIME_TYPE));

                cursor.close();

                selectedAttachment = HSAttachment.createAttachment(selectedImage.toString(), display_name, mime_type);

                resetAttachmentImage();
            }
        }
        else if(requestCode == REQUEST_CODE_NEW_TICKET) {
            if (resultCode == HSActivityManager.resultCode_sucess) {
                HSActivityManager.sendSuccessSignal(getActivity(), intent);
            }
        }
    }

    @Override
    public void onDetach() {
        gearSource.cancelOperation("NEW_TICKET");
        super.onDetach();
    }

    private OnClickListener attachmentClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (selectedAttachment == null) {
                Intent intent = new Intent(getActivity(), EditAttachmentActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER);
            }
            else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle(getResources().getString(R.string.hs_attachment));
                alertBuilder.setIcon(R.drawable.hs_attachment);
                String[] attachmentOptions = {getResources().getString(R.string.hs_change), getResources().getString(R.string.hs_remove)};
                alertBuilder.setItems(attachmentOptions, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(getActivity(), EditAttachmentActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER);
                        }
                        else if (which == 1) {
                            selectedAttachment = null;
                            resetAttachmentImage();
                        }
                    }
                });
                alertBuilder.create().show();
            }
        }
    };

    private void resetAttachmentImage() {
        if (selectedAttachment == null) {
            this.imageView1.setImageResource(R.drawable.hs_add_attachment_img);
        }
        else {
            try {
                Uri uri = Uri.parse(selectedAttachment.getUrl());
                Bitmap selectedBitmap;
                selectedBitmap = downscaleAndReadBitmap(getActivity(), uri);
                this.imageView1.setImageBitmap(selectedBitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void clearFormData() {
        this.subjectField.setText("");
        this.messageField.setText("");
        this.selectedAttachment = null;

        resetAttachmentImage();

        gearSource.clearTicketDraft();
    }

    public String getSubject() {
        return subjectField.getText().toString();
    }

    public String getMessage() {
        return messageField.getText().toString();
    }

    public static Bitmap downscaleAndReadBitmap(Context context, Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if ((width_tmp/2 < REQUIRED_SIZE) || (height_tmp/2 < REQUIRED_SIZE)) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public void sendSuccessSignal(HSTicket ticket) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_TICKET, ticket);
        HSActivityManager.sendSuccessSignal(getActivity(), intent);
    }
}
