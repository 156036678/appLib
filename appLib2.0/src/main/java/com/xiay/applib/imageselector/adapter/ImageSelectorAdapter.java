package com.xiay.applib.imageselector.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.xiay.applib.R;
import com.xiay.applib.imageselector.ImageSelectorConfig;
import com.xiay.applib.imageselector.model.LocalMedia;
import com.xiay.applib.imageselector.view.ImagePreviewDeleteActivity;
import com.xiay.applib.imageselector.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.xiay.bean.HttpConfig;
import cn.xiay.util.SystemUtil;

/**
 * @author Xiay
 */

public class ImageSelectorAdapter extends RecyclerView.Adapter<ImageSelectorAdapter.ViewHolder> {
    private int maxPicCount;
    LocalMedia addIcon;
    ArrayList<LocalMedia>images;
    Context ctx;
    int  requestCode;
    boolean enableCrop;
    public int REQUEST_PREVIEW_DELETE;
    public int REQUEST_ADD;

    /**
     * @param ctx Context
     * @param maxPicCount 显示的最多数量
     * @param addIcon 添加图标
     * @param requestCode 请求码，不能传0哦
     * @param enableCrop 是否开启裁剪功能（默认true）
     *
     */
    public ImageSelectorAdapter(Context ctx,int  requestCode, int maxPicCount, LocalMedia addIcon,boolean enableCrop ) {
        this.ctx = ctx;
        this.addIcon = addIcon;
        this.maxPicCount = maxPicCount;
        this.enableCrop = enableCrop;
        this.requestCode = requestCode==0? ImageSelectorActivity.REQUEST_IMAGE:requestCode;
        REQUEST_ADD=this.requestCode+1;
        REQUEST_PREVIEW_DELETE=this.requestCode-1;
    }
    /**
     * @param ctx Context
     * @param maxPicCount 显示的最多数量
     * @param addIcon 添加图标
     * @param enableCrop 是否开启裁剪功能（默认true）
     */
    public ImageSelectorAdapter(Context ctx, int maxPicCount, LocalMedia addIcon,boolean enableCrop) {
       this(ctx,ImageSelectorActivity.REQUEST_IMAGE,maxPicCount,addIcon,enableCrop);
    }
    public ImageSelectorAdapter(Context ctx, int maxPicCount, LocalMedia addIcon) {
       this(ctx,ImageSelectorActivity.REQUEST_IMAGE,maxPicCount,addIcon,true);
    }
    public ImageSelectorAdapter(Context ctx, int maxPicCount) {
        this(ctx,maxPicCount,null);
    }

    @Override
    public ImageSelectorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_selector_item_result, parent, false);
        return new ImageSelectorAdapter.ViewHolder(itemView);
    }
    /**刷新图片*/
  public  void  refeshPic(ArrayList<LocalMedia>images){
      this.images = images;
      notifyDataSetChanged();
  }
    @Override
    public void onBindViewHolder(ImageSelectorAdapter.ViewHolder holder, final int position) {
        if (images.size() <= maxPicCount) {
            holder.imageView.setVisibility(View.VISIBLE);
            RequestManager manager= Glide.with(ctx);
            final DrawableTypeRequest request;
            if (images.get(position) != addIcon) {
                if (images.get(position).getPath().startsWith(ImageSelectorConfig.serverPathStartWith)){
                    request= manager.load(HttpConfig.UrlHead+images.get(position).getPath());
                }else {
                    request= manager.load(new File(images.get(position).getPath()));
                }
                request.centerCrop().into(holder.imageView);

            } else {
                manager.load(SystemUtil.getInstance().resIdToUri(ctx, addIcon.getAddIconResId())).centerCrop().into(holder.imageView);
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  mOnItemClickListener.onItemClick(v, position);
                    if (images.get(position).equals(addIcon)) {
                        int mode;
                        if (maxPicCount==1||enableCrop==true){
                            mode=ImageSelectorActivity.MODE_SINGLE;
                        }else {
                            mode=ImageSelectorActivity.MODE_MULTIPLE;
                        }
                        ImageSelectorActivity.start((Activity) ctx,REQUEST_ADD, maxPicCount-images.size()+1,mode , true, false, enableCrop);
                    }else {
                        startPreview(images,position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (images!=null)
            return images.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
    public void startPreview(List<LocalMedia> previewImages, int position) {
        if (addIcon==null){
            ImagePreviewDeleteActivity.startPreview((Activity) ctx, images, images, maxPicCount, position,true);
        } else {
            if (images.contains(addIcon)) {//if has addIcon, remove it
                List<LocalMedia> images = new ArrayList<>();
                images.addAll(previewImages);
                images.remove(addIcon);
                ImagePreviewDeleteActivity.startPreview((Activity) ctx,REQUEST_PREVIEW_DELETE, images, images, maxPicCount, position,false);
            } else {
                ImagePreviewDeleteActivity.startPreview((Activity) ctx,REQUEST_PREVIEW_DELETE, previewImages, images, maxPicCount, position,false);
            }
        }
    }
}
