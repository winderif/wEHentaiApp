package com.example.ehentaiapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ehentaiapp.util.DataLoader;
import com.example.winderif.ehentaiapp.Image;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

	private static int DISPALY_WIDTH;
	private static int DISPALY_HEIGHT;
	private static int DEFAULT_HEIGHT;

	private List<Image> images;

	private DisplayImageOptions options;

	private LayoutInflater mLayoutInflater;

	private DataLoader dataLoader;

	public ImageAdapter(Context context) {
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	public ImageAdapter(Context context, List<Image> imgs) {
		this.mLayoutInflater = LayoutInflater.from(context);

		this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(null)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(null)
				.bitmapConfig(Bitmap.Config.RGB_565)
//				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.imageScaleType(ImageScaleType.EXACTLY)
//				.resetViewBeforeLoading(true)
				.cacheInMemory(false).cacheOnDisk(true).build();

		this.images = imgs;
		this.DISPALY_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
		this.DISPALY_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;
		this.DEFAULT_HEIGHT = DISPALY_WIDTH * DISPALY_WIDTH / DISPALY_HEIGHT;

		this.dataLoader = DataLoader.getInstance(context);
	}

	static class ViewHolder {
		public ImageView mImgView;

		public TextView pageText;
		public ProgressBar progressBar;

		public TextView errorView;
		public Button retryButton;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		final ViewHolder holder;
		final Image image = images.get(position);

		if (convertView == null) {
			view = this.mLayoutInflater.inflate(R.layout.grid_item_comic, null);

			holder = new ViewHolder();
			holder.mImgView = (ImageView) view.findViewById(R.id.comic_image);

			holder.progressBar = (ProgressBar) view.findViewById(R.id.comic_loading);
			holder.pageText = (TextView) view.findViewById(R.id.comic_page);
			holder.pageText.setText(Integer.toString(image.getPage()));

			holder.errorView = (TextView) view.findViewById(R.id.error);
			holder.errorView.setText("Load Fail");
			holder.retryButton = (Button) view.findViewById(R.id.retry);
			holder.retryButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ImageLoader.getInstance().clearMemoryCache();

					File diskCache = DiskCacheUtils.findInCache(image.getSrc(),
							ImageLoader.getInstance().getDiskCache());

					if (diskCache == null) {
						new ImageTask().execute(image);
					}

					notifyDataSetChanged();
				}
			});

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if(image.getSrc() == null || image.getSrc().isEmpty()) {
			Log.i("URL", "ImageAdapter image src null " + position);

			new ImageTask().execute(image);

			holder.mImgView.getLayoutParams().width = DISPALY_WIDTH;
			holder.mImgView.getLayoutParams().height = DEFAULT_HEIGHT;
			holder.mImgView.setImageBitmap(null);

			holder.progressBar.setVisibility(View.VISIBLE);
			holder.pageText.setVisibility(View.VISIBLE);

			holder.errorView.setVisibility(View.INVISIBLE);
			holder.retryButton.setVisibility(View.INVISIBLE);

			return view;
		}

		holder.mImgView.getLayoutParams().width = DISPALY_WIDTH;
		holder.mImgView.getLayoutParams().height = image.getHeight() * DISPALY_WIDTH / image.getWidth();

		ImageLoader.getInstance().displayImage(image.getSrc(), holder.mImgView,
				options, new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						holder.mImgView.setImageBitmap(loadedImage);

						holder.progressBar.setVisibility(View.INVISIBLE);
						holder.pageText.setVisibility(View.INVISIBLE);

						holder.errorView.setVisibility(View.INVISIBLE);
						holder.retryButton.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						File diskCache = DiskCacheUtils.findInCache(imageUri, ImageLoader.getInstance().getDiskCache());
						if (diskCache != null) {
							Log.i("URL", diskCache.getAbsolutePath());
						}

						holder.progressBar.setVisibility(View.VISIBLE);
						holder.pageText.setVisibility(View.VISIBLE);

						holder.errorView.setVisibility(View.INVISIBLE);
						holder.retryButton.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						holder.progressBar.setVisibility(View.INVISIBLE);
						holder.pageText.setVisibility(View.INVISIBLE);

						holder.errorView.setVisibility(View.VISIBLE);
						holder.retryButton.setVisibility(View.VISIBLE);
					}
				});

		return view;
	}

	public class ImageTask extends AsyncTask<Image, Void, Integer> {
		@Override
		protected Integer doInBackground(Image... param) {

			Image oldImage = param[0];

			images.set(oldImage.getPage()-1,
					dataLoader.getImageInfo(oldImage.getGalleryId(), oldImage.getPage(), true));

			return oldImage.getPage();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == getCount()) {
				notifyDataSetChanged();
			}
		}
	}
}