package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {

    int total_time=0;
    String  dis_trav = "";

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;


        String temp_name;

        try {

            jRoutes = jObject.getJSONArray("routes");
            total_time=0;
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){



                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    try
                    {
                        dis_trav = ( (JSONObject)jLegs.get(j)).getJSONObject("distance").getString("text");
                        Log.e("dis_trav",dis_trav);
                    }catch (Exception e)
                    {
                        Log.e("distance trav ex",e.toString());
                    }

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
//                        total_time = total_time+;
                        temp_name=(String)((JSONObject)((JSONObject)jSteps.get(k)).get("duration")).get("text");

                        if(temp_name.contains("hours"))
                        {
                            total_time =total_time+Integer.parseInt(temp_name.substring(0,temp_name.indexOf("h")-1))*60+Integer.parseInt(temp_name.substring(temp_name.indexOf("s")+2,temp_name.indexOf("m")-1));


                        }else if(temp_name.contains("hour"))
                        {
                            total_time =total_time+Integer.parseInt(temp_name.substring(0,temp_name.indexOf("h")-1))*60+Integer.parseInt(temp_name.substring(temp_name.indexOf("r")+2,temp_name.indexOf("m")-1));

                        }else
                        {
                            total_time=total_time+Integer.parseInt(temp_name.replaceAll("[^0-9]", ""));
                        }

//
//                        Log.e("temp_name",temp_name);
//                        Log.e("total_time",total_time+"");


                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public String get_total_time() {

        int formated_time;

        if(total_time>60)
        {
            formated_time = total_time / 60;
            return formated_time+" hrs";

        }else
        {
            return total_time+" mins";
        }

    }


    public String get_dist_trav() {

        return  dis_trav;

    }
}