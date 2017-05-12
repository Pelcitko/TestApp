package cz.tul.lp.testapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.improvelectronics.sync.android.BpNote;
import com.improvelectronics.sync.android.BpText;
import com.improvelectronics.sync.android.SyncCaptureReport;
import com.improvelectronics.sync.android.SyncPath;
import com.improvelectronics.sync.android.SyncUtilities;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * This class defines fields and methods for drawing.
 */

public class CanvasView extends View{

    // Enumeration for Mode
    public enum Mode {
        DRAW,
        TEXT,
        ERASER;
    }

    // Enumeration for Drawer
    public enum Drawer {
        PEN,
        LINE,
        RECTANGLE,
        CIRCLE,
        ELLIPSE,
        QUADRATIC_BEZIER,
        QUBIC_BEZIER;
    }

    private Context context = null;
    private Canvas canvas   = null;
    private Bitmap bitmap   = null;

//    private List<Path> pathLists    = new ArrayList<>();
//    private List<SyncPath> pathLists = new ArrayList<>();
//    private List<BpText> textLists = new ArrayList<>();
//    private List<Paint> paintLists   = new ArrayList<>();
    private BpNote data = null;

    private float strokeWidth = 3F;
    private int strokeOpacity = 255;
    private float strokeBlur  = 0F;

    // for Eraser
    private int baseColor     = Color.WHITE;
    private float eraserWidth = strokeWidth * 10;
    private int eraserOpacity = 255;
    private float eraserBlur  = 0F;

    // for Undo, Redo
//    private int historyPointer = 0;

    // Flags
    private Mode mode      = Mode.DRAW;
    private Drawer drawer  = Drawer.PEN;
    private boolean isDown = false;
    private boolean isStylusDown  = false;
    private boolean isStylusOver  = false;
    private boolean redraw = true;
    private boolean containPressure = true;

    // for Paint
    private Paint.Style paintStyle = Paint.Style.STROKE;
    private int paintStrokeColor   = Color.BLACK;
    private int paintFillColor     = Color.BLACK;
    private float drawerWidth      = strokeWidth;
    private int drawerOpacity      = strokeOpacity;
    private float drawerBlur       = strokeBlur;
    private Paint.Cap lineCap      = Paint.Cap.ROUND;

    // for Mouse
    private PointF mouse    = null;
    private Paint mousePaint = null;

    // for BB
    private float sensitivity       = 21f;

    // for BpText
//    private Paint textPaint       = new Paint();
    private String currentText = "";
    private Typeface fontFamily = Typeface.DEFAULT;
    private float fontSize = 32F;
    private Paint.Align textAlign = Paint.Align.RIGHT;  // fixed
    private int textOpacity = 255;
    private float textBlur  = 0F;

    // for Drawer
    private float startX   = 0F;
    private float startY   = 0F;
    private float controlX = 0F;
    private float controlY = 0F;

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup(context);
    }

    /**
     * Invalidate the whole view.
     * If the view is visible and redraw flag is set to true,
     * {@link #onDraw(Canvas)} will be called at some point in
     * the future.
     */
    @Override
    public void invalidate() {
        if (redraw)
            super.invalidate();
    }

    /**
     * Copy Constructor
     *
     * @param context
     * @param attrs
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup(context);
    }

    /**
     * Copy Constructor
     *
     * @param context
     */
    public CanvasView(Context context) {
        super(context);
        this.setup(context);
    }

    /**
     * Common initialization.
     *
     * @param context
     */
    private void setup(Context context) {
        this.context = context;

        this.data = new BpNote(this.createPaint(), this.baseColor);
        this.mouse =new PointF();
        this.mousePaint = new Paint();
        this.mousePaint.setStrokeWidth(0);
        this.mousePaint.setTextSize(22);

//        this.pathLists.add(new Path());
//        this.pathLists.add(new SyncPath());
//        this.textLists.add(new BpText());
//        this.paintLists.add(this.createPaint());
//        this.historyPointer++;

//        this.textPaint.setARGB(0, 255, 255, 255);   //white
    }

