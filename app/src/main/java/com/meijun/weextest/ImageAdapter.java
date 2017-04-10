package com.meijun.weextest;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;
/**
 * Created by lixinke on 16/6/1.
 */
public class ImageAdapter implements IWXImgLoaderAdapter {
  private static final String TAG = "ImageAdapter";
  @Override
  public void setImage(String url, ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
    //实现你自己的图片下载，否则图片无法显示。
    if(view==null){
      Log.e(TAG, "setImage: "+" view is null" );
    }
    Picasso.with(view.getContext()).load(url).into(view);
  }
}