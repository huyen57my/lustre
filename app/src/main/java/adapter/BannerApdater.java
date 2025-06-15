package adapter;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class BannerApdater extends RecyclerView.Adapter<BannerApdater.BannerViewHolder> {

    private List<String> imgUrls;
    private Context context;

    public BannerApdater(Context ctx, List<String> imageUrls) {
        this.context = ctx;
        this.imgUrls = imageUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = new CardView(context);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        cardView.setRadius(32); // bo góc tại đây
        cardView.setPreventCornerOverlap(true);
        cardView.setUseCompatPadding(true);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        cardView.addView(imageView);
        return new BannerViewHolder(cardView, imageView);
    }


    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(32));

        Glide.with(context)
                .load(imgUrls.get(position))
                .apply(requestOptions)
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return  imgUrls.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;

        public BannerViewHolder(@NonNull View itemView, ImageView imageView) {
            super(itemView);
            this.imgView = imageView;
        }
    }
}
