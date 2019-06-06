package com.dfsx.searchlibaray.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.searchlibaray.R;

import java.util.ArrayList;

/**
 * 自定义可扩展布局
 *
 * @author liuwenbo
 */
public class CustomLabelLayout extends LinearLayout {
    //嗷嗷多的参数，如果调细节距离什么的，调这里
    public static final int TITLE_SIZE_BY_SP = 18;
    public static final int BODY_SIZE_BY_SP = 11;
    public static final int TITLE_COLOR = Color.BLACK;
    public int BODY_COLOR = Color.BLACK;
    public int BODY_COLOR_SELECTED = Color.WHITE;
    public int BODY_BACKGROUND_RESUORCE = R.drawable.public_custom_label_layout_body_bg_unselected;
    public int BODY_BACKGROUND_SELECTED_RESUORCE = R.drawable.public_custom_label_layout_body_bg_selected;
    public static final int BODY_BACKGROUND_ADD_FLAG_RESUORCE = R.drawable.public_custom_label_layout_body_add_flag;
    public static final int PADDING_BY_DIP = 0;
    public static final int BODY_HORIZONTAL_PADDING_BY_DIP = 8;
    public static final int SINGLE_LINE_HEIGHT_BY_DIP = 30;
    public static final int SINGLE_LINE_HEIGHT_PADDING_VERTICAL_BY_DIP = 4;

    //******新加的一些参数*******
    //最小一行里的个数，不够的话，默认用空白的补
    public static final int MIN_CONTENT_COUNT = 3;
    public static final int MAX_CONTENT_COUNT = 5;
    public static final int AUTO_FILL_WEIGHT = 2;
    //预留0%的冗余空间
    public static final float SPACE_RETAINED_PERCENT = 0.0F;
    //如果剩余空间大于30%，继续追加标签
    public static final float SPACE_LEFT_ENOUGH_APPENDING_PERCENT = 0.3F;

    //控件宽度，用于计算补充空白的宽度
    private int widgetLength = 0;
    //是否重新计算单元宽度，true的话不支持.9，false支持.9
    private boolean isResizeCellWidth = true;
    private int extWidthDp = 0;
    //加号控件的宽度
    private int addFlagWidgetWidth = 0;
    private Runnable runAfterOnlayout;
    private Runnable runAfterOnlayoutForPreSelection;
    public static final int MAX_SELECT_COUNT_DEFAULT = 10;
    private int maxSelectCount = MAX_SELECT_COUNT_DEFAULT;
    private Paint paint;

    private boolean isAddFlagNeedShown = true;
    //******新加的一些参数*******

    //权重自动调平衡，不开的话只按字数来走(关了后果自负)
    public static final boolean SWITCHER_AUTO_BALANCE = true;
    //最大最小之间的允许的最大倍数差
    public static final float MAX_RATIO_BETWEEN_LARGEST_AND_SMALLEST = 1.5f;

    private boolean isClickBody = true;

    private Context context;

    private String title;

    private OnClickListener listener;
    private OuterBodyClickListener outerBodyListener;

    private ArrayList<BodyEntity> list = new ArrayList<BodyEntity>();

