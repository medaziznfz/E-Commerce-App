package x7030.nefzi.tjinitaw.Common;

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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import x7030.nefzi.tjinitaw.Model.AddonModel;
import x7030.nefzi.tjinitaw.Model.CategoryModel;
import x7030.nefzi.tjinitaw.Model.DiscountModel;
import x7030.nefzi.tjinitaw.Model.FoodModel;
import x7030.nefzi.tjinitaw.Model.PharmacieModel;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;
import x7030.nefzi.tjinitaw.Model.SizeModel;
import x7030.nefzi.tjinitaw.Model.UserModel;
import x7030.nefzi.tjinitaw.R;

public class Common {
    public static final String USER_REFERENCESER = "Users";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    //public static final String ADMIN_REF = "Admin";
    public static final String CONTACT_REF = "Contact";
    //public static final String BEST_DEAL_SHOP_REF = "BestDealsShop";
    public static final String OTHERS_REF = "OthersPub";
    public static final String OFFERS_PUB_REF = "OffersPub";
    public static final String PLATS_JOUR_REF = "PlatsJour";
    public static String RESTAURANT_REF = "Restaurant" ;
    public static final String CATEGORY_REF = "Category";
    public  static  final  int DEFAULT_COLUMN_COUNT = 0;
    public  static  final  int FULL_WIDTH_COLUMN = 1 ;
    public static final String PHARMACIE_REF = "Pharmacie";
    public static final String COMMENT_REF = "Comments";
    public static final String DISCOUNT_REF = "Discount";
    public static final String TASKS_REF = "Tasks";
    public static final String MAGASIN_REF = "Magasin";
    //public  static  final  String POPULAR_CATEGORY_REF = "MostPopular";
    //public  static  final  String BEST_DEAL_REF = "BestDeals";
    public static UserModel currentUser;
    public static RestaurantModel currentRestaurant;
    public static CategoryModel categorySelected;
    public static FoodModel selectedFood;
    public static DiscountModel discountApply;
    public static PharmacieModel currentPharmacie;


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

    public static Double calculateExtraPrice(SizeModel userSelectedSize, List<AddonModel> userSelectedAddon) {
        Double result = 0.0;
        if (userSelectedSize==null && userSelectedAddon == null)
            return 0.0;
        else if (userSelectedSize == null)
        {
            for (AddonModel addonModel : userSelectedAddon)
                result += addonModel.getPrice();
            return  result;

        }
        else if (userSelectedAddon == null)
        {
            return userSelectedSize.getPrice()*1.0;
        }
        else
        {
            result = userSelectedSize.getPrice()*1.0;
            for (AddonModel addonModel : userSelectedAddon)
                result += addonModel.getPrice();
            return  result;

        }



    }

    public static String createOrderNumber() {
        return new StringBuilder().append(System.currentTimeMillis()).append(Math.abs(new Random().nextInt())).toString();
    }

    public static String getDateOfWeek(int i) {
        switch (i)
        {
            case 1:
                return "La7ad";
            case 2:
                return "Lethnin";
            case 3:
                return "Etletha";
            case 4:
                return "Lereb3a";
            case 5:
                return "Lekhmis";
            case 6:
                return "Ejem3a";
            case 7:
                return "Esebt";
            default:
                return "Mana3refech";


        }
    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "la7adhat w njiwk";
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

    public static String getListAddon(List<AddonModel> addonModels) {
        StringBuilder result = new StringBuilder();
        for (AddonModel addonModel:addonModels)
        {
            result.append(addonModel.getName()).append(",");
        }
        return result.substring(0,result.length());
    }

    public static FoodModel findFoodInListById(CategoryModel categoryModel, String foodId) {
        if (categoryModel.getFoods() != null && categoryModel.getFoods().size() > 0)
        {
            for (FoodModel foodModel:categoryModel.getFoods())
                if (foodModel.getId().equals(foodId))
                    return foodModel;

            return  null;
        }
        else
            return null;
    }
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
    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }

    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "edmt_dev_eat_it_v2";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Eat It V2",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Eat It V2");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);



        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_star_black_24dp));
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id,notification);


    }






}
