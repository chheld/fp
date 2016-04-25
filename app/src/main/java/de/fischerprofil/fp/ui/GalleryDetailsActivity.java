package de.fischerprofil.fp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.reference.DepthPageTransformer;
import de.fischerprofil.fp.model.reference.GalleryImage;
import de.fischerprofil.fp.rest.PicassoUtils;

public class GalleryDetailsActivity  extends AppCompatActivity {

    AppController mAppController;
    final String VOLLEY_TAG = "VOLLEY_TAG_GalleryDetailsActivity";
    Context mContext;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ArrayList<GalleryImage> data = new ArrayList<>();
    private Integer pos;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallerydetails);
        mContext=this;
        mAppController = AppController.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);

        setTitle(data.get(pos).getName());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                //noinspection ConstantConditions
                setTitle(data.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<GalleryImage> data = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<GalleryImage> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            UIUtils.makeToast(mContext, data.get(position).getUrl()); //TEST
            return PlaceholderFragment.newInstance(position, data.get(position).getName(), data.get(position).getUrl());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

                        return data.get(position).getName();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        String name, url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.name = args.getString(ARG_IMG_TITLE);
            this.url = args.getString(ARG_IMG_URL);
        }

        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_gallerydetails, container, false);

            final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);

            Picasso picasso = PicassoUtils.buildPicasso(getActivity());
            picasso.load(url).into(imageView);

            return rootView;
        }
    }
}
