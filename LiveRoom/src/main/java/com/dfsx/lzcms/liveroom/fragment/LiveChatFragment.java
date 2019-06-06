package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfsx.core.CoreApp;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.LiveRoomHttpSendGift;
import com.dfsx.lzcms.liveroom.business.MyMoneyInfoManager;
import com.dfsx.lzcms.liveroom.business.SendGift;
import com.dfsx.lzcms.liveroom.model.GiftMessage;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.model.GiftResponseInfo;
import com.dfsx.lzcms.liveroom.model.UserChatMessage;
import com.dfsx.lzcms.liveroom.util.IntentUtil;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.ReceiveGiftShowUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.*;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;

import java.util.List;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveChatFragment extends Fragment {

    private Activity act;
    private Context context;

    private BarrageListViewSimple chatListViewSimple;
    private LiveBottomEditBar bottomEditBar;

    private SendGiftPopupwindow sendGiftPopupwindow;

    private Handler handler = new Handler();

    private IsLoginCheck loginCheck;

    private SendGift giftSender;

    private int chatNameColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        loginCheck = new IsLoginCheck(context);

        chatNameColor = context.getResources().getColor(R.color.gray_guess_chat_name);
        return inflater.inflate(R.layout.frag_live_chat, null);
    }

    private String getRoomEnterId() {
        String id = "";
        if (act instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) act).getRoomEnterId();
        }
        return id;
    }

    private long getRoomId() {
        if (act instanceof AbsChatRoomActivity) {
            return ((AbsChatRoomActivity) act).getRoomId();
        }
        return 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatListViewSimple = (BarrageListViewSimple) view.findViewById(R.id.chat_list);
        bottomEditBar = (LiveBottomEditBar) view.findViewById(R.id.chat_bottom_bar);

        chatListViewSimple.setBackgroundColor(context.getResources().getColor(R.color.public_bgd));
        chatListViewSimple.setDefaultShowTextColor(context.getResources().getColor(R.color.black_36));
        chatListViewSimple.setOnRecyclerViewClickListener(new RecyclerItemClickListener.OnClickListener() {
            @Override
            public void onClick(View view, float x, float y) {
                hideInput();
            }
        });

        bottomEditBar.setSendGiftViewVisiable(true);
        bottomEditBar.setOnBarClickListener(new LiveBottomEditBar.OnBarClickListener() {
            @Override
            public void onSendTextClick(String text) {
                //                chatListViewSimple.addItemData(new
                //                        BarrageListViewSimple.BarrageItem(AndroidUtil.getUniqueId(context), text));
                if (!loginCheck.checkLogin()) {
                    return;
                }
            }

            @Override
            public void onSendGiftClick() {
                if (sendGiftPopupwindow == null) {
                    sendGiftPopupwindow = new SendGiftPopupwindow(act);
                    sendGiftPopupwindow.setOnClickEventListener(new SendGiftPopupwindow.OnClickEventListener() {
                        @Override
                        public void onBuyGoldClick() {
                            IntentUtil.goAddMoneyActivity(act);
                        }

                        @Override
                        public void onSendGiftClick(final GiftModel gift, final int num, boolean isManySend) {
                            if (!loginCheck.checkLogin()) {
                                return;
                            }
                            if (gift != null) {
                                if (MyMoneyInfoManager.getInstance().getCacheMoneyInfo() != null
                                        &&
                                        MyMoneyInfoManager.getInstance().getCacheMoneyInfo().getCoins() <
                                                gift.getPrice() * num) {
                                    showNoEnoughMoneyDialog();
                                    return;
                                }
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("isManySend", isManySend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (giftSender == null) {
                                    giftSender = new LiveRoomHttpSendGift(context, getRoomId(), getRoomEnterId());
                                }
                                giftSender.sendGift(gift, num, getRoomName(), new ICallBack<GiftResponseInfo>() {
                                    @Override
                                    public void callBack(GiftResponseInfo data) {
                                        boolean isSuccess = data.isSuccess();
                                        if (!isSuccess) {
                                            String giftResponseText = data.getErrorMsg();
                                            if (data.isNoEnoughMoney()) {
                                                showNoEnoughMoneyDialog();
                                            }
                                            Log.e("TAG", "send gift error == " + giftResponseText);
                                        } else {
                                            double payCount = gift.getPrice() * num;
                                            MyMoneyInfoManager.getInstance().payMoney(payCount);
                                            sendGiftPopupwindow.updateMoneyText();
                                        }
                                    }
                                });
                            }

                        }
                    });
                }
                sendGiftPopupwindow.show(bottomEditBar);
            }
        });

        /*XmppManager.getInstance().addPacketListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if (packet != null) {
                    Log.e("TAG", packet.getFrom());
                    Log.e("TAG", packet.getStanzaId());
                    Log.e("TAG", packet.toString());

                }
            }
        });*/
    }

    private void showNoEnoughMoneyDialog() {
        if (act != null && !act.isFinishing()) {
            LXDialog.Builder builder = new LXDialog.Builder(act);
            LXDialog dialog = builder.setMessage("账户余额不足,请充值")
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            IntentUtil.goAddMoneyActivity(act);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    public void hideInput() {
        if (bottomEditBar != null) {
            bottomEditBar.hideInput();
        }
    }

    public void processMessage(final UserChatMessage message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String fromUserName = message.getUserNickName();
                boolean isShowBottom = StringUtil.isCurrentUserName(message.getUserName());
                CharSequence content = message.getBody();
                if (!TextUtils.isEmpty(content)) {
                    chatListViewSimple.addItemData(new
                            BarrageListViewSimple.BarrageItem(fromUserName, chatNameColor, content));
                }

            }
        });
    }

    public void doReceiveGift(List<GiftMessage> receiveGiftInfo) {
        ReceiveGiftShowUtil.showReceiveGiftInList(CoreApp.getInstance().getApplicationContext(),
                chatNameColor,
                chatListViewSimple, receiveGiftInfo);
    }

    private String getRoomName() {
        if (act instanceof AbsChatRoomActivity) {
            return ((AbsChatRoomActivity) act).getRoomName();
        }

        return "";
    }
}
