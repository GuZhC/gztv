package com.dfsx.lzcms.liveroom.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.*;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.lzcms.liveroom.R;

/**
 * 使用方式同原生dialog
 * 通用dialog
 */
public class LXDialog extends Dialog {
    private EditText editTextHolder;
    private LXDialogDismissInterface onDismissListener;

    public interface LXDialogDismissInterface {
        void onDismiss();
    }

    public LXDialog(Context context) {
        super(context);
    }

    public LXDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setCustomOnDismissListener(LXDialogDismissInterface listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void dismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        super.dismiss();
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String msg;
        private int imgId;
        private boolean isShowExitButton; //是否显示右上角退出按钮
        private boolean isHiddenCancleBtn; //是否隐藏取消按钮
        private boolean isCircularImage; //是否显示圆形图片
        private boolean isEditMode; //编辑模式，带编辑框
        private boolean isHiddenMsgText;//是否隐藏显示消息的控件

        private String positiveButtonText, negativeButtonText;

        private Button ok, cancel;
        private TextView tmsg;
        private ImageView ivImg;
        private ImageButton ibtnExit;
        private CircleButton circularImage;
        private EditText editText;

        public interface LXDialogInterface {
            void onClick(DialogInterface dialog, View v);
        }

        private LXDialogInterface positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * is hidden the button of cancle
         *
         * @return
         */
        public Builder isHiddenCancleButton(boolean isHidden) {
            this.isHiddenCancleBtn = isHidden;
            return this;
        }

        /**
         * is show the button of exit
         *
         * @return
         */
        public Builder isShowExitButton(boolean isShow) {
            this.isShowExitButton = isShow;
            return this;
        }

        /**
         * Set the Dialog image for resource
         *
         * @param imgId
         * @return
         */
        public Builder setImage(int imgId) {
            setImage(false, imgId);
            return this;
        }

        /**
         * Set the Dialog circularImage for resource
         *
         * @param imgId
         * @return
         */
        public Builder setImage(boolean isCircle, int imgId) {
            this.imgId = imgId;
            this.isCircularImage = isCircle;
            return this;
        }

        /**
         * Set the Dialog message for resource
         *
         * @param msg
         * @return
         */
        public Builder setMessage(int msg) {
            this.msg = (String) context.getText(msg);
            return this;
        }

        /**
         * Set the Dialog message for string
         *
         * @param msg
         * @return
         */
        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 是否是编辑模式，带编辑框
         *
         * @return
         */
        public Builder isEditMode(boolean isEditMode) {
            this.isEditMode = isEditMode;
            return this;
        }

        public Builder isHiddenMsgText(boolean hiddenMsgText) {
            this.isHiddenMsgText = hiddenMsgText;
            return this;
        }

        public Builder setPositiveButton(LXDialogInterface listener) {
            return setPositiveButton(null, listener);
        }

        /**
         * Set the positive button resource and it"s listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, LXDialogInterface listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it"s listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, LXDialogInterface listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(LXDialogInterface listener) {
            return setNegativeButton(null, listener);
        }

        /**
         * Set the negative button resource and it"s listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, LXDialogInterface listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it"s listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, LXDialogInterface listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            this.isHiddenCancleBtn = false;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public LXDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LXDialog dialog = new LXDialog(context, R.style.transparentFrameWindowStyle);
//            final LXDialog dialog = new LXDialog(context, R.style.common_dialog);
            View layout = inflater.inflate(R.layout.lx_dialog, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            setViewToDialog(layout);
            if (msg != null) {
                tmsg.setText(msg);
                tmsg.setVisibility(View.VISIBLE);
            } else {
                tmsg.setVisibility(View.INVISIBLE);
            }
            tmsg.setVisibility(isHiddenMsgText ? View.GONE : View.VISIBLE);
            editText.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            cancel.setVisibility(isHiddenCancleBtn ? View.GONE : View.VISIBLE);
            ibtnExit.setVisibility(isShowExitButton ? View.VISIBLE : View.GONE);
            if (isCircularImage && imgId > 0) {
                ivImg.setVisibility(View.GONE);
                circularImage.setVisibility(View.VISIBLE);
                circularImage.setBackground(context.getResources().getDrawable(imgId));
            } else if (!isCircularImage && imgId > 0) {
                ivImg.setVisibility(View.VISIBLE);
                circularImage.setVisibility(View.GONE);
//                ivImg.setBackgroundResource(LXApplication.getInstance().getResources().getDrawable(imgId));
                ivImg.setBackgroundResource(imgId);
            } else {
                ivImg.setVisibility(View.GONE);
                circularImage.setVisibility(View.GONE);
            }
            ok.setText(positiveButtonText != null ? positiveButtonText : context.getString(R.string.ok));
            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (positiveButtonClickListener != null)
                        positiveButtonClickListener.onClick(dialog, v);
                    dialog.dismiss();
                }
            });
            cancel.setText(negativeButtonText != null ? negativeButtonText : context.getString(R.string.cancel));
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (negativeButtonClickListener != null)
                        negativeButtonClickListener.onClick(dialog, v);
                    dialog.dismiss();
                }
            });
            ibtnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setEditTextHolder(editText);
            return dialog;
        }

        private void setViewToDialog(View layout) {
            ok = (Button) layout.findViewById(R.id.btn_dialog_ok);
            cancel = (Button) layout.findViewById(R.id.btn_dialog_cancel);
            tmsg = (TextView) layout.findViewById(R.id.tv_dialog_msg);
            ibtnExit = (ImageButton) layout.findViewById(R.id.ibtn_exit);
            ivImg = (ImageView) layout.findViewById(R.id.img);
            circularImage = (CircleButton) layout.findViewById(R.id.circularImage);
            editText = (EditText) layout.findViewById(R.id.edittext);
        }
    }

    private void setEditTextHolder(EditText target) {
        editTextHolder = target;
    }

    public EditText getEditText() {
        return editTextHolder;
    }
}