//    /**
//     * view assigned size
//     *
//     * @param w
//     * @param h
//     * @param oldw
//     * @param oldh
//     */
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        int newW, newH;
//        float ratio = SyncUtilities.PDF_HEIGHT / SyncUtilities.PDF_WIDTH;   //výška/šířka
//        float oldRatio = (float)(h - bottomHeight) / w;
//        newH = h - bottomHeight;
//        newW = Math.round((float)h/ratio);
//
////        if (newH < h - bottomHeight) {
////            newH = h - bottomHeight;
////            newW = Math.round(newH / ratio);
////        }
//
////        if (oldRatio < ratio) {
////            newH = Math.round(h * ratio);
////        }
//        // ...a tady se tu bude scalovat!
//        Log.v("Můj LOG", oldw + "/" + oldh + ", " + w + "/" + h + ", poměr: " + ratio + ", " + oldRatio);
//    }



    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint This is returned as the instance of Paint
     */
    private Paint createPaint() {
        return this.createPaint(this.drawerWidth);
    }

    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint This is returned as the instance of Paint
     */
    private Paint createPaint(float strWidth) {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(this.paintStyle);
        paint.setStrokeWidth(strWidth);
        paint.setStrokeCap(this.lineCap);
        paint.setStrokeJoin(Paint.Join.ROUND); //added
//        paint.setStrokeJoin(Paint.Join.MITER);  // fixed

        // for BpText
        if (this.mode == Mode.TEXT) {
            paint.setTypeface(this.fontFamily);
            paint.setTextSize(this.fontSize);
            paint.setTextAlign(this.textAlign);
            paint.setStrokeWidth(0F);
        }

        if (this.mode == Mode.ERASER) {
            // Eraser
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            paint.setARGB(0, 0, 0, 0);
            paint.setColor(this.baseColor);
//             paint.setShadowLayer(this.blur, 0F, 0F, this.baseColor);
        } else {
            // Otherwise
            paint.setColor(this.paintStrokeColor);
        }
        paint.setShadowLayer(this.drawerBlur, 0F, 0F, this.paintStrokeColor);
        paint.setAlpha(this.drawerOpacity);

        return paint;
    }

    /**
     * This method initialize Path.
     * Namely, this method creates the instance of Path,
     * and moves current position.
     *
     * @param event This is argument of onTouchEvent method
     * @return path This is returned as the instance of Path
     */
    private SyncPath createPath(MotionEvent event) {
        return createPath(event.getX(), event.getY());
    }

    /**
      This method initialize Path.
     * Namely, this method creates the instance of Path,
     * and moves current position.
     *
     * @param x
     * @param y
     * @return path This is returned as the instance of Path
     */
    private SyncPath createPath(float x, float y) {
        SyncPath path = new SyncPath();
//        Path path = new Path();

        // Save for ACTION_MOVE
        this.startX = x;
        this.startY = y;

        path.moveTo(this.startX, this.startY);

        return path;
    }

    private BpText createText(float x, float y) {
        BpText text = new BpText(this.currentText);

        this.startX = x;
        this.startY = y;

        text.moveTo(this.startX, this.startY);
        return text;
    }

    public void onBBMove(SyncPath path) {
        float strWidth = path.getStrokeWidth();
        if (strWidth < this.sensitivity)
            return;

        if (containPressure){
            strWidth *= this.strokeWidth / 70;
            path.transform(getTransformMatrix());
            path.setStrokeWidth(strWidth);
            this.data.updateHistory(path, this.createPaint(strWidth));
        } else {
            List<PointF> ps = path.getPoints();
            PointF p = ps.get(ps.size() - 2);
            p = transform(p);
            onStylusMove(p.x, p.y);
        }

        this.invalidate();
    }

    private PointF transform(PointF p) {
        float x = SyncCaptureReport.MAX_Y - p.y;
        float y = p.x;
        x *= this.getWidth()  / SyncCaptureReport.MAX_Y;
        y *= this.getHeight() / SyncCaptureReport.MAX_X;
        return new PointF(x, y);
    }

    public Matrix getTransformMatrix(){
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        float trX = this.getWidth()  / SyncCaptureReport.MAX_Y;
        float trY = this.getHeight() / SyncCaptureReport.MAX_X;
        matrix.postScale(trX, trY);
        matrix.postTranslate(SyncUtilities.PDF_WIDTH, 0f);
        return matrix;
    }


