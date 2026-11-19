package com.example.lgremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int CARRIER_HZ = 38000;

    private static final int POWER     = 0x20DF10EF;
    private static final int INPUT     = 0x20DFD02F;
    private static final int TV_RAD    = 0x20DF0FF0;
    private static final int MUTE      = 0x20DF906F;
    private static final int VOL_UP    = 0x20DF40BF;
    private static final int VOL_DN    = 0x20DFC03F;
    private static final int CH_UP     = 0x20DF00FF;
    private static final int CH_DN     = 0x20DF807F;
    private static final int MENU      = 0x20DFC23D;
    private static final int QMENU     = 0x20DFA25D;
    private static final int SLEEP     = 0x20DF708F;
    private static final int UP        = 0x20DF02FD;
    private static final int DOWN      = 0x20DF827D;
    private static final int LEFT      = 0x20DFE01F;
    private static final int RIGHT     = 0x20DF609F;
    private static final int OK        = 0x20DF22DD;
    private static final int BACK      = 0x20DF14EB;
    private static final int EXIT      = 0x20DFDA25;
    private static final int[] DIGITS  = {
            0x20DF08F7, 0x20DF8877, 0x20DF48B7, 0x20DFC837, 0x20DF28D7,
            0x20DFA857, 0x20DF6897, 0x20DFE817, 0x20DF18E7, 0x20DF9867
    };

    private static final int BG_DARK      = Color.parseColor("#0B1626");
    private static final int BTN_DARK     = Color.parseColor("#112138");
    private static final int BTN_STROKE   = Color.parseColor("#3B6496");
    private static final int BTN_BLUE     = Color.parseColor("#3278C6");
    private static final int BTN_RED      = Color.parseColor("#D92B2B");
    private static final int TEXT_LIGHT   = Color.parseColor("#E3EEFA");

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ConsumerIrManager ir;
    private boolean hasIr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ir = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        hasIr = ir != null && ir.hasIrEmitter();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG_DARK);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(20), dp(14), dp(20), dp(14));

        TextView btnPower = createPillButton("⏻", POWER, false, BTN_RED, Color.TRANSPARENT, 20);
        LinearLayout.LayoutParams lpPower = new LinearLayout.LayoutParams(dp(74), dp(38));
        lpPower.bottomMargin = dp(12);
        root.addView(btnPower, lpPower);

        LinearLayout rowTop = new LinearLayout(this);
        rowTop.setOrientation(LinearLayout.HORIZONTAL);
        rowTop.setGravity(Gravity.CENTER);
        rowTop.addView(createPillButton("TV", TV_RAD, false, BTN_DARK, BTN_STROKE, 13), pillParams(72, 36, 14));
        rowTop.addView(createPillButton("⌂", QMENU, false, BTN_DARK, BTN_STROKE, 18), pillParams(72, 36, 14));
        rowTop.addView(createPillButton("◫", INPUT, false, BTN_DARK, BTN_STROKE, 16), pillParams(72, 36, 0));
        LinearLayout.LayoutParams lpRowTop = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpRowTop.bottomMargin = dp(8);
        root.addView(rowTop, lpRowTop);

        LinearLayout rowInputApps = new LinearLayout(this);
        rowInputApps.setOrientation(LinearLayout.HORIZONTAL);
        rowInputApps.setGravity(Gravity.CENTER_VERTICAL);
        rowInputApps.addView(createPillButton("INPUT", INPUT, false, BTN_DARK, BTN_STROKE, 12),
                new LinearLayout.LayoutParams(dp(74), dp(36)));
        rowInputApps.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));
        rowInputApps.addView(createPillButton("APPS", QMENU, false, BTN_DARK, BTN_STROKE, 12),
                new LinearLayout.LayoutParams(dp(74), dp(36)));
        root.addView(rowInputApps, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        FrameLayout dpadContainer = new FrameLayout(this);
        int dpadSize = dp(210);
        LinearLayout.LayoutParams lpDpad = new LinearLayout.LayoutParams(dpadSize, dpadSize);
        lpDpad.topMargin = dp(-6);
        lpDpad.bottomMargin = dp(-6);

        View dpadCircle = new View(this);
        GradientDrawable dpadBg = new GradientDrawable();
        dpadBg.setShape(GradientDrawable.OVAL);
        dpadBg.setColor(BTN_BLUE);
        dpadCircle.setBackground(dpadBg);
        dpadContainer.addView(dpadCircle, new FrameLayout.LayoutParams(dpadSize, dpadSize));

        dpadContainer.addView(createDpadArrow("△", UP, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10, 0, 0));
        dpadContainer.addView(createDpadArrow("▽", DOWN, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 10));
        dpadContainer.addView(createDpadArrow("◁", LEFT, Gravity.START | Gravity.CENTER_VERTICAL, 14, 0, 0, 0));
        dpadContainer.addView(createDpadArrow("▷", RIGHT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 14, 0));

        int okSize = dp(76);
        TextView btnOk = new TextView(this);
        btnOk.setText("OK");
        btnOk.setTextColor(TEXT_LIGHT);
        btnOk.setTextSize(17);
        btnOk.setTypeface(null, Typeface.BOLD);
        btnOk.setGravity(Gravity.CENTER);
        GradientDrawable okBg = new GradientDrawable();
        okBg.setShape(GradientDrawable.OVAL);
        okBg.setColor(BG_DARK);
        btnOk.setBackground(okBg);
        attachTouch(btnOk, OK, false);
        dpadContainer.addView(btnOk, new FrameLayout.LayoutParams(okSize, okSize, Gravity.CENTER));

        root.addView(dpadContainer, lpDpad);

        LinearLayout rowBackExit = new LinearLayout(this);
        rowBackExit.setOrientation(LinearLayout.HORIZONTAL);
        rowBackExit.setGravity(Gravity.CENTER_VERTICAL);
        rowBackExit.addView(createPillButton("←", BACK, false, BTN_DARK, BTN_STROKE, 18),
                new LinearLayout.LayoutParams(dp(74), dp(36)));
        rowBackExit.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));
        rowBackExit.addView(createPillButton("EXIT", EXIT, false, BTN_DARK, BTN_STROKE, 12),
                new LinearLayout.LayoutParams(dp(74), dp(36)));
        LinearLayout.LayoutParams lpBackExit = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBackExit.bottomMargin = dp(14);
        root.addView(rowBackExit, lpBackExit);

        LinearLayout midBlock = new LinearLayout(this);
        midBlock.setOrientation(LinearLayout.HORIZONTAL);
        midBlock.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpMid = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        lpMid.bottomMargin = dp(10);

        midBlock.addView(createRocker("VOL", VOL_UP, VOL_DN), new LinearLayout.LayoutParams(dp(64), dp(176)));

        LinearLayout centerCol = new LinearLayout(this);
        centerCol.setOrientation(LinearLayout.VERTICAL);
        centerCol.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpCenterCol = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        TextView btnMenu = createPillButton("MENU", MENU, false, BTN_DARK, BTN_STROKE, 12);
        LinearLayout.LayoutParams lpMenu = new LinearLayout.LayoutParams(dp(74), dp(36));
        lpMenu.bottomMargin = dp(16);
        centerCol.addView(btnMenu, lpMenu);

        TextView btnMute = createCircleButton("🔇", MUTE, BTN_DARK, BTN_STROKE, 16);
        LinearLayout.LayoutParams lpMute = new LinearLayout.LayoutParams(dp(52), dp(52));
        lpMute.bottomMargin = dp(16);
        centerCol.addView(btnMute, lpMute);

        TextView btnSleep = createPillButton("SLEEP", SLEEP, false, BTN_DARK, BTN_STROKE, 12);
        centerCol.addView(btnSleep, new LinearLayout.LayoutParams(dp(74), dp(36)));

        midBlock.addView(centerCol, lpCenterCol);
        midBlock.addView(createRocker("CH", CH_UP, CH_DN), new LinearLayout.LayoutParams(dp(64), dp(176)));
        root.addView(midBlock, lpMid);

        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.CENTER_VERTICAL);
        bottomBar.setPadding(dp(24), dp(4), dp(24), dp(4));

        TextView iconIr = new TextView(this);
                iconSet.setText("⚙");
        iconSet.setTextSize(20);
        iconSet.setTextColor(TEXT_LIGHT);
        attachTouch(iconSet, MENU, false);
        bottomBar.addView(iconSet);

        root.addView(bottomBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void showNumpadDialog() {
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        grid.setBackgroundColor(BG_DARK);
        grid.setPadding(dp(16), dp(16), dp(16), dp(16));

        int[] order = {1, 2, 3, 4, 5, 6, 7, 8, 9, -1, 0, -2};
        for (int n : order) {
            if (n == -1) {
                grid.addView(new View(this), new ViewGroup.LayoutParams(dp(76), dp(46)));
            } else if (n == -2) {
                TextView closeBtn = createPillButton("✕", EXIT, false, BTN_RED, Color.TRANSPARENT, 15);
                GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
                glp.width = dp(76);
                glp.height = dp(46);
                glp.setMargins(dp(6), dp(6), dp(6), dp(6));
                grid.addView(closeBtn, glp);
            } else {
                TextView btn = createPillButton(String.valueOf(n), DIGITS[n], false, BTN_DARK, BTN_STROKE, 16);
                GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
                glp.width = dp(76);
                glp.height = dp(46);
                glp.setMargins(dp(6), dp(6), dp(6), dp(6));
                grid.addView(btn, glp);
            }
        }
        new AlertDialog.Builder(this).setView(grid).show();
    }

    static int[] buildNec(int code) {
        int[] p = new int[2 + 32 * 2 + 1];
        int i = 0;
        p[i++] = 9000;
        p[i++] = 4500;
        for (int b = 31; b >= 0; b--) {
            p[i++] = 560;
            p[i++] = (((code >>> b) & 1) == 1) ? 1690 : 560;
        }
        p[i] = 560;
        return p;
    }

    private void send(int code) {
        if (!hasIr) {
            Toast.makeText(this, "ІК-передавача немає на цьому пристрої", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ir.transmit(CARRIER_HZ, buildNec(code));
            getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        } catch (Exception e) {
            Toast.makeText(this, "Помилка передачі ІК", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createPillButton(String label, int code, boolean repeat, int fillColor, int strokeColor, int textSp) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(TEXT_LIGHT);
        tv.setTextSize(textSp);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, Typeface.BOLD);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dp(50));
        if (strokeColor != Color.TRANSPARENT) {
            gd.setStroke(dp(1), strokeColor);
        }
        tv.setBackground(gd);
        attachTouch(tv, code, repeat);
        return tv;
    }

    private TextView createCircleButton(String label, int code, int fillColor, int strokeColor, int textSp) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(TEXT_LIGHT);
        tv.setTextSize(textSp);
        tv.setGravity(Gravity.CENTER);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fillColor);
        gd.setStroke(dp(1), strokeColor);
        tv.setBackground(gd);
        attachTouch(tv, code, false);
        return tv;
    }

    private TextView createDpadArrow(String symbol, int code, int gravity, int l, int t, int r, int b) {
        TextView tv = new TextView(this);
        tv.setText(symbol);
        tv.setTextColor(TEXT_LIGHT);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        attachTouch(tv, code, true);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(56), dp(56), gravity);
        lp.setMargins(dp(l), dp(t), dp(r), dp(b));
        return tv;
    }

    private LinearLayout createRocker(String title, int codeUp, int codeDown) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(BTN_BLUE);
        gd.setCornerRadius(dp(32));
        box.setBackground(gd);

        TextView plus = new TextView(this);
        plus.setText("+");
        plus.setTextColor(TEXT_LIGHT);
        plus.setTextSize(22);
        plus.setGravity(Gravity.CENTER);
        attachTouch(plus, codeUp, true);
        box.addView(plus, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextColor(TEXT_LIGHT);
        label.setTextSize(14);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.CENTER);
        box.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView minus = new TextView(this);
        minus.setText("−");
        minus.setTextColor(TEXT_LIGHT);
        minus.setTextSize(22);
        minus.setGravity(Gravity.CENTER);
        attachTouch(minus, codeDown, true);
        box.addView(minus, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        return box;
    }

    private void attachTouch(View v, final int code, final boolean repeat) {
        v.setOnTouchListener(new View.OnTouchListener() {
            private Runnable repeater;

            @Override
            public boolean onTouch(View view, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.55f);
                        send(code);
                        if (repeat) {
                            repeater = new Runnable() {
                                @Override
                                public void run() {
                                    send(code);
                                    handler.postDelayed(this, 150);
                                }
                            };
                            handler.postDelayed(repeater, 450);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setAlpha(1f);
                        if (repeater != null) {
                            handler.removeCallbacks(repeater);
                            repeater = null;
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private LinearLayout.LayoutParams pillParams(int wDp, int hDp, int rightMarginDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(wDp), dp(hDp));
        lp.rightMargin = dp(rightMarginDp);
        return lp;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
