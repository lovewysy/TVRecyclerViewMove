package com.hwp.tv;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.hwp.tv.databinding.ItemAppMgtBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author hongweiping
 * @date 2023/3/23
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private List<? extends AppUtils.AppInfo> mData;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_app_mgt, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemAppMgtBinding itemAppMgtBinding = (ItemAppMgtBinding) holder.mDataBinding;
        itemAppMgtBinding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    if(mOnItemKeyListener!=null){
                         mOnItemKeyListener.onKey(view, holder.getAdapterPosition(),keyEvent.getKeyCode());
                    }}
                return false;
            }
        });
        itemAppMgtBinding.setVariable(BR.appEntity,mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    private  OnItemKeyListener mOnItemKeyListener;

    public void setOnItemKeyListener(OnItemKeyListener onItemKeyListener) {
        mOnItemKeyListener = onItemKeyListener;
    }

    public interface OnItemKeyListener {
        void onKey(View var1, int var2,int var3);
    }


    public void setDatas(@NotNull List<? extends AppUtils.AppInfo> appsInfo) {
        this.mData = appsInfo;
    }

    static class  ViewHolder extends  RecyclerView.ViewHolder{
        ViewDataBinding mDataBinding;
        public ViewHolder(@NonNull ViewDataBinding dataBinding) {
            super(dataBinding.getRoot());
            mDataBinding = dataBinding;
        }
    }
}
