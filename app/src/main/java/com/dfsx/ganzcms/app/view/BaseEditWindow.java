package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.dfsx.ganzcms.app.business.AbsRunnable;

/**
 * 编辑弹窗使用DialogFragment可以有效防止EditText的bug。比如EditText的粘贴功能，和EditText的焦点获取
 *
 * @author liuwb
 */
public abstract class BaseEditWindow extends DialogFragment {

    protected boolean isCreated;
    protected Context context;
    private DialogInterface.OnDismissListener onDismissListener;

    protected InputMethodManager imm;

    protected Handler handler = new Handler();

    /**
     * 设置为在创建成功之后运行
     */
    protected Runnable runAfterCreated;

    private Object objectTag;
    private View anchor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isCreated = false;
        Window window = getDialog().getWindow();
        View view = inflater.inflate(getWindowLayoutRes(), ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(getWindowBackGroundDrawable());//注意此处
        window.setLayout(getLayoutWidth(), getLayoutHeight());//这2行,和上面的一样,注意顺序就行;
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setDimAmount(getWindowDimAmount());
        window.setGravity(getGravity());
        context = getActivity();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onWindowCreateView(view);
        isCreated = true;
        if (runAfterCreated != null) {
            handler.post(runAfterCreated);
            runAfterCreated = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void showInput() {
        showInput(getEditText());
    }

    public void showInput(EditText editText) {
        handler.postDelayed(new AbsRunnable<EditText>(editText) {
            @Override
            public void runData(EditText data) {
                imm.showSoftInput(data, InputMethodManager.SHOW_FORCED);
            }
        }, 50);
    }

    public void hideInput() {
        hideInput(anchor);
    }

    public void hideInput(View view) {
        handler.postDelayed(new AbsRunnable<View>(view) {
            @Override
            public void runData(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }, 50);

    }

    /**
     * 最好调用此方法显示，因为这个类做了输入法显示和隐藏功能，
     * 不使用此扩展，隐藏输入法可能失效。需要在外部实现输入法隐藏功能
     *
     * @param fm
     * @param tag
     * @param anchorView 调用窗口的控件，可以理解为弹窗的锚点， 类似于PopupWindow的anchor
     */
    public void show(FragmentManager fm, String tag, View anchorView) {
        super.show(fm, tag);
        this.anchor = anchorView;
    }

    /**
     * 获取整个window的布局。默认为全屏
     *
     * @return
     */
    public abstract int getWindowLayoutRes();

    /**
     * 获取当前布局里面的EditText
     *
     * @return
     */
    public abstract EditText getEditText();

    /**
     * 当View创建的时候调用，可以初始化一些逻辑
     *
     * @param v
     */
    public abstract void onWindowCreateView(View v);

    public Drawable getWindowBackGroundDrawable() {
        return new ColorDrawable(Color.TRANSPARENT);
    }

    /**
     * 控制整个布局的宽度， 默认最大宽度
     *
     * @return
     */
    public int getLayoutWidth() {
        return -1;
    }

    /**
     * 控制整个布局的显示高度，默认为自适应最小
     *
     * @return
     */
    public int getLayoutHeight() {
        return -2;
    }

    /**
     * from 0 for no dim to 1 for full dim.
     *
     * @return
     */
    public float getWindowDimAmount() {
        return 0f;
    }

    /**
     * 控制整个布局的显示位置
     *
     * @return
     */
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public Object getObjectTag() {
        return objectTag;
    }

    public void setObjectTag(Object objectTag) {
        this.objectTag = objectTag;
    }
}
