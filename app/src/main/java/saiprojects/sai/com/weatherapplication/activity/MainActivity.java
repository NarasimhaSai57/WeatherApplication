package saiprojects.sai.com.weatherapplication.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import saiprojects.sai.com.weatherapplication.Model.ApiData;
import saiprojects.sai.com.weatherapplication.R;

public class MainActivity extends AppCompatActivity {

    TextView tv_palce,tv_date,tv_temp;
    RecyclerView rv_list;
    RequestQueue requestQueue;

    Context mContext;
    CustomProgressBar customProgressBar;

    DataListAdapter dataListAdapter;

    private String url = "http://api.apixu.com/v1/forecast.json?key=6ba82f1655f54824999110708191702&q=BENGALURU&days=3";
    ArrayList<ApiData> apiDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        customProgressBar = new CustomProgressBar(mContext);

        requestQueue = Volley.newRequestQueue(this);
        tv_palce = findViewById(R.id.textView3);
        tv_date = findViewById(R.id.textView2);
        tv_temp = findViewById(R.id.textView);
        rv_list = findViewById(R.id.rv_list);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(staggeredGridLayoutManager);
        rv_list.setNestedScrollingEnabled(false);


        ApiCalling();
    }


    private void ApiCalling()
    {

        if(!isNetworkConnected()){
            Toast toast = Toast.makeText(getApplicationContext(), "Please check internet Connection", Toast.LENGTH_SHORT); toast.show();
            return;
        }

        if (!customProgressBar.isProgressBarShowing()) {
            customProgressBar.showCustomDialog();
            customProgressBar.setCustomCancelable(false);
            customProgressBar.setCustomMessage("loading");
        }



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                customProgressBar.closeCustomDialog();
                Log.i("%%%","response --> "+response.toString());

                try {
                    JSONObject   sys = response.getJSONObject("location");
                    String place = sys.getString("name");


                    JSONObject main  = response.getJSONObject("current");
                    String temperature = main.getString("temp_c");
                    String date = main.getString("last_updated");


                    if(date!=null && !date.equalsIgnoreCase("")){
                        tv_date.setText("Today");
                    }

                    if(place!=null && !place.equalsIgnoreCase("")){
                        tv_palce.setText(place);
                    }

                    if(temperature!=null && !temperature.equalsIgnoreCase("")){
                        tv_temp.setText(temperature);
                    }




                    JSONObject forecast  = response.getJSONObject("forecast");
                    JSONArray forecastday = forecast.getJSONArray("forecastday");

                    for (int i = 0; i < forecastday.length(); i++)
                    {

                        JSONObject c = forecastday.getJSONObject(i);
                        ApiData apiDataOdj = new ApiData();
                        String arraydate = c.getString("date");
                        Log.i("%%%","response --> "+arraydate.toString());

                        String Converteddate = getCustomDateConvertion(arraydate,"yyyy-MM-dd","EEE");


                        apiDataOdj.setForcastdate(Converteddate);

                        JSONObject c1 = c.getJSONObject("day");
                        String maxtemp_c = c1.getString("maxtemp_c");
                        apiDataOdj.setForcastmaxtemp(maxtemp_c);

                        JSONObject img = c1.getJSONObject("condition");
                        String thumbnail = img.getString("text");
                        apiDataOdj.setThumb(thumbnail);


                        apiDataArrayList.add(apiDataOdj);
                    }


                    if(apiDataArrayList!=null && apiDataArrayList.size()>0)
                    {

                        rv_list.setHasFixedSize(true);
                        dataListAdapter = new DataListAdapter(apiDataArrayList);
                        rv_list.setAdapter(dataListAdapter);
                    }


                } catch (JSONException e) {
                    customProgressBar.closeCustomDialog();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customProgressBar.closeCustomDialog();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.ViewHolder>{
        ArrayList<ApiData> apiDataArrayList;
        public DataListAdapter(ArrayList<ApiData> apiDataArrayList) {
            this.apiDataArrayList = apiDataArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_display, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tv_date_forcast.setText(apiDataArrayList.get(position).getForcastdate());
            holder.tv_date_maxtemp.setText(apiDataArrayList.get(position).getForcastmaxtemp()+"c");
            holder.tv_text.setText(apiDataArrayList.get(position).getThumb());
        }

        @Override
        public int getItemCount() {
            return apiDataArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_date_forcast,tv_date_maxtemp;
            TextView tv_text;
            public ViewHolder(View itemView) {
                super(itemView);

                tv_date_forcast = itemView.findViewById(R.id.tv_date_forcast);
                tv_date_maxtemp = itemView.findViewById(R.id.tv_date_maxtemp);
                tv_text = itemView.findViewById(R.id.tv_text);
            }
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public static String getCustomDateConvertion(String dateValue, String fromDateFormate, String toDateFormate) {
        // 1arg value to be converted 2 arg date forrmate of current date 3 arg desired formate

        Log.i("DATETIME.", "getCustomDateConvertion: " + dateValue + " " + fromDateFormate + " " + toDateFormate);
        String result = "";
        if (dateValue!=null) {
            dateValue = dateValue.replace("Thu", "thu").replace("Tue", "tue");
            dateValue = dateValue.replace("T", " ");
        }
        try {
            if (toDateFormate.equalsIgnoreCase("DD/MM/YY")) {
                toDateFormate = "dd/MM/yy";
            } else if (toDateFormate.equalsIgnoreCase("DD/MM/YYYY")) {
                toDateFormate = "dd/MM/yyyy";

            } else if (toDateFormate.equalsIgnoreCase("DD/MMM/YY")) {
                toDateFormate = "dd/MMM/yy";
            } else if (toDateFormate.equalsIgnoreCase("DD-MM-YY")) {
                toDateFormate = "dd-MM-yy";
            } else if (toDateFormate.equalsIgnoreCase("DD-MM-YYYY")) {
                toDateFormate = "dd-MM-yyyy";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(fromDateFormate, Locale.US);
            //DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
            SimpleDateFormat csdf = new SimpleDateFormat(toDateFormate,Locale.US);
            Date currentDate = new Date();
            if (dateValue !=null) {
                dateValue = dateValue.replace("T", " ");
                currentDate = sdf.parse(dateValue);
                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                currentDate = c.getTime();
                result = csdf.format(currentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }


}
