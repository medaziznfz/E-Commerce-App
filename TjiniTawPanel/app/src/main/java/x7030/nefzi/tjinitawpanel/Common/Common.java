package x7030.nefzi.tjinitawpanel.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import x7030.nefzi.tjinitawpanel.Model.DiscountModel;
import x7030.nefzi.tjinitawpanel.Model.OrderModel;
import x7030.nefzi.tjinitawpanel.Model.ShipperModel;
import x7030.nefzi.tjinitawpanel.R;

public class Common {
    public static final String PHARMACY_REF = "Pharmacie";
    public static String RESTAURANT_REF = "Restaurant" ;
    public static final String SHIPPER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static final String USER_REFERENCESER = "Shippers";
    public static final String DISCOUNT_REF = "Discount";
    public static final String TASKS_REF = "Tasks";
    public static ShipperModel currentShipper;
    public static DiscountModel discountSelected;
    public static OrderModel currentShippingOrder;
    public static int totalCommande;


    public static void setSpanStringColor(String welcome, String name, TextView textView, int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color),0,name.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder,TextView.BufferType.SPANNABLE);


    }

    public static String convertStatusToString(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "Mezel jdyd";
            case 1:
                return "f thniya";
            case 2:
                return "Saye weslet";
            case -1:
                return "Annuler";
            default:
                return "mana3refech";


        }
    }


    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while (index<len)
        {
            int b,shift=0,result=0;
            do {
                b=encoded.charAt(index++)-63;
                result |= (b & 0xff) << shift;
                shift += 5;
            }while (b >= 0x20);

            int dlat = (result & 1 ) != 0 ? ~(result >> 1):(result >> 1);
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double)lat/1E5)),((double)lng/1E5));
            poly.add(p);

        }
        return poly;
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude-end.latitude);
        double lng = Math.abs(begin.longitude-end.longitude);
        if (begin.latitude < end.latitude && begin.longitude<begin.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        else if (begin.latitude >= end.latitude && begin.longitude<begin.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat)))+90);
        else if (begin.latitude >= end.latitude && begin.longitude >= begin.longitude)
            return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
        else if (begin.latitude < end.latitude && begin.longitude >= begin.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng/lat)))+270);

        return 0;
    }

    public static String formatPrice(double price) {
        if (price !=0)
        {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalePrice = new StringBuilder(df.format(price)).toString();
            return finalePrice.replace(".",",");

        }
        else
            return "0,00";
    }
}
