package cn.bingoogolapple.androidcommon.adapter.databinding;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
public abstract class ItemDatabindingDummyBinding extends ViewDataBinding {
    protected ItemDatabindingDummyBinding(android.databinding.DataBindingComponent bindingComponent, android.view.View root_, int localFieldCount
    ) {
        super(bindingComponent, root_, localFieldCount);
    }
    public abstract void setModel(java.lang.Object model);
    public abstract void setItemEventHandler(java.lang.Object itemEventHandler);
    public abstract void setViewHolder(cn.bingoogolapple.androidcommon.adapter.BGABindingViewHolder viewHolder);
    public static ItemDatabindingDummyBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ItemDatabindingDummyBinding inflate(android.view.LayoutInflater inflater) {
        return inflate(inflater, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ItemDatabindingDummyBinding bind(android.view.View view) {
        return null;
    }
    public static ItemDatabindingDummyBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
    public static ItemDatabindingDummyBinding inflate(android.view.LayoutInflater inflater, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
    public static ItemDatabindingDummyBinding bind(android.view.View view, android.databinding.DataBindingComponent bindingComponent) {
        return null;
    }
}