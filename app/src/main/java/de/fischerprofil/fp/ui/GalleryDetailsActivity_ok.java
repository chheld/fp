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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.fischerprofil.fp.R;
import de.fischerprofil.fp.model.reference.GalleryImage;

public class GalleryDetailsActivity_ok extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    public ArrayList<GalleryImage> mData = new ArrayList<>();
    Integer mPos;
    Toolbar mToolbar;
    private ViewPager mViewPager;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext=this;
        setContentView(R.layout.activity_gallerydetails);

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);

        mData = getIntent().getParcelableArrayListExtra("data");

/*
        GalleryImage galleryImage = new GalleryImage();
        galleryImage.setName("I_01");
        galleryImage.setUrl(RestUtils.getPicURL()+ "/picture.png");
        mData.add(0,galleryImage);
        galleryImage = new GalleryImage();
        galleryImage.setName("I_02");
        galleryImage.setUrl(RestUtils.getPicURL()+ "/picture.png");
        mData.add(1,galleryImage);
        galleryImage = new GalleryImage();
        galleryImage.setName("I_03");
        galleryImage.setUrl(RestUtils.getPicURL()+ "/picture.png");
        mData.add(2,galleryImage);
*/

        mPos = getIntent().getIntExtra("pos", 0);

        setTitle(mData.get(mPos).getName());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mData);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mPos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                //noinspection ConstantConditions
                setTitle(mData.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

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

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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

            //Glide.with(getActivity()).load(url).thumbnail(0.1f).into(imageView);
            Picasso.with(this.getContext())
                    .load(url)
                    //.stableKey(mDataset.get(position).getUrl())
                    //.resize(200, 200)
                    .placeholder(R.drawable.ic_hourglass_black)
                    .error(R.drawable.ic_default)
                    .centerCrop()
                    //.tag(holder)
                    .into(imageView);

            return rootView;
        }

    }
}