//    /**
//     * This method updates the lists for the instance of Path and Paint.
//     * "Undo" and "Redo" are enabled by this method.
//     *
//     * @param path the instance of Path
//     */
//    private void updateHistory(SyncPath path) {
//        if (this.historyPointer == this.pathLists.size()) {
//            this.pathLists.add(path);
//            this.textLists.add(null);
//            this.paintLists.add(this.createPaint());
//            this.historyPointer++;
//        } else {
//            // On the way of Undo or Redo
//            this.pathLists.set(this.historyPointer, path);
//            this.textLists.set(this.historyPointer, null);
//            this.paintLists.set(this.historyPointer, this.createPaint());
//            this.historyPointer++;
//
//            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
//                this.pathLists.remove(this.historyPointer);
//                this.textLists.remove(this.historyPointer);
//                this.paintLists.remove(this.historyPointer);
//            }
//        }
//    }
//
//
//    /**
//     * This method updates the lists for the instance of Path and Paint.
//     * "Undo" and "Redo" are enabled by this method.
//     *
//     * @param text
//     */
//    private void updateHistory(BpText text) {
//        if (this.historyPointer == this.pathLists.size()) {
//            this.pathLists.add(null);
//            this.textLists.add(text);
//            this.paintLists.add(this.createPaint());
//            this.historyPointer++;
//        } else {
//            // On the way of Undo or Redo
//            this.pathLists.set(this.historyPointer, null);
//            this.textLists.set(this.historyPointer, text);
//            this.paintLists.set(this.historyPointer, this.createPaint());
//            this.historyPointer++;
//
//            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
//                this.pathLists.remove(this.historyPointer);
//                this.textLists.remove(this.historyPointer);
//                this.paintLists.remove(this.historyPointer);
//            }
//        }
//    }

//    /**
//     * This method gets the instance of Path that pointer indicates.
//     *
//     * @return the instance of Path
//     */
//    private Path getCurrentPath() {
//        return this.pathLists.get(this.historyPointer - 1);
//    }

//    /**
//     * This method gets the instance of BpText that pointer indicates.
//     *
//     * @return the instance of BpText
//     */
//    private BpText getCurrentTextObj() {
//        return this.textLists.get(this.historyPointer - 1);
//    }

    /**
     * This method draws currentText.
     *
     * @param canvas the instance of Canvas
     */
    private void drawText(BpText text, Paint paint, Canvas canvas) {
        String strText = text.getText();
        int textLength = text.getText().length();

        if (textLength <= 0) {
            return;
        }

//        if (this.mode == Mode.TEXT) {
////            this.currentTextX = bpText.getX();
////            this.currentTextY = bpText.getY();
//
//            this.textPaint = this.createPaint();
//        }

        float textX    = text.getX();
        float textY    = text.getY();

        Paint paintForMeasureText = new Paint();

        // Line break automatically
        float textWidth   = paintForMeasureText.measureText(strText);
        float lengthOfChar = textWidth / (float)textLength;
        float restWidth    = this.canvas.getWidth() - textX;  // currentText-align : right
        int numChars       = (lengthOfChar <= 0) ? 1 : (int) Math.floor((double)(restWidth / lengthOfChar));  // The number of characters at 1 line
        int modNumChars    = (numChars < 1) ? 1 : numChars;
        float y            = textY;

        for (int i = 0, len = textLength; i < len; i += modNumChars) {
            String substring = "";

            if ((i + modNumChars) < len) {
                substring = strText.substring(i, (i + modNumChars));
            } else {
                substring = strText.substring(i, len);
            }

            y += text.getFontSize();

            paint.setStyle(Paint.Style.FILL);

            canvas.drawText(substring, textX, y, paint);
        }
        return;
    }

