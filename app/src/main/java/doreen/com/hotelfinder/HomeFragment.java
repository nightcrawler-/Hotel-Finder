package doreen.com.hotelfinder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import doreen.com.hotelfinder.content.PlaceContract;

/**
 * Created by Doreen on 3/22/2015.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private GridView list;
    private ListAdapter adapter;
    private ListCursorAdapter adapter2;
    protected static ImageLoader imageLoader = ImageLoader.getInstance();
    static DisplayImageOptions options;

    private static final String[] PLACES_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            PlaceContract.PlacesEntry.TABLE_NAME,
            PlaceContract.PlacesEntry.COLUMN_PHOTO_REFERENCE,
            PlaceContract.PlacesEntry.COLUMN_PLACE_ID,
            PlaceContract.PlacesEntry.COLUMN_ICON


    };
    private int PLACES_LOADER = 0;

    public HomeFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        list = (GridView) rootView.findViewById(R.id.gridView);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                        //.showImageForEmptyUri(R.drawable.w_empty)
                .showImageOnFail(R.drawable.loading_failed)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

        //FetchIntentService.startActionFetch(this, null, null);

        adapter = new ListAdapter(getActivity());
        adapter2 = new ListCursorAdapter(getActivity());

        list.setAdapter(adapter2);

        // new FetchTask().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                ((Callback) getActivity())
                        .onItemSelected(adapter2.getPlaceId(position));

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri placesUri = PlaceContract.PlacesEntry.buildPlacesUri(0);
        ;
        return new CursorLoader(
                getActivity(),
                placesUri,
                PLACES_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter2.setCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter2.setCursor(null);

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String placeId);
    }

    @Deprecated
    public class FetchTask extends AsyncTask<Object, Object, JSONArray> {
        @Override
        protected JSONArray doInBackground(Object... params) {

            try {
                return Backbone.getData();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray data) {
            super.onPostExecute(data);
            if (data != null) {
                adapter.setData(data);

                Backbone.insertData(getActivity(), data);


            } else {
                Log.e("HOTEL", "something blew up");
                Toast.makeText(getActivity(), "request failed", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Deprecated
    public static class ListAdapter extends BaseAdapter {
        private Context context;
        private JSONArray data;


        public ListAdapter(Context context) {
            this.context = context;
        }

        public void setData(JSONArray data) {
            this.data = data;
            this.notifyDataSetChanged();
        }

        @Override
        public JSONObject getItem(int position) {
            try {
                return data.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.card_content, null);
                convertView.setTag(holder);

                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.icon = (ImageView) convertView.findViewById(R.id.imageView2);
                holder.title = (TextView) convertView.findViewById(R.id.textView);
                holder.source = (TextView) convertView.findViewById(R.id.textView2);
                holder.time = (TextView) convertView.findViewById(R.id.textView3);
                holder.tag = (TextView) convertView.findViewById(R.id.textView4);
                holder.bottomPadding = convertView.findViewById(R.id.bottomPad);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //hack for the padding at the bottom of the list
            if (position + 1 == getCount())
                holder.bottomPadding.setVisibility(View.VISIBLE);
            else
                holder.bottomPadding.setVisibility(View.GONE);


            try {
                holder.title.setText(getItem(position).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Log.i("HOTEL", getItem(position).toString(1));
                String firstPhotoRef = getItem(position).getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                imageLoader.displayImage(Backbone.BASE_PHOTO_URL + firstPhotoRef, holder.imageView, options);
                holder.imageView.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                holder.imageView.setVisibility(View.GONE);

            }

            try {
                imageLoader.displayImage(getItem(position).getString("icon"), holder.icon, options);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return convertView;
        }

        @Override
        public int getCount() {
            return data != null ? data.length() : 0;
        }


        private class ViewHolder {
            ImageView imageView, icon;
            TextView title, source, time, tag;
            View bottomPadding;
        }
    }

    public static class ListCursorAdapter extends BaseAdapter {
        private Context context;
        private Cursor cursor;
        private String placeId;

        public String getPlaceId(int position) {
            cursor.moveToPosition(position);
            return cursor.getString(cursor.getColumnIndex(PlaceContract.PlacesEntry.COLUMN_PLACE_ID));
        }


        public ListCursorAdapter(Context context) {
            this.context = context;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
            this.notifyDataSetChanged();
        }



        @Override
        public Cursor getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.card_content, null);
                convertView.setTag(holder);

                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.icon = (ImageView) convertView.findViewById(R.id.imageView2);
                holder.title = (TextView) convertView.findViewById(R.id.textView);
                holder.source = (TextView) convertView.findViewById(R.id.textView2);
                holder.time = (TextView) convertView.findViewById(R.id.textView3);
                holder.tag = (TextView) convertView.findViewById(R.id.textView4);
                holder.bottomPadding = convertView.findViewById(R.id.bottomPad);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            cursor.moveToPosition(position);
            //hack for the padding at the bottom of the list
            if (position + 1 == getCount())
                holder.bottomPadding.setVisibility(View.VISIBLE);
            else
                holder.bottomPadding.setVisibility(View.GONE);


            holder.title.setText(cursor.getString(cursor.getColumnIndex(PlaceContract.PlacesEntry.COLUMN_NAME)));
            String firstPhotoRef = cursor.getString(cursor.getColumnIndex(PlaceContract.PlacesEntry.COLUMN_PHOTO_REFERENCE));


            if (firstPhotoRef == null || firstPhotoRef.length() == 0)
                holder.imageView.setVisibility(View.GONE);
            else {
                imageLoader.displayImage(Backbone.BASE_PHOTO_URL + firstPhotoRef, holder.imageView, options);
                holder.imageView.setVisibility(View.VISIBLE);
            }

            imageLoader.displayImage(cursor.getString(cursor.getColumnIndex(PlaceContract.PlacesEntry.COLUMN_ICON)), holder.icon, options);


            return convertView;
        }

        @Override
        public int getCount() {
            return cursor != null ? cursor.getCount() : 0;
        }


        private class ViewHolder {
            ImageView imageView, icon;
            TextView title, source, time, tag;
            View bottomPadding;
        }
    }
}
