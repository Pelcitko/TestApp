package cz.tul.lp.testapp.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class DrawActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TabHost mTabHost;
    private ViewPager viewPager = null;
    private CanvasView mCanvasView = null;
    private View mSeekFragment = null;
    private View mColorFragment = null;
    private View mDrawerChooseFragment = null;
    private Fragment mDrawFragment = null;
    private SeekBar seekBar1 = null, seekBar2 = null, seekBar3 = null;
    private ImageButton currPaint = null;
    private ImageButton buttonPen = null;
    private ImageButton buttonLine = null;
    private ImageButton buttonRectangle = null;
    private ImageButton buttonCircle = null;
    private LinearLayout paintLayout = null;
    private Button buttonEllipse = null;
    private Button buttonQuadratic = null;
    private Button buttonQubic = null;
    private static final String TAG = "DrawActivity";
    private NavigationView mNavigationView = null;
    private SharedPreferences preferences = null;
    private boolean pressureEnable = true;
    private int height;
    private int width;
    private Button buttonGone1;
    private Button buttonGone2;
    private boolean goneFlag = true;
    private View goneView1;
    private View goneView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.mCanvasView = (CanvasView)this.findViewById(R.id.canvas);

        //Fragmenty a navigece
        this.mColorFragment  = (View)this.findViewById(R.id.color_btns_fragment);
        this.mSeekFragment   = (View)this.findViewById(R.id.seeks_fragment);
        this.mDrawerChooseFragment = (View)this.findViewById(R.id.drawer_fragment);
        this.buttonPen = (ImageButton) this.findViewById(R.id.penBtn);
        this.buttonLine = (ImageButton) this.findViewById(R.id.lineBtn);
        this.buttonRectangle = (ImageButton) this.findViewById(R.id.rectangleBtn);
        this.buttonCircle = (ImageButton) this.findViewById(R.id.circleBtn);
        this.buttonEllipse = (Button) this.findViewById(R.id.ellipseBtn);
        this.buttonQuadratic = (Button) this.findViewById(R.id.quadraticBtn);
        this.goneView1 = (View) this.findViewById(R.id.gone1);
        this.goneView2 = (View) this.findViewById(R.id.gone2);
        this.buttonQubic = (Button) this.findViewById(R.id.qubicBtn);
        this.buttonGone1 = (Button) this.findViewById(R.id.goneBtn1);
        this.buttonGone2 = (Button) this.findViewById(R.id.goneBtn2);
        this.buttonGone1.setText("↑");
        this.buttonGone2.setText("↑");

