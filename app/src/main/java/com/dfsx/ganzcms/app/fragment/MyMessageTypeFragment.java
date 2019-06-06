package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.PushMessageHelper;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/6/28.
 */
public class MyMessageTypeFragment extends Fragment {

    private ListView listView;
    private ArrayList<MessageTypeData> messageTypeDataList;
    private MyMessageTypeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_my_message_type, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.message_type_list_view);
        setListAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                if (position >= 0 && position < messageTypeDataList.size()) {
                    MessageTypeData data = messageTypeDataList.get(position);
                    if (data != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(MessageListFragment.KEY_INTENT_MESSAGE_TYPE, data.messageType);
                        WhiteTopBarActivity.startAct(getContext(), MessageListFragment.class.getName(),
                                data.typeText, "", bundle);
                    }
                }

            }
        });
        PushMessageHelper.getInstance().addNoReadPushMessageChangeListener(noReadMessageChangeListener);
    }

    private PushMessageHelper.NoReadPushMessageChangeListener noReadMessageChangeListener = new PushMessageHelper.NoReadPushMessageChangeListener() {
        @Override
        public void onNoReadMessage(PushMessageHelper.NoReadPushMessage data) {
            if (data != null && data.getMessageCategoryList() != null) {
                for (int i = 0; i < messageTypeDataList.size(); i++) {
                    boolean isSet = false;
                    for (PushMessageHelper.NoReadCategoryMessage categoryMessage : data.getMessageCategoryList()) {
                        if (categoryMessage.getCategory() == messageTypeDataList.get(i).messageType) {
                            messageTypeDataList.get(i).isHasMessagePoint = true;
                            isSet = true;
                            break;
                        }
                    }
                    if (!isSet) {
                        messageTypeDataList.get(i).isHasMessagePoint = false;
                    }
                }
            } else {
                for (int i = 0; i < messageTypeDataList.size(); i++) {
                    messageTypeDataList.get(i).isHasMessagePoint = false;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    };


    protected void setListAdapter() {
        adapter = new MyMessageTypeAdapter(getContext());
        listView.setAdapter(adapter);
        messageTypeDataList = new ArrayList<>();
        //        MessageTypeData data = new MessageTypeData(R.drawable.icon_msg_comment, "评论消息");
        //        messageTypeDataList.add(data);
        //        data = new MessageTypeData(R.drawable.icon_msg_zan, "点赞消息", 1);
        //        messageTypeDataList.add(data);
        //        data = new MessageTypeData(R.drawable.icon_msg_invite, "邀请回答", 1);
        //        messageTypeDataList.add(data);
        MessageTypeData data = new MessageTypeData(R.drawable.icon_msg_concern, "关注消息", 2);
        messageTypeDataList.add(data);
        data = new MessageTypeData(R.drawable.icon_msg_system, "系统消息", 1);
        messageTypeDataList.add(data);

        adapter.update(messageTypeDataList, false);

        PushMessageHelper.getInstance()
                .getNoReadMessageInfo(getContext(),
                        false, new DataRequest.DataCallback<PushMessageHelper.NoReadPushMessage>() {
                            @Override
                            public void onSuccess(boolean isAppend, PushMessageHelper.NoReadPushMessage data) {
                                if (data != null && data.getMessageCategoryList() != null) {
                                    for (int i = 0; i < messageTypeDataList.size(); i++) {
                                        for (PushMessageHelper.NoReadCategoryMessage categoryMessage : data.getMessageCategoryList()) {
                                            if (categoryMessage.getCategory() == messageTypeDataList.get(i).messageType) {
                                                messageTypeDataList.get(i).isHasMessagePoint = true;
                                                break;
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFail(ApiException e) {
                                e.printStackTrace();
                            }
                        });
    }


    class MyMessageTypeAdapter extends BaseListViewAdapter<MessageTypeData> {

        public MyMessageTypeAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_message_type;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView imageView = holder.getView(R.id.item_message_type_img);
            TextView textView = holder.getView(R.id.item_message_type_text);
            ImageView pointView = holder.getView(R.id.item_message_point);
            MessageTypeData data = list.get(position);
            imageView.setImageResource(data.typeResId);
            textView.setText(data.typeText);
            pointView.setVisibility(data.isHasMessagePoint ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (noReadMessageChangeListener != null) {
            PushMessageHelper.getInstance()
                    .removeNoReadPushMessageChangeListener(noReadMessageChangeListener);
        }
    }

    class MessageTypeData {
        int typeResId;
        String typeText;
        int messageType;
        boolean isHasMessagePoint;

        public MessageTypeData() {

        }

        public MessageTypeData(int resId, String text, int messageType) {
            this.typeResId = resId;
            this.typeText = text;
            this.messageType = messageType;
        }

        public MessageTypeData(int resId, String text) {
            this.typeResId = resId;
            this.typeText = text;
            this.messageType = 1;
        }
    }
}