    public CustomLabelLayout(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context, attrs, defStyle);
    }

    public CustomLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs, 0);
    }

    public CustomLabelLayout(Context context) {
        super(context);
        this.context = context;
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setOrientation(VERTICAL);

        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        int paddingPx = PixelUtil.dp2px(context, PADDING_BY_DIP);
        setPadding(paddingPx, paddingPx / 2, paddingPx, paddingPx);
    }

    /**
     * 添加标题项
     *
     * @param title
     */
    public void addTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }

        if (!TextUtils.isEmpty(this.title)) {
            //原来有标题了，移除
            removeViewAt(0);
        }
        this.title = title;

        TextView titleView = new TextView(context);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, SINGLE_LINE_HEIGHT_BY_DIP));
        titleView.setLayoutParams(params);
        titleView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        titleView.setText(title);
        titleView.setTextSize(TITLE_SIZE_BY_SP);
        titleView.setTextColor(TITLE_COLOR);

        addView(titleView, 0);
    }

    public void removeTitle() {
        if (TextUtils.isEmpty(title)) {
            return;
        }

        title = "";
        removeViewAt(0);
    }

    /**
     * 更换背景主题
     *
     * @param resourceId
     */
    public void changeThemeForBackground(int resourceId) {
        setBackgroundResource(resourceId);
    }

    public void changeThemeForBackgroundColor(int colorInt) {
        setBackgroundColor(colorInt);
    }

    /**
     * 更换文字主题 bg resid为0表示不修改
     *
     * @param colorIntForUnselected
     * @param colorIntForSelected
     */
    public void changeThemeForTextColor(int colorIntForSelected, int colorIntForUnselected,
                                        int bodyBgResForSelected, int bodyBgResForUnselected) {
        BODY_COLOR = colorIntForUnselected;
        BODY_COLOR_SELECTED = colorIntForSelected;
        BODY_BACKGROUND_RESUORCE = bodyBgResForUnselected <= 0 ? BODY_BACKGROUND_RESUORCE : bodyBgResForUnselected;
        BODY_BACKGROUND_SELECTED_RESUORCE = bodyBgResForSelected <= 0 ? BODY_BACKGROUND_SELECTED_RESUORCE : bodyBgResForSelected;

        //		refreshView();
        rebuildAll();
    }


    /**
     * 监听body点击事件，内部点击逻辑会正常执行，可设置点击/非点击为相同资源ID来实现屏蔽选择状态切换的功能。
     * 传null，则取消外部监听
     * 和添加按钮的监听区分开
     *
     * @param outerBodyListener
     */
    public void setOuterBodyClickListener(OuterBodyClickListener outerBodyListener) {
        this.outerBodyListener = outerBodyListener;
    }

    public void setAddFlagOnClickListener(OnClickListener lis) {
        listener = lis;
    }

    /**
     * 设置加号是不是显示
     *
     * @param isNeedShown
     */
    public void setAddFlagNeedShown(boolean isNeedShown) {
        isAddFlagNeedShown = isNeedShown;

        if (widgetLength > 0) {
            //这里需要重新构建控件了
            rebuildAll();
        }
    }

    private void rebuildAll() {
        if (widgetLength > 0) {
            //这里需要重新构建控件了
            clearBodies();
            ArrayList<String> bodyList = bodyArrToStringArr();

            //再来个加号
            if (isAddFlagNeedShown) {
                bodyList.add("加号");
            }

            addAllBody(bodyList.toArray(new String[0]));

            //去掉不活动的body实体
            destroyBodies();
        }
    }

    public void deleteBody(BodyEntity body) {
        if (body == null) {
            return;
        }
        //这里需要重新构建控件了
        clearBodies();
        ArrayList<String> bodyList = bodyArrToStringArr(body.content);

        //再来个加号
        if (isAddFlagNeedShown) {
            bodyList.add("加号");
        }

        addAllBody(bodyList.toArray(new String[0]));

        //去掉不活动的body实体
        destroyBodies();
    }

    /**
     * 单独加一个button
     *
     * @param content
     */
    public boolean appendBody(String content) {
        if (checkHasSameContentBody(content)) {
            return false;
        }
        //所有值钱的body都置成不活动的
        clearBodies();
        ArrayList<String> bodyList = bodyArrToStringArr();
        bodyList.add(content);
        //再来个加号
        if (isAddFlagNeedShown) {
            bodyList.add("加号");
        }

        addAllBody(bodyList.toArray(new String[0]));

        //去掉不活动的body实体
        destroyBodies();
        return true;
    }

    private void clearBodies() {
        removeAllViews();

        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            entity.isAlive = false;
        }
    }

    public void destroy() {
        clearBodies();
        destroyBodies();
        list.clear();
        runAfterOnlayout = null;
    }

    private void destroyBodies() {
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (!entity.isAlive) {
                list.remove(index);
                index--;
            }
        }
    }

    public void addAllBody(final String... contents) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addAllBodyInternal(contents);
            }
        };

        runAfterOnlayout = runnable;
        if (widgetLength > 0) {
            runAfterOnlayout.run();
            runAfterOnlayout = null;
        }
    }

    /**
     * 设置选择的所有实体
     *
     * @param contents
     */
    public void setSelectedBodies(final String... contents) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setSelectedBodiesInternal(contents);
            }
        };

        runAfterOnlayoutForPreSelection = runnable;
        if (widgetLength > 0 && runAfterOnlayout == null) {
            runAfterOnlayoutForPreSelection.run();
            runAfterOnlayoutForPreSelection = null;
        }
    }

    private void setSelectedBodiesInternal(final String... contents) {
        if (contents == null) {
            return;
        }

        clearSelection();
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            for (int indexContents = 0; indexContents < contents.length; indexContents++) {
                if (entity.content.equals(contents[indexContents])) {
                    changeViewState(entity, true);
                    break;
                }
            }
        }
    }

    /**
     * 添加所有数据
     *
     * @param contents
     */
    private void addAllBodyInternal(String... contents) {
        int realWidth = (int) (widgetLength * (1 - SPACE_RETAINED_PERCENT));
        if (realWidth <= 0) {
            return;
        }
        int curCountInLine = 0;
        int curLength = 0;
        ArrayList<String> curTextArr = new ArrayList<String>();
        for (int index = 0; index < contents.length; index++) {
            boolean isNeedFillEmpty = index == contents.length - 1;
            String curText = contents[index];
            int paddingWidth = curCountInLine == 0 ? 0 : PixelUtil.dp2px(context, BODY_HORIZONTAL_PADDING_BY_DIP);
            curLength += calcWidgetWidth(curText) + paddingWidth;
            if (curLength > realWidth) {
                //触发add单行
                if (curCountInLine > 0) {
                    index--;
                    curCountInLine = 0;
                    curLength = 0;
                    //这里因为要回退，所以isNeedFillEmpty强制为false
                    addBody(false, false, curTextArr.toArray(new String[0]));
                    curTextArr.clear();
                } else {
                    //如果只有1个，就从了吧
                    addBody(false, isNeedFillEmpty, curText);
                    curLength = 0;
                }
                continue;
            }

            curTextArr.add(contents[index]);
            curCountInLine++;

            //如果是最后一个，或者到达单行最大值
            if (index == contents.length - 1 || curCountInLine == MAX_CONTENT_COUNT) {
                addBody(false, isNeedFillEmpty, curTextArr.toArray(new String[0]));
                curCountInLine = 0;
                curLength = 0;
                curTextArr.clear();
            }
        }
    }

    /**
     * 添加一整行数据
     *
     * @param isSameWidth 是否等宽，不等宽的话，将按照LinearLayout的权重分配
     * @param contents
     */
    private void addBody(boolean isSameWidth, boolean isNeedFillEmpty, String... contents) {
        if (contents == null || contents.length == 0) {
            return;
        }

        LinearLayout singleLine = new LinearLayout(context);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, SINGLE_LINE_HEIGHT_BY_DIP));
        singleLine.setLayoutParams(params);
        singleLine.setOrientation(HORIZONTAL);
        singleLine.setGravity(Gravity.CENTER_VERTICAL);

        int lastOneIndex = 0;

        if (isNeedFillEmpty) {
            lastOneIndex = contents.length - 1;
        }

        for (int index = 0; index < contents.length; index++) {
            TextView bodyView = new TextView(context);
            LayoutParams nodeParams = new LayoutParams(calcWidgetWidth(contents[index]),
                    PixelUtil.dp2px(context, SINGLE_LINE_HEIGHT_BY_DIP - SINGLE_LINE_HEIGHT_PADDING_VERTICAL_BY_DIP * 2));
            bodyView.setGravity(Gravity.CENTER);
            bodyView.setText(contents[index]);
            bodyView.setTextSize(BODY_SIZE_BY_SP);
            bodyView.setTextColor(BODY_COLOR);
            bodyView.setBackgroundResource(BODY_BACKGROUND_RESUORCE);

            if (TextUtils.isEmpty(contents[index])) {
                bodyView.setVisibility(View.GONE);
            }

            if (index != 0) {
                nodeParams.setMargins(PixelUtil.dp2px(context, BODY_HORIZONTAL_PADDING_BY_DIP), 0, 0, 0);
            }

            bodyView.setLayoutParams(nodeParams);
            singleLine.addView(bodyView);

            //添加到内容管理器里
            BodyEntity entity = createOrUpdateBodyEntity(contents[index], false, bodyView);

            if (index == lastOneIndex && isNeedFillEmpty && isAddFlagNeedShown) {
                entity.isAddFlag = true;

                //这里给加号按钮特殊处理一下
                bodyView.setVisibility(View.VISIBLE);
                bodyView.setText("");
                bodyView.setBackgroundResource(R.drawable.public_custom_label_layout_body_add_flag);

                nodeParams.weight = 0;
                nodeParams.width = getStandardAddFlagButtonWidth();
                bodyView.setLayoutParams(nodeParams);
            }
        }

        addView(singleLine);
    }

    private BodyEntity createOrUpdateBodyEntity(String content, boolean isEditable, TextView view) {
        if (!TextUtils.isEmpty(content)) {
            for (int index = 0; index < list.size(); index++) {
                BodyEntity entity = list.get(index);
                if (!TextUtils.isEmpty(entity.content) && entity.content.equals(content)) {
                    //激活并更新节点
                    entity.isAlive = true;
                    entity.content = content;
                    entity.isEditable = isEditable;
                    entity.textView = view;
                    entity.setListener();
                    changeViewState(entity, entity.isSelected);
                    return entity;
                }
            }
        }

        //如果没有 就NEW一个
        BodyEntity entity = new BodyEntity(content, isEditable, view);
        list.add(entity);
        changeViewState(entity, entity.isSelected);
        return entity;
    }

    /**
     * 看看有没有重复标签
     *
     * @return
     */
    private boolean checkHasSameContentBody(String content) {
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (!TextUtils.isEmpty(entity.content) && entity.content.equals(content)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getAllBody() {
        if (list != null && !list.isEmpty()) {
            ArrayList<String> data = new ArrayList<>();
            for (BodyEntity entity : list) {
                if (entity != null) {
                    data.add(entity.content);
                }
            }
            return data;
        }
        return null;
    }

    /**
     * 添加编辑框
     *
     * @param hint
     */
    public void addEditableBody(String hint) {
        LinearLayout singleLine = new LinearLayout(context);
        singleLine.setFocusable(true);
        singleLine.setFocusableInTouchMode(true);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, SINGLE_LINE_HEIGHT_BY_DIP));
        singleLine.setLayoutParams(params);
        singleLine.setOrientation(HORIZONTAL);
        singleLine.setGravity(Gravity.CENTER_VERTICAL);

        EditText bodyView = new EditText(context);
        LayoutParams nodeParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, SINGLE_LINE_HEIGHT_BY_DIP - SINGLE_LINE_HEIGHT_PADDING_VERTICAL_BY_DIP * 2));
        bodyView.setGravity(Gravity.CENTER);
        bodyView.setText(hint);
        bodyView.setTextSize(BODY_SIZE_BY_SP);
        bodyView.setTextColor(BODY_COLOR);
        bodyView.setBackgroundResource(BODY_BACKGROUND_RESUORCE);
        bodyView.setSingleLine();
        bodyView.setLayoutParams(nodeParams);
        singleLine.addView(bodyView);

        //添加到内容管理器里
        BodyEntity entity = new BodyEntity(hint, true, bodyView);
        list.add(entity);

        addView(singleLine);
    }

    /**
     * 获取标准的加号按钮宽度，以一行5个按钮，每个按钮俩字来计算，为了美观
     *
     * @return
     */
    private int getStandardAddFlagButtonWidth() {
        if (addFlagWidgetWidth != 0) {
            return addFlagWidgetWidth;
        }
        int spacePx = PixelUtil.dp2px(context, (MAX_CONTENT_COUNT - 1) * BODY_HORIZONTAL_PADDING_BY_DIP);
        int addFlagWidth = (widgetLength - spacePx) / MAX_CONTENT_COUNT;
        addFlagWidgetWidth = addFlagWidth;
        return addFlagWidth;
    }

    /**
     * 计算控件宽度
     *
     * @return
     */
    private int calcWidgetWidth(String str) {
        if (paint == null) {
            paint = new Paint();
            paint.setTextSize(PixelUtil.sp2px(context, BODY_SIZE_BY_SP));
        }

        float pixel = paint.measureText(str) + PixelUtil.dp2px(context, 8)
                //是否支持.9自动扩展
                + (isResizeCellWidth ? 0 : PixelUtil.dp2px(context, extWidthDp));
        if (pixel < getStandardAddFlagButtonWidth()) {
            return getStandardAddFlagButtonWidth();
        } else {
            return (int) (pixel + 4);
        }
    }

    /**
     * 计算权重
     *
     * @param resultArray
     */
    private void calcWeight(float[] resultArray, String[] contents, boolean isSameWidth, boolean isNeedFillEmpty) {
        if (isSameWidth) {
            for (int index = 0; index < resultArray.length; index++) {
                resultArray[index] = 1;
            }
            return;
        }

        int maxLength = 0;
        for (int index = 0; index < contents.length; index++) {
            if (contents[index].length() > maxLength) {
                maxLength = contents[index].length();
            }
        }

        for (int index = 0; index < contents.length; index++) {
            resultArray[index] = contents[index].length();

            if (isNeedFillEmpty) {
                resultArray[index] = resultArray[index] == 0 ? AUTO_FILL_WEIGHT : resultArray[index];
            }
            if (SWITCHER_AUTO_BALANCE) {
                float min = maxLength / MAX_RATIO_BETWEEN_LARGEST_AND_SMALLEST;
                if (resultArray[index] < min) {
                    //自动调平衡
                    float waitToAdd = min - resultArray[index];
                    waitToAdd = waitToAdd / MAX_RATIO_BETWEEN_LARGEST_AND_SMALLEST;
                    resultArray[index] += waitToAdd;
                }
            }
        }
    }

    /**
     * 获取当前选中的
     *
     * @return
     */
    public ArrayList<String> getCurrentSelection() {
        ArrayList<String> retList = new ArrayList<String>();
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (entity.isSelected) {
                retList.add(entity.content);
            }
        }

        return retList;
    }

    /**
     * 获取当前标题
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置最多选几个
     *
     * @param count
     */
    public void setMaxSelectCount(int count) {
        maxSelectCount = count;
    }

    /**
     * 取消选择
     */
    private void clearSelection() {
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            changeViewState(entity, false);
        }
    }

    /**
     * 获取选择了多少个
     *
     * @return
     */
    private int getSelectedCount() {
        int count = 0;
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (entity.isSelected) {
                count++;
            }
        }

        return count;
    }

    /**
     * 取消除了编辑框之外的选中
     */
    private void clearSelectionExceptEditableTextView() {
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (entity.isEditable) {
                continue;
            }
            changeViewState(entity, false);
        }
    }

    /**
     * 设置是否需要重新计算单元宽度
     * true的话不支持.9，false支持.9
     *
     * @param isResizeCellWidth
     * @param extWidthDp        不是内容区的宽度总和，左+右，单位DP
     */
    public void setResizeCellWidth(boolean isResizeCellWidth, int extWidthDp) {
        this.isResizeCellWidth = isResizeCellWidth;
        this.extWidthDp = extWidthDp;
    }

    /**
     * 刷新整个布局
     */
    public void refreshView() {
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            boolean isSelected = entity.isSelected;
            changeViewState(entity, isSelected);
        }
    }

    /**
     * 改变视图状态
     *
     * @param entity
     * @param isSelected
     */
    private void changeViewState(BodyEntity entity, boolean isSelected) {
        if (entity.isAddFlag) {
            return;
        }

        entity.isSelected = isSelected;
        if (isSelected) {
            entity.getView().setTextColor(BODY_COLOR_SELECTED);
            entity.getView().setBackgroundResource(BODY_BACKGROUND_SELECTED_RESUORCE);
        } else {
            entity.getView().setTextColor(BODY_COLOR);
            entity.getView().setBackgroundResource(BODY_BACKGROUND_RESUORCE);
        }

        if (entity.isEditable) {
            //编辑框特殊处理一下
            boolean isClear = TextUtils.isEmpty(entity.getView().getText().toString());
            if (isClear) {
                entity.getView().setText(entity.editTextFadeHint);
            }

            if (!entity.isSelected) {
                entity.getView().clearFocus();

                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(entity.getView().getWindowToken(), 0);
            }
        }
    }

    private ArrayList<String> bodyArrToStringArr() {
        return bodyArrToStringArr(null);
    }

    private ArrayList<String> bodyArrToStringArr(String removedContent) {
        ArrayList<String> dstList = new ArrayList<String>();
        for (int index = 0; index < list.size(); index++) {
            BodyEntity entity = list.get(index);
            if (!TextUtils.isEmpty(removedContent) && entity.content.equals(removedContent)) {
                continue;
            }
            if (!entity.isAddFlag && !TextUtils.isEmpty(entity.content)) {
                dstList.add(entity.content);
            }
        }

        return dstList;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        widgetLength = r - l - PixelUtil.dp2px(context, PADDING_BY_DIP * 2);

        if (runAfterOnlayout != null) {
            runAfterOnlayout.run();
            runAfterOnlayout = null;
        }
        if (runAfterOnlayoutForPreSelection != null) {
            runAfterOnlayoutForPreSelection.run();
            runAfterOnlayoutForPreSelection = null;
        }
    }

    public void setCouldClickBody(boolean clickBody) {
        isClickBody = clickBody;
    }

    public interface OuterBodyClickListener {
        public void onClick(BodyEntity body);
    }

    /**
     * 用于管理视图显示和获取当前选中项的实体类
     *
     * @author liuwenbo
     */

    public class BodyEntity {
        private boolean isSelected = false;
        private boolean isEditable = false;
        private boolean isAddFlag = false;
        private boolean isAlive = true;
        private String content = "";
        private String editTextFadeHint;

        private TextView textView;
        private EditText editTextView;

        public BodyEntity(String content, boolean isEditable, TextView view) {
            this.content = isEditable ? "" : content;
            this.editTextFadeHint = content;
            this.isEditable = isEditable;

            if (isEditable) {
                this.editTextView = (EditText) view;
            } else {
                this.textView = view;
            }
            if (isClickBody) {
                setListener();
            }
        }

        /**
         * 获取绑定的视图
         *
         * @return
         */
        private TextView getView() {
            if (!isEditable) {
                return textView;
            } else {
                return editTextView;
            }
        }

        private void setListener() {
            if (!isEditable) {
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dealClickTextView();
                    }
                });
            } else {
                //第一次的时候只有焦点获得，木有onclick事件
                editTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dealClickEditTextView();
                        }
                    }
                });
                editTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dealClickEditTextView();
                    }
                });
                editTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        content = s.toString();
                    }
                });
            }
        }

        /**
         * 处理textview的点击
         */
        private void dealClickTextView() {
            if (isAddFlag) {
                //点的是添加
                //				Toast.makeText(context, "添加标签！", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onClick(null);
                }
                return;
            }
            if (!isSelected && getSelectedCount() >= maxSelectCount) {
                Toast.makeText(context, "最多选择" + maxSelectCount + "个标签", Toast.LENGTH_SHORT).show();
                return;
            }
            isSelected = !isSelected;
            changeViewState(BodyEntity.this, isSelected);

            if (outerBodyListener != null) {
                outerBodyListener.onClick(this);
            }
        }

        /**
         * 处理Editview的点击
         */
        private void dealClickEditTextView() {
            if (isSelected) {
                return;
            }

            clearSelectionExceptEditableTextView();
            isSelected = true;
            changeViewState(BodyEntity.this, isSelected);

            boolean isClear = editTextView.getText().toString().equals(editTextFadeHint);
            if (isClear) {
                editTextView.setText("");
            }
        }

        public boolean isSelected() {
            return isSelected;
        }

        public String getContent() {
            return content;
        }
    }
}