//        this.buttonQuadratic.setVisibility(View.GONE);

        height = (int)(SyncUtilities.PDF_HEIGHT);
        width = (int)(SyncUtilities.PDF_WIDTH);
        LinearLayout.LayoutParams newViewParams = new LinearLayout.LayoutParams(width, height);
        mCanvasView.setLayoutParams(newViewParams);
        ///barva pozadí
        mCanvasView.setBaseColor(Color.parseColor("#F7F4E2"));

        // Barvičky
        //get the palette and first color button
        paintLayout = (LinearLayout)this.findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(1);

        // seekbary
        this.seekBar1 = (SeekBar)this.findViewById(R.id.seekBar1);
        this.seekBar2 = (SeekBar)this.findViewById(R.id.seekBar2);
        this.seekBar3 = (SeekBar)this.findViewById(R.id.seekBar3);

        // nastavit listenery
        this.setListeners();

        setSeekBars();
    }

    private void setListeners() {
        paintLayout.setOnLongClickListener(this);
        buttonPen.setOnClickListener(this);
        buttonLine.setOnClickListener(this);
        buttonRectangle.setOnClickListener(this);
        buttonCircle.setOnClickListener(this);
        buttonEllipse.setOnClickListener(this);
        buttonQuadratic.setOnClickListener(this);
        buttonQuadratic.setOnLongClickListener(this);
        buttonQubic.setOnClickListener(this);
        buttonQubic.setOnLongClickListener(this);
        buttonGone1.setOnClickListener(this);
        buttonGone2.setOnClickListener(this);

        this.seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mCanvasView.setDrawerWidth(progress);
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        this.seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mCanvasView.setBlur(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initSet() {
//        int newW, newH, w, h;
//
//
//        float boardRatio = SyncUtilities.PDF_HEIGHT / SyncUtilities.PDF_WIDTH;   //výška/šířka
//        Display display = getWindowManager().getDefaultDisplay();
//        int dispWidth = display.getWidth();
//        h = Math.round(dispWidth*boardRatio);
//        w = dispWidth;
//        newH = h;
//        newW = w;
////        - (findViewById(R.id.bottomView)).getHeight();
//        float myRatio = (float) h / w;
//        if (myRatio < boardRatio) {
//            newH = Math.round((float) w * boardRatio);
//            newW = w;
//        }
//        if (myRatio > boardRatio) {
//            newH = h;
//            newW = Math.round((float) h / boardRatio);
//        }
//
//        newH = (int)SyncUtilities.PDF_HEIGHT;
//        newW = (int)SyncUtilities.PDF_WIDTH;
//        LinearLayout.LayoutParams newViewParams = new LinearLayout.LayoutParams(newW, newH);
//        mCanvasView.setLayoutParams(newViewParams);
//        Log.v("Main LOG", "new " + newW + "/" + newH + ", old: " + w + "/" + h + ", poměr: " + boardRatio + ", " + myRatio);
//        Log.v("Main LOG", "Bottom " + (findViewById(R.id.bottomNavigation)).getHeight());

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
//                mCanvasView.setStrokeOpacity(progress);
//                return;
//            case TEXT:
//                break;
//            case ERASER:
//                mCanvasView.setStrokeOpacity(progress);
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
                mDrawerChooseFragment.setVisibility(View.GONE);
                setSeekBars();
                return true;

            case R.id.pencil:
                this.mCanvasView.setMode(CanvasView.Mode.DRAW);
                mDrawerChooseFragment.setVisibility(View.VISIBLE);
                setSeekBars();
                return true;

            case R.id.eraser:
                this.mCanvasView.setMode(CanvasView.Mode.ERASER);
                mDrawerChooseFragment.setVisibility(View.GONE);
                setSeekBars();
                return true;
            case R.id.pressure:
                pressureEnable = !item.isChecked();
                mCanvasView.enablePressure(pressureEnable);
                item.setChecked(pressureEnable);
                return true;
            case R.id.redraw:
                mCanvasView.setRedraw(item.isChecked());
                item.setChecked(!item.isChecked());
                return true;
            case R.id.archive:
                mCanvasView.getBitmap();
                String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(),
                        mCanvasView.getScaleBitmap(width*2, height*2),
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
//                mCanvasView.destroyDrawingCache();

//                mCanvasView.setDrawingCacheEnabled(true);
//                String imgSaved = MediaStore.Images.Media.insertImage(
//                        getContentResolver(), mCanvasView.getDrawingCache(),
//                        "boogie_" + UUID.randomUUID().toString()+".png", "drawing");
//                if(imgSaved!=null){
//                    Toast savedToast = Toast.makeText(getApplicationContext(),
//                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
//                    savedToast.show();
//                }
//                else{
//                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
//                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
//                    unsavedToast.show();
//                }
//                mCanvasView.destroyDrawingCache();
                return true;

//            case R.id.pdfinboard:
//                startActivity(new Intent(this, FileBrowsingActivity.class));
//                return true;

            default:
                Toast.makeText(this, item.toString() + " touched", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when a btn has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.penBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.PEN);
                break;
            case R.id.lineBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.LINE);
                break;
            case R.id.rectangleBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.RECTANGLE);
                break;
            case R.id.circleBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.CIRCLE);
                break;
            case R.id.ellipseBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.ELLIPSE);
                break;
            case R.id.quadraticBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.QUBIC_BEZIER);
                break;
            case R.id.qubicBtn:
                this.mCanvasView.setDrawer(CanvasView.Drawer.QUBIC_BEZIER);
                break;
            case R.id.goneBtn1:
            case R.id.goneBtn2:
                Log.w("LONG CLICK: ", " " + v.getId());
                if (goneFlag){
                    buttonGone1.setText("↓");
                    buttonGone2.setText("↓");
                    goneFlag = false;
                    this.goneView1.setVisibility(View.VISIBLE);
                    this.goneView2.setVisibility(View.VISIBLE);
                }else {
                    buttonGone1.setText("↑");
                    buttonGone2.setText("↑");
                    goneFlag = true;
                    this.goneView1.setVisibility(View.GONE);
                    this.goneView2.setVisibility(View.GONE);
                }
                break;
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


    private void  setSeekBars(){
        setSeekBars(
                Math.round(this.mCanvasView.getDrawerWidth()),
                Math.round(this.mCanvasView.getOpacity()),
                Math.round(this.mCanvasView.getBlur())
                );
    }


    private void setSeekBars(int level1, int level2, int level3) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.seekBar1.setProgress(level1, true);
            this.seekBar2.setProgress(level2, true);
            this.seekBar3.setProgress(level3, true);
        } else {
            this.seekBar1.setProgress(level1);
            this.seekBar2.setProgress(level2);
            this.seekBar3.setProgress(level3);
        }
    }

    //user clicked paint
    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
            Log.w("LONG CLICK: ", " " + currPaint.getId());
            mCanvasView.setPaintStrokeColor(view.getTag().toString());
            //update ui
            this.currPaint.setImageDrawable(getDrawable(R.drawable.color_btn));
            this.currPaint = (ImageButton)view;
            this.currPaint.setOnLongClickListener(this);
            this.currPaint.setImageDrawable(getDrawable(R.drawable.check));
        }
    }

    public boolean getPressureEnable() {
        return pressureEnable;
    }


    public CanvasView getCanvas() {
        return this.mCanvasView;
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.quadraticBtn:
                this.buttonQuadratic.setVisibility(View.GONE);
                this.mCanvasView.setDrawer(CanvasView.Drawer.QUBIC_BEZIER);
                this.buttonQubic.setVisibility(View.VISIBLE);
                return true;
            case R.id.qubicBtn:
                this.buttonQubic.setVisibility(View.GONE);
                this.mCanvasView.setDrawer(CanvasView.Drawer.QUADRATIC_BEZIER);
                this.buttonQuadratic.setVisibility(View.VISIBLE);
                return true;
            default:
                Log.w("LONG CLICK: ", " " + v.getId());
                return true;
        }
    }
}