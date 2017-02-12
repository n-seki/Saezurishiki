package com.seki.saezurishiki.view.adapter.viewholder;

import android.view.View;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.customview.PictureThumbnail;

/**
 * Picture添付Status用ViewHolder<br>
 */
public class ViewHolderWithPicture extends ViewHolder {

    public PictureThumbnail[] mPictures;

    public ViewHolderWithPicture(View view) {
        super(view);

        mPictures = new PictureThumbnail[4];

        mPictures[0] = (PictureThumbnail)view.findViewById(R.id.picture1);
        mPictures[1] = (PictureThumbnail)view.findViewById(R.id.picture2);
        mPictures[2] = (PictureThumbnail)view.findViewById(R.id.picture3);
        mPictures[3] = (PictureThumbnail)view.findViewById(R.id.picture4);
    }
}
