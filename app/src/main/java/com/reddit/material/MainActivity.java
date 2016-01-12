package com.reddit.material;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.reddit.material.libraries.google.CustomTabActivityHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE = 100;
    private static MainActivity instance;
    private NavigationView navigationView;
    private CustomTabActivityHelper chromeTabsHelper;
    private String subreddit = "";
    private int selectedSort = 0;
    private AlertDialog alertDialog;
    private InputStream inputStream;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instance = this;
        Context context = getBaseContext();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
        Fresco.initialize(context, config);
        ConnectionSingleton.createInstance(context);
        Authentication.newInstance(context);

        if (getIntent().getStringExtra("subreddit") == null) {
            ConnectionSingleton.getInstance().getSubreddits();
            if (Authentication.getInstance().isLoggedIn())
                subreddit = "frontpage";
            else
                subreddit = "all";
            getSupportActionBar().setTitle(subreddit);
            getSupportActionBar().setSubtitle("Hot");
        } else {
            ConnectionSingleton.getInstance().getSubreddits(false);
            subreddit = getIntent().getStringExtra("subreddit");
            changeSubreddit(subreddit, "Hot");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Authentication.getInstance().isLoggedIn())
                    loadSubmitDialog();
                else
                    Toast.makeText(getBaseContext(), "You must be logged in to make a post!", Toast.LENGTH_SHORT)
                            .show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (Authentication.getInstance().isLoggedIn()) {
            navigationView.getMenu().getItem(0).setTitle("Log out");
        }
        chromeTabsHelper = new CustomTabActivityHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        chromeTabsHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        chromeTabsHelper.unbindCustomTabsService(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.menu_sort:
                loadSortDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.login:
                if (Authentication.getInstance().isLoggedIn()) {
                    Authentication.getInstance().logout();
                    ConnectionSingleton.getInstance().reloadSubreddits();
                    navigationView.getMenu().getItem(0).setTitle("Log in");
                } else
                    ConnectionSingleton.getInstance().login(MainActivity.this);
                break;
            case R.id.search:
                Intent intent = new Intent(this, SearchResultsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
        }

        subreddit = item.getTitle().toString();
        if (item.getItemId() != R.id.login) {
            changeSubreddit(subreddit, "Hot");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (alertDialog != null) {
                ImageView imageView = (ImageView) alertDialog.findViewById(R.id.selected_image);
                TextView textView = (TextView) alertDialog.findViewById(R.id.image_text);
                if (data == null) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(Color.RED);
                    textView.setText(R.string.load_image_error);
                    imageView.setVisibility(View.GONE);
                } else {
                    try {
                        imageView.setVisibility(View.VISIBLE);
                        inputStream = getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                        textView.setVisibility(View.GONE);
                    } catch (FileNotFoundException e) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setTextColor(Color.RED);
                        textView.setText(R.string.load_image_error);
                        imageView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void changeSubreddit(String subreddit, String sort) {
        changeSubreddit(subreddit, sort, "");
    }

    private void changeSubreddit(String subreddit, String sort, String time) {
        this.subreddit = subreddit;
        getSupportActionBar().setTitle(subreddit);
        getSupportActionBar().setSubtitle(sort);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, SubredditFragment.newInstance
                (subreddit, sort.toLowerCase(), time)).commit();
    }

    private void loadSortDialog() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                .setView(R.layout.dialog_sort)
                .setTitle("Sort By")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog view = (Dialog) dialog;
                        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_sort);
                        RadioButton radioButton = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
                        selectedSort = radioButton.getId();
                        String sort = radioButton.getText().toString();
                        String[] sortSplit = sort.split(" - ");
                        if (sortSplit.length == 2) {
                            switch (sortSplit[1]) {
                                case "All Time":
                                    changeSubreddit(subreddit, sortSplit[0], "all");
                                    break;
                                case "Past 24 Hours":
                                    changeSubreddit(subreddit, sortSplit[0], "day");
                                    break;
                                default:
                                    changeSubreddit(subreddit, sortSplit[0], sortSplit[1].replace("Past ", "")
                                            .toLowerCase());
                                    break;
                            }
                        } else {
                            changeSubreddit(subreddit, sort);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Dialog view = (Dialog) dialog;
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_sort);
                RadioButton button = (RadioButton) radioGroup.findViewById(selectedSort);
                if (button != null)
                    button.setChecked(true);
                else {
                    button = (RadioButton) radioGroup.findViewById(R.id.hot_radio_button);
                    button.setChecked(true);
                }
            }
        });
        dialog.show();
    }

    private void loadSubmitDialog() {
        final int[] selected = {0};
        alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme)
                .setTitle("Submit to Reddit")
                .setView(R.layout.dialog_submit)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("Select Image...", null)
                .create();

        alertDialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        final Dialog view = (Dialog) dialog;
                        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        neutralButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, PICK_IMAGE);
                            }
                        });
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initSubmitConfirmButton(view, selected[0]);
                            }
                        });
                        neutralButton.setVisibility(View.INVISIBLE);
                        RadioGroup options = (RadioGroup) view.findViewById(R.id.radio_group_options);
                        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId) {
                                    case R.id.radio_text:
                                        selected[0] = 0;
                                        break;
                                    case R.id.radio_url:
                                        selected[0] = 1;
                                        break;
                                    case R.id.radio_image:
                                        selected[0] = 2;
                                        break;
                                }
                                loadSubmitView(view, selected[0]);
                            }
                        });
                        loadSubmitView(view, selected[0]);
                    }
                }

        );
        alertDialog.show();
    }

    private void loadSubmitView(Dialog parent, int checkedID) {
        TextInputLayout textInputLayout = (TextInputLayout) parent.findViewById(R.id.text_url_input_layout);
        EditText textURLEditText = (EditText) parent.findViewById(R.id.text_url_edit);
        EditText subredditText = (EditText) parent.findViewById(R.id.subreddit_edit);
        ImageView selectedImage = (ImageView) parent.findViewById(R.id.selected_image);
        TextView emptyImageText = (TextView) parent.findViewById(R.id.image_text);
        Button selectImageButton = null;
        if (alertDialog != null)
            selectImageButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        textURLEditText.setVisibility(checkedID == 2 ? View.GONE : View.VISIBLE);
        selectedImage.setVisibility(checkedID == 2 && selectedImage.getDrawable() != null ? View.VISIBLE : View.GONE);
        emptyImageText.setVisibility(checkedID == 2 && selectedImage.getDrawable() == null ? View.VISIBLE : View.GONE);
        emptyImageText.setText(R.string.no_image_select);
        emptyImageText.setTextColor(Color.parseColor("#757575"));

        if (checkedID == 0) {
            textInputLayout.setHint("Text");
            textURLEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            if (selectImageButton != null)
                selectImageButton.setVisibility(View.INVISIBLE);
            if (!subreddit.equalsIgnoreCase("frontpage") && !subreddit.equalsIgnoreCase("all")) {
                subredditText.setText(subreddit);
            }
        } else if (checkedID == 1) {
            textInputLayout.setHint("URL");
            textURLEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            if (selectImageButton != null)
                selectImageButton.setVisibility(View.INVISIBLE);
            if (!subreddit.equalsIgnoreCase("frontpage") && !subreddit.equalsIgnoreCase("all")) {
                subredditText.setText(subreddit);
            }
        } else if (checkedID == 2 && selectImageButton != null) {
            selectImageButton.setVisibility(View.VISIBLE);
        }
    }

    private void initSubmitConfirmButton(Dialog parent, int selected) {
        EditText titleEditText = (EditText) parent.findViewById(R.id.title_edit);
        EditText subredditText = (EditText) parent.findViewById(R.id.subreddit_edit);
        String title = titleEditText.getText().toString();
        String subreddit = subredditText.getText().toString();
        if (selected == 0 || selected == 1) {
            EditText textURLEdit = (EditText) parent.findViewById(R.id.text_url_edit);
            String textURL = textURLEdit.getText().toString();
            String errorForm;
            if ((errorForm = validForm(title, subreddit, textURL)).equals("")) {
                ConnectionSingleton.getInstance().post(MainActivity.this, title, textURL, subreddit, selected == 0 ?
                        "self" : "link");
                parent.dismiss();
            } else {
                String[] errors = errorForm.split(", ");
                for (String error : errors) {
                    String errorMessage = "Cannot be empty!";
                    switch (error.toLowerCase()) {
                        case "title":
                            titleEditText.setError(errorMessage);
                            break;
                        case "subreddit":
                            subredditText.setError(errorMessage);
                            break;
                        case "texturl":
                            textURLEdit.setError(errorMessage);
                            break;
                    }
                }
            }
        } else {
            ImageView selectedImage = (ImageView) parent.findViewById(R.id.selected_image);
            TextView errorText = (TextView) parent.findViewById(R.id.image_text);
            Drawable image = selectedImage.getDrawable();
            String errorForm;
            if ((errorForm = validForm(title, image)).equals("")) {
                if (inputStream != null) {
                    new SubmitImageImgurUpload(title, subreddit, MainActivity.this).execute(inputStream);
                }
                parent.dismiss();
            } else {
                String errorMessage = "Cannot be empty!";
                switch (errorForm.toLowerCase()) {
                    case "title":
                        titleEditText.setError(errorMessage);
                        break;
                    case "image":
                        errorText.setText(R.string.select_image);
                        errorText.setTextColor(Color.RED);
                        break;
                }
            }
        }
    }

    private String validForm(String title, Drawable image) {
        String error = "";
        error += title.equals("") ? "Title, " : "";
        error += image == null ? "Image" : "";
        return error;
    }

    private String validForm(String title, String subreddit, String textURL) {
        String error = "";
        error += title.equals("") ? "Title, " : "";
        error += subreddit.equals("") ? "Subreddit, " : "";
        error += textURL.equals("") ? "TextURL" : "";
        return error;
    }
}
