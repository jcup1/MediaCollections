/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package mywins.theandroiddev.com.mediacollections.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import mywins.theandroiddev.com.mediacollections.Constants;
import mywins.theandroiddev.com.mediacollections.ExtendedViewPager;
import mywins.theandroiddev.com.mediacollections.R;
import mywins.theandroiddev.com.mediacollections.TouchImageView;


/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @edited by jcup
 */
public class ImagePagerFragment extends Fragment {

    public static final int INDEX = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_pager, container, false);
        ExtendedViewPager pager = rootView.findViewById(R.id.pager);

        pager.setAdapter(new ImageAdapter(getActivity()));
        pager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));
        return rootView;
    }

    static class ImageAdapter extends PagerAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;
        ImageView pagerFilter1, pagerFilter2, pagerFilter3;
        int currentFilter;
        ConstraintLayout pagerLayout;
        CardView pagerCardView;
        private Context context;
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);

            ButterKnife.bind((Activity) context);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        void setSepiaColorFilter(ImageView drawable) {
            if (drawable == null)
                return;

            final ColorMatrix matrixA = new ColorMatrix();
            // making image B&W
            matrixA.setSaturation(0);

            final ColorMatrix matrixB = new ColorMatrix();
            // applying scales for RGB color values
            matrixB.setScale(1f, .95f, .82f, 1.0f);
            matrixA.setConcat(matrixB, matrixA);

            final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
            drawable.setColorFilter(filter);
        }

        void setAccentColorFilter(ImageView drawable) {
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        void setDarkColorFilter(ImageView drawable) {
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.cardview_dark_background), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        private void addFilter(int i, final TouchImageView tiv) {
            if (currentFilter != i) {
                switch (i) {
                    case 1:
                        setAccentColorFilter(tiv);
                        break;
                    case 2:
                        setSepiaColorFilter(tiv);
                        break;
                    case 3:
                        setDarkColorFilter(tiv);
                        break;
                }
            }

        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            currentFilter = -1;
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);

            final TouchImageView touchImageView = imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading);

            //It could be RecyclerView but not it's necessary and it would take longer
            pagerFilter1 = imageLayout.findViewById(R.id.pager_filter_1);
            pagerFilter2 = imageLayout.findViewById(R.id.pager_filter_2);
            pagerFilter3 = imageLayout.findViewById(R.id.pager_filter_3);
            pagerLayout = imageLayout.findViewById(R.id.pager_image_layout);
            pagerCardView = imageLayout.findViewById(R.id.pager_card_view);

            pagerFilter1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAccentColorFilter(touchImageView);
                }
            });
            pagerFilter2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSepiaColorFilter(touchImageView);
                }
            });

            pagerFilter3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDarkColorFilter(touchImageView);
                }
            });

            ImageLoader.getInstance().displayImage(IMAGE_URLS[position], touchImageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                    pagerCardView.setVisibility(View.VISIBLE);
                    setFilters(position);

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                    pagerCardView.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                    //JCUP- strange bug (I have to set touchImageView visibility to GONE and change it to VISIBLE
                    //after ProgressBar is set to GONE. Otherwise it won't work
                    setFilters(position);
                    touchImageView.setVisibility(View.VISIBLE);
                    pagerCardView.setVisibility(View.VISIBLE);

                }
            });

            view.addView(imageLayout);
            return imageLayout;
        }

        private void setFilters(int position) {

            Glide.with(pagerLayout).load(IMAGE_URLS[position]).into(pagerFilter1);
            Glide.with(pagerLayout).load(IMAGE_URLS[position]).into(pagerFilter2);
            Glide.with(pagerLayout).load(IMAGE_URLS[position]).into(pagerFilter3);
            setAccentColorFilter(pagerFilter1);
            setSepiaColorFilter(pagerFilter2);
            setDarkColorFilter(pagerFilter3);

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }
}