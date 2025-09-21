package com.example.famouspaintingwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.io.InputStream;

public class PaintingWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        new AsyncTask<Void, Void, Painting>() {
            @Override
            protected Painting doInBackground(Void... voids) {
                return getDailyPainting(context);
            }

            @Override
            protected void onPostExecute(Painting painting) {
                if (painting != null) {
                    try {
                        Bitmap bitmap = Glide.with(context)
                                .asBitmap()
                                .load(painting.image)
                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();

                        views.setImageViewBitmap(R.id.widget_image, bitmap);

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("painting_id", painting.id);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context, 0, intent,
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private static Painting getDailyPainting(Context context) {
        try {
            InputStream is = context.getAssets().open("paintings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Painting[] paintings = gson.fromJson(json, Painting[].class);

            long day = System.currentTimeMillis() / (1000 * 60 * 60 * 24);
            int index = (int) (day % paintings.length);

            return paintings[index];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Painting getPaintingById(Context context, int id) {
        try {
            InputStream is = context.getAssets().open("paintings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Painting[] paintings = gson.fromJson(json, Painting[].class);

            for (Painting p : paintings) {
                if (p.id == id) return p;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
