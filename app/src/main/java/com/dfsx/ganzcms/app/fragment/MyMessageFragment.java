package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.DeepColorSwitchTopbarActivity;
import com.dfsx.ganzcms.app.business.PushApi;
import com.dfsx.ganzcms.app.push.IPushMessageClickEvent;
import com.dfsx.ganzcms.app.push.MessagePushManager;
import com.dfsx.ganzcms.app.push.OnMessageClickEvent;
import com.dfsx.ganzcms.app.push.OnPushListener;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Messages;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;


/**
 * Created by wen on 2017/3/20.
 */

public class MyMessageFragment extends AbsListFragment implements
        DeepColorSwitchTopbarActivity.ISwitchTopBarActionListener, OnPushListener {

    private SystemMessageItemAdapter systemMessageItemAdapter;
    private AttentionMessageItemAdapter attentionMessageItemAdapter;
    private EmptyView emptyView;
    private ListView listView;
    private int systemMessagePageCount = 1;
    private int attentionMessagePageCount = 1;
    private static int countPerPage = 20;
    //首先显示的是关注类消息
    private int type = 2;

    private OnMessageClickEvent messageClickEvent;

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MessagePushManager.getInstance().addOnMessagePushReceiveListener(this);
        getData(1, false);
    }


    private void getData(int pageIndex, boolean isA) {
        PushApi.getMessages(context, type, 0, pageIndex, countPerPage, isA, new DataRequest.DataCallback<Messages>() {
            @Override
            public void onSuccess(boolean isAppend, Messages data) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                if (data.getData() == null || data.getData().size() == 0)
                    return;
                if (type == 1) {
                    systemMessageItemAdapter.update(data.getData(), isAppend);
                } else {
                    attentionMessageItemAdapter.update(data.getData(), isAppend);
                }
            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
            }
        });
    }

    @Override
    public void setListAdapter(ListView listView) {
        this.listView = listView;
        systemMessageItemAdapter = new SystemMessageItemAdapter(context);
        attentionMessageItemAdapter = new AttentionMessageItemAdapter(context);
        listView.setAdapter(attentionMessageItemAdapter);
        initListViewAction(listView);
    }

    private void initListViewAction(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                BaseListViewAdapter<Message> adapter = null;
                if (listView.getAdapter() instanceof BaseListViewAdapter) {
                    adapter = (BaseListViewAdapter<Message>) parent.getAdapter();
                } else if (listView.getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
                    if (headerViewListAdapter != null && headerViewListAdapter.getWrappedAdapter() instanceof BaseListViewAdapter) {
                        adapter = (BaseListViewAdapter<Message>) headerViewListAdapter.getWrappedAdapter();
                    }
                }

                if (adapter != null && adapter.getData() != null &&
                        position >= 0 && position < adapter.getData().size()) {
                    onItemMessageClick(adapter.getData().get(position));
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int p = position - 1;
                Message message;
                if (type == 1) {
                    message = systemMessageItemAdapter.getData().get(p);
                } else {
                    message = attentionMessageItemAdapter.getData().get(p);
                }
                showDeleteAlertDialog(message);
                return false;
            }
        });
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
    }

    @Override
    public void onActFinish() {

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
                if (type == 1) {
                    systemMessageItemAdapter.getData().clear();
                    systemMessageItemAdapter.notifyDataSetChanged();
                } else {
                    attentionMessageItemAdapter.getData().clear();
                    attentionMessageItemAdapter.notifyDataSetChanged();
                }
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
                BaseListViewAdapter<Message> adapter;
                if (type == 1) {
                    adapter = systemMessageItemAdapter;
                } else {
                    adapter = attentionMessageItemAdapter;
                }
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
    public void onRightViewClick(TextView textView) {
        if ((type == 1 && systemMessageItemAdapter.getCount() == 0) ||
                (type == 2 && attentionMessageItemAdapter.getCount() == 0)) {
            Toast.makeText(context, "没有数据", Toast.LENGTH_SHORT).show();
            return;
        }

        showDeleteAlertDialog(null);

    }

    @Override
    public void onCheckChange(int position, String optionString) {
        type = type == 1 ? 2 : 1;
        if (type == 1)
            listView.setAdapter(systemMessageItemAdapter);
        else
            listView.setAdapter(attentionMessageItemAdapter);
        systemMessageItemAdapter.notifyDataSetChanged();
        attentionMessageItemAdapter.notifyDataSetChanged();
        getData(1, false);
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
        if (type == 1) {
            int size = systemMessageItemAdapter.getCount();
            if (size % countPerPage > 0) {
                systemMessagePageCount = size / countPerPage + 2;
            } else {
                systemMessagePageCount = size / countPerPage + 1;
            }
            getData(systemMessagePageCount, true);
        } else {
            int size = attentionMessageItemAdapter.getCount();
            if (size % countPerPage > 0) {
                attentionMessagePageCount = size / countPerPage + 2;
            } else {
                attentionMessagePageCount = size / countPerPage + 1;
            }
            getData(attentionMessagePageCount, true);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1, false);
    }

    @Override
    public void onMessageReceive(Extension extension) {
        String s = extension.getSource();
    }

    @Override
    public MessagePushManager.MessageFilter getFilter() {
        MessagePushManager.MessageFilter filter = new MessagePushManager.MessageFilter();
        filter.addToFilter(Extension.MessageType.general_notice);
        return filter;
    }

    private class AttentionMessageItemAdapter extends BaseListViewAdapter<Message> {

        public AttentionMessageItemAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.system_messsage_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView name = holder.getView(R.id.item_user_name);
            TextView content = holder.getView(R.id.item_content);
            TextView time = holder.getView(R.id.item_time);
            CircleButton circleButton = holder.getView(R.id.item_logo);
            ImageView vipTag = holder.getView(R.id.item_vip_tag);

            vipTag.setVisibility(View.GONE);
            Util.LoadThumebImage(circleButton, list.get(position).getAvatar_url(), null);
            name.setText(list.get(position).getShowTitle());
            content.setText(list.get(position).getContent());
            time.setText(StringUtil.getTimeText(list.get(position).getCreation_time()));
        }
    }


    private class SystemMessageItemAdapter extends BaseListViewAdapter<Message> {

        public SystemMessageItemAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.system_messsage_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView name = holder.getView(R.id.item_user_name);
            TextView content = holder.getView(R.id.item_content);
            TextView time = holder.getView(R.id.item_time);
            CircleButton circleButton = holder.getView(R.id.item_logo);
            ImageView vipTag = holder.getView(R.id.item_vip_tag);

            Util.LoadThumebImage(circleButton, list.get(position).getAvatar_url(), null);
            name.setText(list.get(position).getShowTitle());
            content.setText(list.get(position).getContent());
            time.setText(StringUtil.getTimeText(list.get(position).getCreation_time()));
            vipTag.setVisibility(View.GONE);
        }
    }
}
