package cz.tul.lp.testapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.improvelectronics.sync.android.SyncUtilities;

import java.util.UUID;

import cz.tul.lp.testapp.CanvasView;
import cz.tul.lp.testapp.R;

public class DrawActivity extends AppCompatActivity {

    private ImageButton mDrawTab,mColorsTab, currPaint;
    private TabHost mTabHost;
    private ViewPager viewPager = null;
    private CanvasView mCanvasView = null;
    private View mSeekFragment = null;
    private View mColorFragment = null;
    private Fragment mDrawFragment = null;
    private SeekBar seekBar1 = null, seekBar2 = null;
    private static final String TAG = "DrawActivity";
    private NavigationView mNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        this.mCanvasView = (CanvasView)this.findViewById(R.id.canvas);

//        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //Fragmenty a navigece
        this.mSeekFragment = (View)this.findViewById(R.id.seeks_fragment);
        this.mColorFragment = (View)this.findViewById(R.id.color_btns_fragment);
        this.mNavigationView = (NavigationView)this.findViewById(R.id.bottomNavigation);
        this.mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_draw:
                        mColorFragment.setVisibility(View.INVISIBLE);
                        mSeekFragment.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_color:
                        mColorFragment.setVisibility(View.VISIBLE);
                        mSeekFragment.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });
        mColorFragment.setVisibility(View.INVISIBLE);

        // Barvičky
        //get the palette and first color button
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setElevation(21);

        // seekbary
        this.seekBar1 = (SeekBar)this.findViewById(R.id.seekBar1);
        this.seekBar2 = (SeekBar)this.findViewById(R.id.seekBar2);
        this.seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mCanvasView.setDrawerSize(progress);
//                    seek1Changed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        this.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mCanvasView.setOpacity(progress);
//                    seek2Changed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        setSeekBars(
                Math.round(this.mCanvasView.getStrokeWidth()),
                Math.round(this.mCanvasView.getOpacity()));

        this.setCanvas();
    }

    private void setCanvas() {
//        int height = (int)(SyncUtilities.PDF_HEIGHT);
//        int width = (int)(SyncUtilities.PDF_WIDTH);
//        LinearLayout.LayoutParams newViewParams = new LinearLayout.LayoutParams(width, height);
//        mCanvasView.setLayoutParams(newViewParams);
    }

    private void initSet() {
        int newW, newH, w, h;

        ///barva pozadí
        mCanvasView.setBaseColor(Color.parseColor("#F7F4E2"));

        float boardRatio = SyncUtilities.PDF_HEIGHT / SyncUtilities.PDF_WIDTH;   //výška/šířka
        Display display = getWindowManager().getDefaultDisplay();
        int dispWidth = display.getWidth();
        h = Math.round(dispWidth*boardRatio);
        w = dispWidth;
        newH = h;
        newW = w;
//        - (findViewById(R.id.bottomView)).getHeight();
        float myRatio = (float) h / w;
        if (myRatio < boardRatio) {
            newH = Math.round((float) w * boardRatio);
            newW = w;
        }
        if (myRatio > boardRatio) {
            newH = h;
            newW = Math.round((float) h / boardRatio);
        }

        newH = (int)SyncUtilities.PDF_HEIGHT;
        newW = (int)SyncUtilities.PDF_WIDTH;
        LinearLayout.LayoutParams newViewParams = new LinearLayout.LayoutParams(newW, newH);
        mCanvasView.setLayoutParams(newViewParams);
        Log.v("Main LOG", "new " + newW + "/" + newH + ", old: " + w + "/" + h + ", poměr: " + boardRatio + ", " + myRatio);
        Log.v("Main LOG", "Bottom " + (findViewById(R.id.bottomNavigation)).getHeight());

//        this.mCanvasView.setBottomHeight((findViewById(R.id.bottomNavigation)).getHeight()); //výmysl pro canvas
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        int viewWidth = 840;
//        int viewHeight = 7202;
//        int canvasWidth = mCanvasView.getWidth();
//        int canvasHeight = mCanvasView.getHeight();
//        int mBottomView = (findViewById(R.id.bottomView)).getHeight();
//
//        float pomer = SyncUtilities.PDF_HEIGHT / SyncUtilities.PDF_WIDTH;   //výška/šířka
//
//        Log.v(TAG, canvasHeight + " " + canvasWidth + " poměr: " + pomer + " " + mBottomView);
//        LinearLayout.LayoutParams newViewParams = new LinearLayout.LayoutParams(viewWidth, viewHeight);
//        mCanvasView.setLayoutParams(newViewParams);
//        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, theSizeIWant));
//    }


    //    private void seek1Changed(int progress) {
//        switch (this.mCanvasView.getMode()){
//            case DRAW:
//                mCanvasView.setDrawerSize(progress);
//                return;
//            case TEXT:
//                break;
//            case ERASER:
//                mCanvasView.setDrawerSize(progress);
//                break;
//        }
//    }
//
//    private void seek2Changed(int progress) {
//        switch (this.mCanvasView.getMode()){
//            case DRAW:
//                mCanvasView.setOpacity(progress);
//                return;
//            case TEXT:
//                break;
//            case ERASER:
//                mCanvasView.setOpacity(progress);
//                break;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draw_menu, menu);
        return true;
    }

    /**
     * Menu selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // získání ID vybrané položky
        switch (item.getItemId())
        {
            case R.id.undo:
                mCanvasView.undo();
                return true;

            case R.id.redo:
                mCanvasView.redo();
                return true;

            case R.id.clear:
                mCanvasView.clear();
                return true;

            case R.id.text:
                textDraw();
                setSeekBars(
                        Math.round(this.mCanvasView.getCurrentFontSize() / 5),
                        Math.round(this.mCanvasView.getPaintOpacity()));
                return true;

            case R.id.pencil:
                this.mCanvasView.setMode(CanvasView.Mode.DRAW);
                setSeekBars(
                        Math.round(this.mCanvasView.getPaintStrokeWidth()),
                        Math.round(this.mCanvasView.getPaintOpacity()));
                return true;

            case R.id.eraser:
                this.mCanvasView.setMode(CanvasView.Mode.ERASER);
                setSeekBars(
                        Math.round(this.mCanvasView.getEraserWidth()),
                        Math.round(this.mCanvasView.getEraserOpacity()));
                return true;

            case R.id.archive:
                mCanvasView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), mCanvasView.getDrawingCache(),
                        "boogie_" + UUID.randomUUID().toString()+".png", "drawing");
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                mCanvasView.destroyDrawingCache();
                return true;

            case R.id.pdfinboard:
                startActivity(new Intent(this, FileBrowsingActivity.class));
                return true;

            default:
                Toast.makeText(this, item.toString() + " touched", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void textDraw() {
        this.mCanvasView.setMode(CanvasView.Mode.TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(this.mCanvasView.getCurrentText());
//        input.selectAll();
        builder.setView(input)
                .setTitle(R.string.dialog_input_text_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCanvasView.setCurrentText(input.getText().toString());
                }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // TODO: Autofocus
//        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        builder.show();
    }


    private void setSeekBars(int level1, int level2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.seekBar1.setProgress(level1, true);
            this.seekBar2.setProgress(level2, true);
        } else {
            this.seekBar1.setProgress(level1);
            this.seekBar2.setProgress(level2);
        }
    }

    //user clicked paint
    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            mCanvasView.setPaintStrokeColor(view.getTag().toString());
            //update ui
        }
    }
    public CanvasView getCanvas() {
        return this.mCanvasView;
    }

}