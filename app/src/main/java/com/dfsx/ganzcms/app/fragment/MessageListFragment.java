package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.PushApi;
import com.dfsx.ganzcms.app.business.PushMessageHelper;
import com.dfsx.ganzcms.app.push.IPushMessageClickEvent;
import com.dfsx.ganzcms.app.push.OnMessageClickEvent;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Messages;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.LXBottomDialog;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by liuwb on 2017/6/29.
 */
public class MessageListFragment extends AbsListFragment {

    public static final String KEY_INTENT_MESSAGE_TYPE = "MessageListFragment_message_type";

    private MessageListAdapter adapter;
    /**
     * 消息的类型
     * 关注消息2， 系统消息1
     */
    private int type;
    private EmptyView emptyView;
    private OnMessageClickEvent messageClickEvent;

    private static int countPerPage = 20;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        doIntent();
        super.onViewCreated(view, savedInstanceState);
        intTopBar();
        initAction();
        getData(1, false);
    }


    private void intTopBar() {
        if (act instanceof WhiteTopBarActivity) {
            WhiteTopBarActivity whiteTopbarAct = (WhiteTopBarActivity) act;
            TextView rightText = whiteTopbarAct.getTopBarRightText();
            rightText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.icon_more_point, 0);
            rightText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog();
                }
            });
        }
    }

    private void showMoreDialog() {
        LXBottomDialog dialog = new LXBottomDialog(act, new String[]{"删除所有消息", "全部标记为已读"}, new LXBottomDialog.onPositionClickListener() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    showDeleteAlertDialog(null);
                } else {
                    tagAllMessageRead();
                }
            }
        });
        if (act != null && !act.isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new MessageListAdapter(context);
        listView.setAdapter(adapter);
    }

    private void doIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt(KEY_INTENT_MESSAGE_TYPE, 1);
        } else {
            type = 1;
        }
    }

    private void getData(int pageIndex, boolean isA) {
        PushApi.getMessages(context, type, 0, pageIndex, countPerPage, isA, new DataRequest.DataCallback<Messages>() {
            @Override
            public void onSuccess(boolean isAppend, Messages data) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                if (data.getData() == null || data.getData().size() == 0)
                    return;
                adapter.update(data.getData(), isAppend);
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }
        });
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("目前还没有消息");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        int pageCount = 0;
        int size = adapter.getCount();
        if (size % countPerPage > 0) {
            pageCount = size / countPerPage + 2;
        } else {
            pageCount = size / countPerPage + 1;
        }
        getData(pageCount, true);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1, false);
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    private void initAction() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                if (adapter != null && adapter.getData() != null &&
                        position >= 0 && position < adapter.getData().size()) {
                    onItemMessageClick(adapter.getData().get(position));
                }
            }
        });

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "删除");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        int pos = info.position;
        switch (item.getItemId()) {
            case 0:
                //System.out.println("删除"+info.id);
                int p = pos - listView.getHeaderViewsCount();
                Message message = adapter.getData().get(p);
                showDeleteAlertDialog(message);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void tagAllMessageRead() {
        PushMessageHelper.getInstance().tagAllMessageRead(type);
        if (adapter != null && adapter.getData() != null) {
            for (Message message : adapter.getData()) {
                message.setHas_read(true);
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void showDeleteAlertDialog(final Message message) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            LXDialog dialog = new LXDialog.Builder(getActivity())
                    .setMessage("是否删除数据?")
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            if (message == null) {
                                clearMessage();
                            } else {
                                deleteMessage(message);
                            }
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        }
    }

    private void clearMessage() {
        PushApi.removeAllMessage(context, type, new DataRequest.DataCallback<Void>() {
            @Override
            public void onSuccess(boolean isAppend, Void data) {
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMessage(final Message message) {
        PushApi.removeMessage(context, message.getId(), new DataRequest.DataCallback<Void>() {
            @Override
            public void onSuccess(boolean isAppend, Void data) {
                adapter.getData().remove(message);
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 每条Message的点击事件
     *
     * @param message
     */
    private void onItemMessageClick(Message message) {
        if (messageClickEvent == null) {
            messageClickEvent = new OnMessageClickEvent(getActivity(), new IPushMessageClickEvent.DefaultPushMessageClickEvent());
        }
        messageClickEvent.onMessageClick(message);
        if (message != null) {
            PushMessageHelper.getInstance().tagMessageRead(message.getId());
            message.setHas_read(true);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PushMessageHelper.getInstance().getNoReadMessageInfo(App.getInstance().getApplicationContext(),
                true, null);
    }

    class MessageListAdapter extends BaseListViewAdapter<Message> {

        public MessageListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return type == 1 ? R.layout.system_messsage_item : R.layout.adapter_user_push_message_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            if (type == 1) {
                TextView content = holder.getView(R.id.item_content);
                TextView time = holder.getView(R.id.item_time);
                ImageView pointTag = holder.getView(R.id.item_message_point_tag);
                content.setText(list.get(position).getContent());
                time.setText(StringUtil.getTimeText(list.get(position).getCreation_time()));
                pointTag.setVisibility(list.get(position).isHas_read() ? View.GONE : View.VISIBLE);
            } else {
                TextView name = holder.getView(R.id.item_user_name);
                TextView content = holder.getView(R.id.item_content);
                TextView time = holder.getView(R.id.item_time);
                CircleButton circleButton = holder.getView(R.id.item_logo);
                ImageView vipTag = holder.getView(R.id.item_vip_tag);
                ImageView pointTag = holder.getView(R.id.item_message_point_tag);

                Util.LoadThumebImage(circleButton,
                        list.get(position).getAvatar_url(), null);
                name.setText(list.get(position).getShowTitle());
                content.setText(list.get(position).getContent());
                time.setText(StringUtil.getTimeText(list.get(position).getCreation_time()));
                vipTag.setVisibility(View.GONE);
                pointTag.setVisibility(list.get(position).isHas_read() ? View.GONE : View.VISIBLE);
            }

        }
    }
}
