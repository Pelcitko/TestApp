package cz.tul.lp.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.improvelectronics.sync.android.SyncUtilities;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private CanvasView mCanvasView = null;
    private Fragment mDrawFragment = null;
    private SeekBar seekBar1 = null, seekBar2 = null;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        int newW, newH, w, h;
        this.mCanvasView = (CanvasView)this.findViewById(R.id.canvas);
//        this.mDrawFragment = (Fragment)this.findViewById(R.id.draw_fragment);

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
        Log.v("Main LOG", "Bottom " + (findViewById(R.id.bottomView)).getHeight());

        this.mCanvasView.setBottomHeight((findViewById(R.id.bottomView)).getHeight()); //výmysl pro canvas


        this.seekBar1 = (SeekBar)this.findViewById(R.id.seekBar1);
        this.seekBar2 = (SeekBar)this.findViewById(R.id.seekBar2);

        this.seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mCanvasView.setStrokeWidth(progress);
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
//                mCanvasView.setStrokeWidth(progress);
//                return;
//            case TEXT:
//                break;
//            case ERASER:
//                mCanvasView.setStrokeWidth(progress);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Menu selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
                this.mCanvasView.setMode(CanvasView.Mode.TEXT);
                this.mCanvasView.setText("Canvas View");
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
            case R.id.settings:
                Intent i;
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            default:
                Toast.makeText(this, item.toString() + " touched", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
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


    public CanvasView getCanvas() {
        return this.mCanvasView;
    }

}