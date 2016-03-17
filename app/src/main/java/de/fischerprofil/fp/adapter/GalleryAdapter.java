package de.fischerprofil.fp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.reference.GalleryImage;
import de.fischerprofil.fp.rest.PicassoUtils;
import de.fischerprofil.fp.rest.RestUtils;
import de.fischerprofil.fp.ui.UIUtils;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder> {

    private Context mContext;
    private List<GalleryImage> mDataset = new ArrayList<>();
    private Picasso mPicasso;
    private AppController mAppController;
    private final String VOLLEY_TAG = "VOLLEY_TAG_rvContactListAdapter";
    private static String URL = RestUtils.getApiURL();

    public GalleryAdapter(Context context, List<GalleryImage> data) {
        mContext = context;
        mDataset = data;
        mPicasso = PicassoUtils.buildPicasso(mContext);
        mAppController = AppController.getInstance();
    }

    @Override
    public GalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View mView = inflater.inflate(R.layout.item_gallerylist, parent, false);

            RecyclerView.ViewHolder viewHolder = new GalleryAdapter.GalleryItemViewHolder(mView);

        return (GalleryItemViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(final GalleryItemViewHolder holder, int position) {

        final GalleryImage current = mDataset.get(position);

        if (current != null) {

            // Populate the mDataset into the template view using the mDataset object
            holder.position = position;
            holder.mName.setText(current.getName());
            holder.picURL = mDataset.get(position).getUrl();

            if (holder.mImg.getDrawable() == null) {

                mPicasso.load(mDataset.get(position).getUrl())
                        .stableKey(mDataset.get(position).getUrl())
                        .resize(200, 200)
                        .placeholder(R.drawable.ic_hourglass_black)
                        .error(R.drawable.ic_default)
                        .centerCrop()
                        .tag(holder)
                        .into(holder.mImg);

                holder.mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showGalleryDialog(view, current.getUrl(), holder.position, (ArrayList<GalleryImage>) mDataset);
                        // showGalleryDialog(view, current.getUrl(), holder.position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    private void showGalleryDialog(View v, String nr, Integer position, ArrayList<GalleryImage> data) {

        UIUtils.makeToast(v.getContext(), nr); //TEST

        try {
            if (nr != null && nr != "") {

                //TODO neues fragment anzeigen
//                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(nr).build();
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                v.getContext().startActivity(intent);

//                Intent intent = new Intent(mContext, ReferenceDetailActivity.class);
//                intent.putParcelableArrayListExtra("mDataset", mDataset);
//                intent.putExtra("pos", position);
//                v.getContext().startActivity(intent);

//                Intent intent = new Intent(mContext, GalleryDetailsActivity.class);
//                intent.putParcelableArrayListExtra("mData", mData);
//                intent.putExtra("pos", position);
//                mContext.startActivity(intent);
            }
        }
        catch (Exception e) {
            UIUtils.makeToast(v.getContext(), "Image kann nicht angezeigt werden");
        }
    }

    static class GalleryItemViewHolder  extends RecyclerView.ViewHolder {

        public Integer position;
        public String picURL;
        public ImageView mImg;
        public TextView mName;

        public GalleryItemViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mName = (TextView) itemView.findViewById(R.id.tvImageName);
         }
    }
}
