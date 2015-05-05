package doreen.com.hotelfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Doreen on 3/22/2015.
 */
public class DetailFragment extends Fragment {

    private TextView name, number, extra, type;
    private ImageView image, icon;
    protected static ImageLoader imageLoader = ImageLoader.getInstance();
    static DisplayImageOptions options;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_content_details, container, false);

        image = (ImageView) rootView.findViewById(R.id.imageView);
        icon = (ImageView) rootView.findViewById(R.id.imageView2);
        name = (TextView) rootView.findViewById(R.id.textView);
        number = (TextView) rootView.findViewById(R.id.textView2);
        extra = (TextView) rootView.findViewById(R.id.textView3);
        type = (TextView) rootView.findViewById(R.id.textView4);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                        //.showImageForEmptyUri(R.drawable.w_empty)
                .showImageOnFail(R.drawable.loading_failed)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();

        String placeId = getArguments().getString("place_id");

        if (placeId != null) {
            new FetchDetailsTask().execute(placeId);

        }

        return rootView;
    }

    private class FetchDetailsTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return Backbone.getPlaceDetails(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (jsonObject != null) {
                fillOut(jsonObject);
            } else
                Toast.makeText(getActivity(), "request failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void fillOut(final JSONObject data) {
        try {
            name.setText(data.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            number.setText(data.getString("international_phone_number"));
            number.setClickable(true);
            number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    try {
                        intent.setData(Uri.parse("tel:" + data.getString("international_phone_number")));
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            number.setText("number unavailable");

        }
        try {
            extra.setText(data.getString("website"));
            extra.setClickable(true);
            extra.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    try {
                        intent.setData(Uri.parse(data.getString("website")));
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            extra.setText("website unavailable");

        }


        try {
            JSONArray types = data.getJSONArray("types");
            String typesString = "#";
            for (int i = 0; i < types.length(); i++) {
                typesString += types.getString(i) + " ";
            }

            type.setText(typesString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            String firstPhotoRef = data.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            imageLoader.displayImage(Backbone.BASE_PHOTO_URL + firstPhotoRef, image, options);
            image.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
            image.setVisibility(View.GONE);

        }

        try {
            imageLoader.displayImage(data.getString("icon"), icon, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
