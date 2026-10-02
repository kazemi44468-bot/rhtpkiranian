package ir.rhtpkiranian.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout content;
    private TextView title;
    private final int ink=Color.rgb(24,43,50), muted=Color.rgb(105,123,128), accent=Color.rgb(8,127,116), gold=Color.rgb(201,155,61), bg=Color.rgb(245,248,248);
    private final MockApi api=new MockApi();

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        buildShell();
        showDashboard();
    }

    private void buildShell(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(bg);
        LinearLayout head=new LinearLayout(this); head.setOrientation(LinearLayout.HORIZONTAL); head.setGravity(Gravity.CENTER_VERTICAL); head.setPadding(dp(18),dp(14),dp(18),dp(10));
        TextView logo=label("◆",22,Color.WHITE); logo.setGravity(Gravity.CENTER);
        GradientDrawable lg=round(accent,14); logo.setBackground(lg); head.addView(logo,new LinearLayout.LayoutParams(dp(46),dp(46)));
        LinearLayout ht=new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL); ht.setPadding(dp(12),0,0,0);
        title=label("مرکز مدیریت",18,ink); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        TextView sub=label("راهکار هوشمند تأمین، پشتیبانی و خدمات ایرانیان",9,muted); ht.addView(title); ht.addView(sub);
        head.addView(ht,new LinearLayout.LayoutParams(0,-2,1));
        TextView bell=label("●",18,gold); bell.setGravity(Gravity.CENTER); head.addView(bell,new LinearLayout.LayoutParams(dp(38),dp(38)));
        root.addView(head);

        ScrollView scroll=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16),dp(6),dp(16),dp(92)); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout nav=new LinearLayout(this); nav.setGravity(Gravity.CENTER); nav.setPadding(dp(8),dp(7),dp(8),dp(7)); nav.setBackgroundColor(Color.WHITE);
        String[] ns={"داشبورد","پرونده‌ها","شبکه","گزارش‌ها"}; for(String n:ns){ TextView v=label(n,10,muted); v.setGravity(Gravity.CENTER); v.setOnClickListener(x->{if(n.equals("داشبورد"))showDashboard();else if(n.equals("پرونده‌ها"))showRecords();else if(n.equals("شبکه"))showNetwork();else showReports();}); nav.addView(v,new LinearLayout.LayoutParams(0,dp(54),1));}
        root.addView(nav);
        setContentView(root);
    }

    private void showDashboard(){
        title.setText("مرکز مدیریت"); content.removeAllViews();
        content.addView(sectionTitle("نمای کلی سامانه","وضعیت لحظه‌ای عملیات"));
        LinearLayout k=new LinearLayout(this); k.setOrientation(LinearLayout.HORIZONTAL);
        k.addView(metric("پرونده باز",api.open(),accent),new LinearLayout.LayoutParams(0,dp(96),1));
        k.addView(space(8,1)); k.addView(metric("فوری",api.urgent(),gold),new LinearLayout.LayoutParams(0,dp(96),1)); content.addView(k);
        content.addView(gap(10));
        LinearLayout k2=new LinearLayout(this); k2.setOrientation(LinearLayout.HORIZONTAL);
        k2.addView(metric("تأمین‌کننده",api.suppliers(),ink),new LinearLayout.LayoutParams(0,dp(96),1)); k2.addView(space(8,1)); k2.addView(metric("پروژه",api.projects(),ink),new LinearLayout.LayoutParams(0,dp(96),1)); content.addView(k2);
        content.addView(gap(18));
        content.addView(sectionTitle("صف اقدام مدیر","اولویت‌های جاری"));
        for(Record r:api.records()) if(!r.done) content.addView(queue(r));
        content.addView(gap(18));
        content.addView(sectionTitle("چرخه عملیات","از ثبت تا اجرا"));
        String[] steps={"ثبت","بررسی","تکمیل","تأیید","اجرا"}; LinearLayout flow=new LinearLayout(this); flow.setOrientation(LinearLayout.HORIZONTAL);
        for(String s:steps){TextView v=label(s,9,accent);v.setGravity(Gravity.CENTER);v.setBackground(round(Color.WHITE,10));flow.addView(v,new LinearLayout.LayoutParams(0,dp(48),1));} content.addView(flow);
    }

    private void showRecords(){ title.setText("پرونده‌ها"); content.removeAllViews(); content.addView(sectionTitle("مدیریت پرونده‌ها","درخواست، تأمین‌کننده و فرصت پروژه"));
        for(Record r:api.records()) content.addView(recordCard(r));
    }
    private void showNetwork(){ title.setText("شبکه تأمین"); content.removeAllViews(); content.addView(sectionTitle("شبکه همکاران","تأمین‌کنندگان و ظرفیت عملیاتی"));
        content.addView(infoCard("۲۸","تأمین‌کننده و شریک ثبت‌شده","ظرفیت نمونه شبکه")); content.addView(infoCard("۱۲","پرونده فعال","در انتظار اقدام یا ارجاع")); content.addView(infoCard("۸","حوزه تخصصی","پوشش خدمات و تأمین"));
    }
    private void showReports(){ title.setText("گزارش‌ها"); content.removeAllViews(); content.addView(sectionTitle("گزارش مدیریتی","خلاصه عملکرد سامانه"));
        String[] a={"درخواست‌ها","تأمین‌کنندگان","پروژه‌ها"}; int[] n={api.kind("request"),api.kind("supplier"),api.kind("project")};
        for(int i=0;i<a.length;i++){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(12),dp(12),dp(12),dp(12));row.setBackground(round(Color.WHITE,12)); TextView t=label(a[i],10,ink);row.addView(t,new LinearLayout.LayoutParams(dp(105),-2)); ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(10);p.setProgress(Math.min(10,n[i]));row.addView(p,new LinearLayout.LayoutParams(0,dp(7),1)); TextView num=label(fa(n[i]),11,accent);num.setPadding(dp(10),0,0,0);row.addView(num);content.addView(row);content.addView(gap(8));}
    }

    private View queue(Record r){LinearLayout x=base();x.setPadding(dp(13),dp(12),dp(13),dp(12));TextView dot=label("●",12,r.priority.equals("فوری")?gold:accent);x.addView(dot,new LinearLayout.LayoutParams(dp(22),-2));LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.addView(label(r.title,11,ink));c.addView(label(r.id+" · "+r.status+" · "+r.owner,8,muted));x.addView(c,new LinearLayout.LayoutParams(0,-2,1));TextView p=label(r.priority,8,r.priority.equals("فوری")?gold:muted);x.addView(p);return x;}
    private View recordCard(Record r){LinearLayout x=base();x.setPadding(dp(14),dp(13),dp(14),dp(13));LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.addView(label(r.title,12,ink));c.addView(label(r.kind+"  ·  "+r.id,8,muted));x.addView(c,new LinearLayout.LayoutParams(0,-2,1));TextView s=label(r.status,8,accent);s.setGravity(Gravity.CENTER);s.setPadding(dp(7),dp(5),dp(7),dp(5));s.setBackground(round(Color.rgb(235,246,243),9));x.addView(s);return x;}
    private View metric(String l,int n,int c){LinearLayout x=base();x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(13),dp(12),dp(13),dp(10));x.addView(label(l,9,muted));TextView v=label(fa(n),24,c);v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(v);return x;}
    private View infoCard(String n,String a,String b){LinearLayout x=base();x.setPadding(dp(15),dp(14),dp(15),dp(14));TextView num=label(n,25,accent);num.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(num);x.addView(label(a,11,ink));x.addView(label(b,8,muted));return x;}
    private TextView sectionTitle(String a,String b){TextView v=label(a+"\n"+b,15,ink);v.setPadding(dp(2),dp(8),0,dp(8));return v;}
    private TextView label(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);return v;}
    private LinearLayout base(){LinearLayout x=new LinearLayout(this);x.setGravity(Gravity.CENTER_VERTICAL);x.setBackground(round(Color.WHITE,14));x.setElevation(dp(1));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(8));x.setLayoutParams(p);return x;}
    private View gap(int h){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return s;}
    private View space(int w,int h){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(dp(w),dp(h)));return s;}
    private GradientDrawable round(int c,int r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(r));g.setStroke(dp(1),Color.rgb(225,233,233));return g;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private String fa(int n){String s=String.valueOf(n);return s.replace('0','۰').replace('1','۱').replace('2','۲').replace('3','۳').replace('4','۴').replace('5','۵').replace('6','۶').replace('7','۷').replace('8','۸').replace('9','۹');}

    static class Record{String id,title,kind,status,owner,priority;boolean done;Record(String i,String t,String k,String s,String o,String p,boolean d){id=i;title=t;kind=k;status=s;owner=o;priority=p;done=d;}}
    static class MockApi{
        List<Record> rs=Arrays.asList(
            new Record("REQ-۱۴۰۵-۰۰۱","تأمین تجهیزات پروژه توسعه","درخواست","در حال بررسی","واحد تأمین","فوری",false),
            new Record("SUP-۱۴۰۵-۰۰۷","شبکه تأمین تجهیزات صنعتی","supplier","تکمیل اطلاعات","مرکز عملیات","عادی",false),
            new Record("PRJ-۱۴۰۵-۰۰۳","فرصت پروژه زیرساخت هوشمند","project","تأیید / آماده اجرا","مدیریت پروژه","فوری",false),
            new Record("REQ-۱۴۰۵-۰۰۲","درخواست خدمات پشتیبانی","درخواست","مختومه","واحد خدمات","عادی",true));
        List<Record> records(){return rs;} int open(){int n=0;for(Record r:rs)if(!r.done)n++;return n;} int urgent(){int n=0;for(Record r:rs)if(r.priority.equals("فوری"))n++;return n;} int suppliers(){return 28;} int projects(){return 8;} int kind(String k){int n=0;for(Record r:rs)if(r.kind.equalsIgnoreCase(k))n++;return n;}
    }
}