//    /**
//     * This method draws currentText.
//     *
//     * @param canvas the instance of Canvas
//     */
//    private void drawText(Canvas canvas) {
//        if (this.currentText.length() <= 0) {
//            return;
//        }
//
//        if (this.mode == Mode.TEXT) {
//            this.currentTextX = this.startX;
//            this.currentTextY = this.startY;
//
//            this.textPaint = this.createPaint();
//        }
//
//        float textX = this.currentTextX;
//        float textY = this.currentTextY;
//
//        Paint paintForMeasureText = new Paint();
//
//        // Line break automatically
//        float textLength   = paintForMeasureText.measureText(this.currentText);
//        float lengthOfChar = textLength / (float)this.currentText.length();
//        float restWidth    = this.canvas.getWidth() - textX;  // currentText-align : right
//        int numChars       = (lengthOfChar <= 0) ? 1 : (int) Math.floor((double)(restWidth / lengthOfChar));  // The number of characters at 1 line
//        int modNumChars    = (numChars < 1) ? 1 : numChars;
//        float y            = textY;
//
//        for (int i = 0, len = this.currentText.length(); i < len; i += modNumChars) {
//            String substring = "";
//
//            if ((i + modNumChars) < len) {
//                substring = this.currentText.substring(i, (i + modNumChars));
//            } else {
//                substring = this.currentText.substring(i, len);
//            }
//
//            y += this.fontSize;
//
//            canvas.drawText(substring, textX, y, this.textPaint);
//        }
//    }
    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param event This is argument of onTouchEvent method
     */
    private void onActionDown(MotionEvent event) {
        onActionDown(event.getX(), event.getY());
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param x
     * @param y
     */
    private void onActionDown(Float x, Float y) {
        switch (this.mode) {
            case DRAW   :
            case ERASER :
                if ((this.drawer != Drawer.QUADRATIC_BEZIER) && (this.drawer != Drawer.QUBIC_BEZIER)) {
                    // Oherwise
                    this.data.updateHistory(this.createPath(x, y), this.createPaint());
                    this.isDown = true;
                } else {
                    // Bezier
                    if ((this.startX == 0F) && (this.startY == 0F)) {
                        // The 1st tap
                        this.data.updateHistory(this.createPath(x, y), this.createPaint());
                    } else {
                        // The 2nd tap
                        this.controlX = x;
                        this.controlY = y;

                        this.isDown = true;
                    }
                }

                break;
            case TEXT   :
                this.data.updateHistory(this.createText(x, y), this.createPaint());
                this.isDown = true;
                break;
            default :
                break;
        }
    }
    /**
     * This method defines processes on MotionEvent.ACTION_MOVE
     *
     * @param event This is argument of onTouchEvent method
     */
    private void onActionMove(MotionEvent event) {
        this.onActionMove(event.getX(), event.getY());
    }

    /**
     * This method defines processes on MotionEvent.ACTION_MOVE
     *
     * @param x
     * @param y
     */
    private  void  onActionMove(float x, float y) {

        switch (this.mode) {
            case DRAW   :
            case ERASER :

                if ((this.drawer != Drawer.QUADRATIC_BEZIER) && (this.drawer != Drawer.QUBIC_BEZIER)) {
                    if (!isDown) {
                        return;
                    }

                    Path path = this.data.getCurrentPath();

                    switch (this.drawer) {
                        case PEN :
                            path.lineTo(x, y);
                            break;
                        case LINE :
                            path.reset();
                            path.moveTo(this.startX, this.startY);
                            //korekce
                            x -= (this.getWidth()  / 2 - x) / 2.5;
                            y -= (this.getHeight() / 2 - y) / 3;
                            path.lineTo(x, y);
                            break;
                        case RECTANGLE :
                            path.reset();
                            path.addRect(this.startX, this.startY, x, y, Path.Direction.CCW);
                            break;
                        case CIRCLE :
                            double distanceX = Math.abs((double)(this.startX - x));
                            double distanceY = Math.abs((double)(this.startX - y));
                            double radius    = Math.sqrt(Math.pow(distanceX, 2.0) + Math.pow(distanceY, 2.0));

                            path.reset();
                            path.addCircle(this.startX, this.startY, (float)radius, Path.Direction.CCW);
                            break;
                        case ELLIPSE :
                            RectF rect = new RectF(this.startX, this.startY, x, y);

                            path.reset();
                            path.addOval(rect, Path.Direction.CCW);
                            break;
                        default :
                            break;
                    }
                } else {
                    if (!isDown) {
                        return;
                    }

                    Path path = this.data.getCurrentPath();

                    path.reset();
                    path.moveTo(this.startX, this.startY);
                    path.quadTo(this.controlX, this.controlY, x, y);
                }

                break;
            case TEXT :
                BpText bpText = this.data.getCurrentText();
                //korekce
                x -= (this.getWidth() / 2 - x - 100) / 2.5;
                y += 70;
                bpText.moveTo(x, y);
                break;
            default :
                break;
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_UP
     *
     * @param event This is argument of onTouchEvent method
     */
    private void onActionUp(MotionEvent event) {
        if (isDown) {
            this.startX = 0F;
            this.startY = 0F;
            this.isDown = false;
        }
    }

    public void onStylusUp() {
        if (isStylusDown) {
            this.startX = 0F;
            this.startY = 0F;
            this.isStylusDown = false;
            this.isStylusOver = true;
            return;
        }
        if (!isStylusOver)
            this.isStylusOver = true;
    }

    public void onStylusOver(long x, long y) {
        this.onStylusUp();
        mouse.set(x, y);
        mouse = transform(mouse);
        this.invalidate();
    }


    /**
     * This method updates the instance of Canvas (View)
     *
     * @param canvas the new instance of Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Before "drawPath"
        canvas.drawColor(this.baseColor);

        if (this.bitmap != null) {
            canvas.drawBitmap(this.bitmap, 0F, 0F, new Paint());
        }

        for (int i = 0; i < this.data.getHistoryPointer(); i++) {
            Path path   = this.data.getPath(i);
            BpText bpText = this.data.getText(i);
            Paint paint = this.data.getPaint(i);

            if (path != null)
                canvas.drawPath(path, paint);
            else
                this.drawText(bpText, paint, canvas);
//                canvas.drawText(bpText.getText(), bpText.getX(), bpText.getY(), paint);
        }

        if (this.isStylusOver)
            canvas.drawText("☼", mouse.x-9, mouse.y+5, mousePaint);

        this.canvas = canvas;
    }

    public void onStylusMove(float x, float y) {
        // byl dole
        if (isStylusDown) {
            this.onActionMove(x, y);
        // nový dotyk
        }else{
            this.isStylusDown = true;
            this.onActionDown(x, y);
        }
    }

    public void onStylusMoveUp() {
        if (isStylusDown) {
            this.isStylusDown = false;
        }
    }

    /**
     * This method set event listener for drawing.
     *
     * @param event the instance of MotionEvent
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (redraw) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.isStylusOver = false;
                    this.onActionDown(event);
                    break;
                case MotionEvent.ACTION_MOVE :
                    this.onActionMove(event);
                    break;
                case MotionEvent.ACTION_UP :
                    this.onActionUp(event);
                    break;
                default :
                    break;
            }

            this.invalidate();
         } else {
            super.invalidate();
        }

        return true;
    }

    /**
     * Invoke, if mode will be changed.
     */
    private void onModeChanged(Mode newMode) {
        if (newMode.equals(this.mode))
            return;
        else {
            // store
            switch (this.mode) {

                case DRAW:
                    this.strokeWidth   = this.drawerWidth;
                    this.strokeOpacity = this.drawerOpacity;
                    this.strokeBlur    = this.drawerBlur;
                    break;

                case ERASER:
                    this.eraserWidth   = this.drawerWidth;
                    this.eraserOpacity = this.drawerOpacity;
                    this.eraserBlur    = this.drawerBlur;
                    break;

                case TEXT:
                    this.textOpacity   = this.drawerOpacity;
                    this.textBlur      = this.drawerBlur;
                    break;
            }
            // and restore
            switch (newMode) {

                case DRAW:
                    this.drawerWidth    = this.strokeWidth;
                    this.drawerOpacity  = this.strokeOpacity;
                    this.drawerBlur     = this.strokeBlur;
                    return;

                case ERASER:
                    this.drawerWidth    = this.eraserWidth;
                    this.drawerOpacity  = this.eraserOpacity;
                    this.drawerBlur     = this.eraserBlur;
                    return;

                case TEXT:
                    this.drawerOpacity  = this.textOpacity;
                    this.drawerBlur     = this.textBlur;
                    return;
            }
        }

    }

    /**
     * This method is getter for mode.
     *
     * @return
     */
    public Mode getMode() {
        return this.mode;
    }

    /**
     * This method is setter for mode.
     *
     * @param mode
     */
    public void setMode(Mode mode) {
        onModeChanged(mode);
        this.mode = mode;
    }

    /**
     * This method is getter for drawer.
     *
     * @return
     */
    public Drawer getDrawer() {
        return this.drawer;
    }

    /**
     * This method is setter for drawer.
     *
     * @param drawer
     */
    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    /**
     * This method draws canvas again for Undo.
     *
     * @return If Undo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean undo() {
        if (this.data.undo()) {
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method draws canvas again for Redo.
     *
     * @return If Redo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean redo() {
        if (this.data.redo()) {
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method initializes canvas
     * and keep history.
     *
     * @return
     */
    public void clear() {
//        Path path = new Path();
        SyncPath path = new SyncPath();
        path.moveTo(0F, 0F);
        path.addRect(0F, 0F, 1000F, 1000F, Path.Direction.CCW);
        path.close();

        Paint paint = new Paint();
        paint.setColor(this.baseColor);
        paint.setStyle(Paint.Style.FILL);

        this.data.set(path, null, paint);

        // Clear
        this.invalidate();
    }

    /**
     * This method initializes canvas.
     *
     * @return
     */
    public void forceClear() {

        this.setup(this.context);
        this.invalidate();
    }

    /**
     * Convert DP to Pixels
     *
     * @param dp
     * @param ctx
     * @return
     */
    public float dpToPixels(int dp, Context ctx) {
        Resources r = ctx.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
        return px;
    }
    /**
     * Convert Pixels to DP
     *
     * @param px
     * @param ctx
     * @return
     */
    public float pixelsToDp(float px, Context ctx) {
        Resources r = ctx.getResources();
        int  dp = Math.round(px/(r.getDisplayMetrics().densityDpi/160f));
        return dp;
    }

    /**
     * Setter for pressure containing
     *
     * @param set
     */
    public void enablePressure(boolean set) {
        this.containPressure = set;
    }

    /**
     * This method is getter for canvas background color
     *
     * @return
     */
    public int getBaseColor() {
        return this.baseColor;
    }

    /**
     * This method is setter for canvas background color
     *
     * @param color
     */
    public void setBaseColor(int color) {
        this.baseColor = color;
    }

    /**
     * This method is getter for drawn currentText.
     *
     * @return
     */
    public String getCurrentText() {
        return this.currentText;
    }

    /**
     * This method is setter for drawn currentText.
     *
     * @param currentText
     */
    public void setCurrentText(String currentText) {
        this.currentText = currentText;
    }

    /**
     * This method is getter for stroke or fill.
     *
     * @return
     */
    public Paint.Style getPaintStyle() {
        return this.paintStyle;
    }

    /**
     * This method is setter for stroke or fill.
     *
     * @param style
     */
    public void setPaintStyle(Paint.Style style) {
        this.paintStyle = style;
    }

    /**
     * This method is getter for stroke color.
     *
     * @return
     */
    public int getPaintStrokeColor() {
        return this.paintStrokeColor;
    }

    /**
     * This method is setter for stroke color.
     *
     * @param color
     */
    public void setPaintStrokeColor(int color) {
        this.paintStrokeColor = color;
    }

    /**
     * This method is setter for stroke color.
     *
     * @param colorString
     */
    public void setPaintStrokeColor(String colorString) {
        this.paintStrokeColor = Color.parseColor(colorString);
    }

    /**
     * This method is getter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @return
     */
    public int getPaintFillColor() {
        return this.paintFillColor;
    };

    /**
     * This method is setter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @param color
     */
    public void setPaintFillColor(int color) {
        this.paintFillColor = color;
    }


    /**
     * This method is getter for stroke width, or text size
     * depending on the current Mode.
     *
     * @return
     */
    public float getDrawerWidth() {
        if (this.mode.equals(Mode.TEXT))
            return this.drawerWidth / 5;
        else
            return this.drawerWidth;
    }

    /**
     * This method is setter for stroke width, or text size
     * depending on the current Mode.
     *
     * @param width must be > 0
     */
    public void setDrawerWidth(float width) {
        if (width > 0)
            this.drawerWidth = width;
        else
            this.drawerWidth = 1F;

        switch (this.mode) {

            case DRAW:
                this.strokeWidth = drawerWidth;
                break;

            case ERASER:
                this.eraserWidth = drawerWidth;
                break;

            case TEXT:
                this.fontSize = drawerWidth * 5;
                break;
        }
        this.setBlur(this.drawerBlur);
    }


    /**
     * This method is getter for amount of blur,
     * for current drawer mod.
     *
     * @return
     */
    public float getBlur() {
        return this.drawerBlur;
    }

    /**
     * This method is setter for amount of blur.
     * The 1st argument is greater than or equal to 0.0.
     *
     * @param blur must be between 0 and 150.
     */
    public void setBlur(float blur) {
        // negativ
        if (blur < 0)
            blur = 0f;
        else if (255 < blur)
            blur = 255 ;
//        // in range
//        else if ((0 < blur) && (blur <= 255))
//                this.drawerBlur = blur + drawerWidth;
//        // out of range
//        else if (255 < blur) {
//            this.drawerBlur = (255 + drawerWidth);
//            Log.w("BLUR", "out !!!!!!!!!!");
//        }

        this.drawerBlur = blur * drawerWidth / 100;

        Log.w("BLUR", "In = " + blur + " Out = " + drawerBlur + " Width = " + drawerWidth);

        switch (this.mode) {

            case DRAW:
                this.strokeBlur = drawerBlur;
                break;

            case ERASER:
                this.eraserBlur = drawerBlur;
                break;

            case TEXT:
                this.textBlur = drawerBlur;
                break;
        }
    }


    /**
     * This method is getter for last painting alpha,
     * for current drawer mod.
     *
     * @return
     */
    public int getOpacity() {
        return drawerOpacity;
    }

    /**
     * This method is setter for last painting alpha,
     * for current drawer mod.
     *
     * @param opacity must be between 0 and 255.
     */
    public void setOpacity(int opacity) {
        if ((opacity >= 0) && (opacity <= 255)) {
            this.drawerOpacity = opacity;
        } else {
            this.drawerOpacity = 255;
        }

        switch (this.mode) {

            case DRAW:
                this.strokeOpacity = drawerOpacity;
                break;

            case ERASER:
                this.eraserOpacity = drawerOpacity;
                break;

            case TEXT:
                this.textOpacity = drawerOpacity;
                break;
        }
}


    /**
     * This method is getter for line cap.
     *
     * @return
     */
    public Paint.Cap getLineCap() {
        return this.lineCap;
    }

    /**
     * This method is setter for line cap.
     *
     * @param cap
     */
    public void setLineCap(Paint.Cap cap) {
        this.lineCap = cap;
    }

    /**
     * This method is getter for font size,
     *
     * @return
     */
    public float getFontSize() {
        return this.fontSize;
    }

    /**
     * This method is setter for font size.
     * The 1st argument is greater than or equal to 0.0.
     *
     * @param size
     */
    public void setFontSize(float size) {
        if (size >= 0F) {
            this.fontSize = size;
        } else {
            this.fontSize = 32F;
        }
    }

    /**
     * This method is getter for font-family.
     *
     * @return
     */
    public Typeface getFontFamily() {
        return this.fontFamily;
    }

    /**
     * This method is setter for font-family.
     *
     * @param face False id freeze.
     */
    public void setFontFamily(Typeface face) {
        this.fontFamily = face;
    }

    /**
     * Is canvas in redrawing mod, or is frozen.
     *
     * @return
     */
    public boolean isRedraw() {
        return redraw;
    }

    /**
     * Set freezimg for canvas.
     *
     * @param redraw False id freeze.
     */
    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    /**
     * This method gets current canvas as bitmap.
     *
     * @return This is returned as bitmap.
     */
    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(false);
        this.setDrawingCacheEnabled(true);

        return Bitmap.createBitmap(this.getDrawingCache());
    }

    /**
     * This method gets current canvas as scaled bitmap.
     *
     * @return This is returned as scaled bitmap.
     */
    public Bitmap getScaleBitmap(int w, int h) {
        this.setDrawingCacheEnabled(false);
        this.setDrawingCacheEnabled(true);

        return Bitmap.createScaledBitmap(this.getDrawingCache(), w, h, true);
    }

    /**
     * This method draws the designated bitmap to canvas.
     *
     * @param bitmap
     */
    public void drawBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.invalidate();
    }

    /**
     * This method draws the designated byte array of bitmap to canvas.
     *
     * @param byteArray This is returned as byte array of bitmap.
     */
    public void drawBitmap(byte[] byteArray) {
        this.drawBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }

    /**
     * This static method gets the designated bitmap as byte array.
     *
     * @param bitmap
     * @param format
     * @param quality
     * @return This is returned as byte array of bitmap.
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * This method gets the bitmap as byte array.
     *
     * @param format
     * @param quality
     * @return This is returned as byte array of bitmap.
     */
    public byte[] getBitmapAsByteArray(Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.getBitmap().compress(format, quality, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * This method gets the bitmap as byte array.
     * Bitmap format is PNG, and quality is 100.
     *
     * @return This is returned as byte array of bitmap.
     */
    public byte[] getBitmapAsByteArray() {
        return this.getBitmapAsByteArray(Bitmap.CompressFormat.PNG, 100);
    }

}