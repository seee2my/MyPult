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
        iconIr.setText("🖱");
        iconIr.setTextSize(18);
        iconIr.setTextColor(hasIr ? Color.parseColor("#4CAF50") : Color.parseColor("#FF5252"));
        iconIr.setOnClickListener(v -> Toast.makeText(this,
                hasIr ? "ІЧ-порт готовий до роботи" : "ІЧ-передавач не знайдено", Toast.LENGTH_SHORT).show());
        bottomBar.addView(iconIr);

        bottomBar.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));

        TextView iconNum = new TextView(this);
        iconNum.setText("⠿");
        iconNum.setTextSize(22);
        iconNum.setTextColor(TEXT_LIGHT);
        iconNum.setOnClickListener(v -> showNumpadDialog());
        bottomBar.addView(iconNum);

        bottomBar.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));

        TextView iconSet = new TextView(this);
