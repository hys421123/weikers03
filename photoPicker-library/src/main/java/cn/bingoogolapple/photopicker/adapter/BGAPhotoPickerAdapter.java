package cn.bingoogolapple.photopicker.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAImageFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/28 上午11:09
 * 描述:
 */
public class BGAPhotoPickerAdapter extends BGARecyclerViewAdapter<String> {
    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mTakePhotoEnabled;
    private Activity mActivity;

    public BGAPhotoPickerAdapter(Activity activity, RecyclerView recyclerView) {
        super(recyclerView, R.layout.bga_pp_item_photo_picker);
        mImageWidth = BGAPhotoPickerUtil.getScreenWidth(recyclerView.getContext()) / 6;
        mImageHeight = mImageWidth;
        mActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTakePhotoEnabled && position == 0) {
            return R.layout.bga_pp_item_photo_camera;
        } else {
            return R.layout.bga_pp_item_photo_picker;
        }
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        if (viewType == R.layout.bga_pp_item_photo_camera) {
            helper.setItemChildClickListener(R.id.iv_item_photo_camera_camera);
        } else {
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_flag);
            helper.setItemChildClickListener(R.id.iv_item_photo_picker_photo);
        }
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String model) {
        if (getItemViewType(position) == R.layout.bga_pp_item_photo_picker) {
            BGAImage.displayImage(mActivity, helper.getImageView(R.id.iv_item_photo_picker_photo), model, R.mipmap.bga_pp_ic_holder_dark, R.mipmap.bga_pp_ic_holder_dark, mImageWidth, mImageHeight, null);

            if (mSelectedImages.contains(model)) {
                helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_checked);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(helper.getConvertView().getResources().getColor(R.color.bga_pp_photo_selected_mask));
            } else {
                helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_normal);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(null);
            }
        }
    }

    public void setSelectedImages(ArrayList<String> selectedImages) {
        if (selectedImages != null) {
            mSelectedImages = selectedImages;
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedImages() {
        return mSelectedImages;
    }

    public int getSelectedCount() {
        return mSelectedImages.size();
    }

    public void setImageFolderModel(BGAImageFolderModel imageFolderModel) {
        mTakePhotoEnabled = imageFolderModel.isTakePhotoEnabled();
        setData(imageFolderModel.getImages());
    }
}
