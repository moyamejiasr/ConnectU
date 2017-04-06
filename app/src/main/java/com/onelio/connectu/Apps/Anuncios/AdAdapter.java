package com.onelio.connectu.Apps.Anuncios;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.R;

import java.util.List;

public class AdAdapter extends BaseAdapter {

    private final Context mContext;
    List<AdList> names;
    Activity activity;
    int owidth;
    int oheight;
    int resPorcent = 220;
    int increased = 0;
    static boolean loaded = true;

    // 1
    public AdAdapter(Context context, List<AdList> names, Activity activity) {
        this.mContext = context;
        this.names = names;
        this.activity = activity;
    }

    // 2
    @Override
    public int getCount() {
        return names.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.list_ad, null);
        }

        // 3
        final TextView texto = (TextView)convertView.findViewById(R.id.texto);
        final TextView titulo1 = (TextView)convertView.findViewById(R.id.titulo1);

        SpannableString content = new SpannableString(names.get(position).getTitulo1() + " " + names.get(position).getTitulo2());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        titulo1.setText(content);
        texto.setText(names.get(position).getTexto());
        final ImageView watch_IMG = (ImageView)convertView.findViewById(R.id.watch_IMG);
        final TextView watch_Text = (TextView)convertView.findViewById(R.id.watch_Text);
        if(names.get(position).getState()) {
            titulo1.setTextColor(Color.parseColor("#00695C"));
            watch_IMG.setImageResource(R.drawable.watch);
            watch_Text.setText("WATCH");
            watch_Text.setTextColor(Color.parseColor("#00695C"));
        } else {
            titulo1.setTextColor(Color.parseColor("#000000"));
            watch_IMG.setImageResource(R.drawable.watched);
            watch_Text.setText("UNWATCH");
            watch_Text.setTextColor(Color.parseColor("#000000"));
        }

        CardView card = (CardView)convertView.findViewById(R.id.titleCard);
        final RelativeLayout adLayout = (RelativeLayout)convertView.findViewById(R.id.adLayout);
        final LinearLayout lastLayout = (LinearLayout)convertView.findViewById(R.id.lastLayout);
        if (owidth != 0) {
            adLayout.getLayoutParams().width = owidth;
            adLayout.getLayoutParams().height = oheight;
            loaded = true;
            adLayout.requestLayout();
        } else {
            increased = resPorcent;
            increased = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, increased, activity.getResources().getDisplayMetrics());
            owidth = adLayout.getWidth();
            oheight = adLayout.getHeight();
        }
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Done smoother
                if (loaded) {
                    loaded = false;
                    if (lastLayout.getHeight() == increased) {
                        collapse(adLayout, 200, oheight, owidth);
                    } else {
                        expand(adLayout, 200, increased, lastLayout.getWidth());
                    }
                }
            }
        });

        LinearLayout view_Button = (LinearLayout)convertView.findViewById(R.id.view_Button);
        view_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.announce = names.get(position);
                activity.startActivity(new Intent(activity, AnunciosViewActivity.class));
            }
        });

        LinearLayout watch_Button = (LinearLayout)convertView.findViewById(R.id.watch_Button);
        watch_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = "idanuncio=" + names.get(position).getId();
                if (names.get(position).getState()) {
                    names.get(position).setState(false);
                    titulo1.setTextColor(Color.parseColor("#000000"));
                    watch_IMG.setImageResource(R.drawable.watched);
                    watch_Text.setText("UNWATCH");
                    watch_Text.setTextColor(Color.parseColor("#000000"));
                    UAWebService.HttpWebPostRequest(activity, UAWebService.ANUNCIO_L, id, new UAWebService.WebCallBack() {
                        @Override
                        public void onNavigationComplete(boolean isSuccessful, String body) {
                            final String state = body;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (state.equals("True")) {
                                        Common.needMainReload = true;
                                        Toast.makeText(activity.getBaseContext(), "Marcado como leido!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity.getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    names.get(position).setState(true);
                    titulo1.setTextColor(Color.parseColor("#00695C"));
                    watch_IMG.setImageResource(R.drawable.watch);
                    watch_Text.setText("WATCH");
                    watch_Text.setTextColor(Color.parseColor("#00695C"));
                    UAWebService.HttpWebPostRequest(activity, UAWebService.ANUNCIO_NL, id, new UAWebService.WebCallBack() {
                        @Override
                        public void onNavigationComplete(boolean isSuccessful, String body) {
                            final String state = body;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (state.equals("True")) {
                                        Common.needMainReload = true;
                                        Toast.makeText(activity.getBaseContext(), "Marcado como no leido!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity.getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });


        return convertView;
    }

    public static void expand(final View v, int duration, int targetHeight, int targetWidth) {
        //Valores Iniciales
        int prevHeight  = v.getHeight();
        int prevWidth  = v.getWidth();

        //Ajustamos el lado
        ValueAnimator valueAnimatorWidth = ValueAnimator.ofInt(prevWidth, targetWidth);
        valueAnimatorWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimatorWidth.setInterpolator(new DecelerateInterpolator());
        valueAnimatorWidth.setDuration(duration);
        //Ajustamos el alto
        final ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimatorHeight.setInterpolator(new DecelerateInterpolator());
        valueAnimatorHeight.setDuration(duration);
        valueAnimatorHeight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //ended
                loaded = true;
            }
        });
        valueAnimatorWidth.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // done
                valueAnimatorHeight.start();
            }
        });
        valueAnimatorWidth.start();
    }

    public static void collapse(final View v, int duration, int targetHeight, int targetWidth) {
        //Valores Iniciales
        int prevHeight  = v.getHeight();
        int prevWidth  = v.getWidth();

        //Ajustamos el alto
        ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimatorHeight.setInterpolator(new DecelerateInterpolator());
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimatorHeight.setInterpolator(new DecelerateInterpolator());
        valueAnimatorHeight.setDuration(duration);
        //Ajustamos el lado
        final ValueAnimator valueAnimatorWidth = ValueAnimator.ofInt(prevWidth, targetWidth);
        valueAnimatorWidth.setInterpolator(new DecelerateInterpolator());
        valueAnimatorWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimatorWidth.setInterpolator(new DecelerateInterpolator());
        valueAnimatorWidth.setDuration(duration);
        valueAnimatorWidth.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //ended
                loaded = true;
            }
        });
        valueAnimatorHeight.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // done
                valueAnimatorWidth.start();
            }
        });
        valueAnimatorHeight.start();
    }

}

