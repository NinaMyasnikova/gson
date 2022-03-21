package com.myasnikova.centralbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    private static final String URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    private ListView listView;
    List<Valute> valute;
    Root root;
    ListAdapter listAdapter;
    Gson gson;
    Button btn_update;
    TextView tV_today;
    TextView tV_updated;
    private SharedPreferences sharedPref;
    String data;
    String date_update;
    SeekBar seekbar_autoUpdate;
    Switch switch_autoUpdate;
    Boolean autoUpdate_bool;
    int autoUpdate_int;
    TextView tV_autoUpdate;
    Timer mTimer;
    EditText edittxt_ru;
    TextView tV_ru;
    Spinner spinner_valute;
    TextView tV_valute;
    SpinnerAdapter spinnerAdapter;
    int ru=1;
    Valute valute_item;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());date();
        tV_today.setText(date);

        if (sharedPref.contains(Constants.SharePref.KEY_PREF + "_" + "data"))
        {
            data=sharedPref.getString(Constants.SharePref.KEY_PREF + "_" + "data","no app selected");
            root = gson. fromJson (data, Root.class);
            date_update=sharedPref.getString(Constants.SharePref.KEY_PREF + "_" + "date_update","no app selected");
            tV_updated.setText(date_update);
            list_init();
            adapter_init();
            autoUpdate_bool=Boolean.valueOf(sharedPref.getString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_bool","no app selected"));
            autoUpdate_int=Integer.parseInt(sharedPref.getString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_int","no app selected"));

            if (autoUpdate_bool)
            {
                switch_autoUpdate.setChecked(true);
                seekbar_autoUpdate.setVisibility(View.VISIBLE);
                tV_autoUpdate.setVisibility(View.VISIBLE);
                startAlarm();
            }
            else if (!autoUpdate_bool)
            {
                switch_autoUpdate.setChecked(false);
                seekbar_autoUpdate.setVisibility(View.GONE);
                tV_autoUpdate.setVisibility(View.GONE);
            }
            tV_autoUpdate.setText(String.valueOf(autoUpdate_int));
            seekbar_autoUpdate.setProgress(autoUpdate_int);
        }
        else
        {
            SimpleDateFormat sdf_update = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
            date_update = sdf_update.format(Calendar.getInstance().getTime());date();
            tV_updated.setText(date_update);
            switch_autoUpdate.setChecked(false);
            seekbar_autoUpdate.setVisibility(View.GONE);
            tV_autoUpdate.setVisibility(View.GONE);
            autoUpdate_int=20;
            fromURL();
            root = gson. fromJson (data, Root.class);
            list_init();
            adapter_init();
        }

        btn_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                update();
            }
        });

        switch_autoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (switch_autoUpdate.isChecked())
                {
                    autoUpdate_bool=true;
                    seekbar_autoUpdate.setVisibility(View.VISIBLE);
                    tV_autoUpdate.setVisibility(View.VISIBLE);
                    if (mTimer==null)
                    {
                        mTimer = new Timer();
                    }
                    startAlarm();
                }
                else
                {
                    autoUpdate_bool=false;
                    seekbar_autoUpdate.setVisibility(View.GONE);
                    tV_autoUpdate.setVisibility(View.GONE);
                    cancelTimer();
                }
            }
        });

        seekbar_autoUpdate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tV_autoUpdate.setText(String.valueOf(progress));
                autoUpdate_int= (int) Long.parseLong(String.valueOf(progress));
                if (mTimer!=null)
                {
                    cancelTimer();
                    mTimer = new Timer();
                }
                startAlarm();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        edittxt_ru.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                ru=Integer.parseInt((s.toString()));
                convertor();
            }
        });

        tV_ru.setText(R.string.ru);

        adapterSpinner_init();
        spinner_valute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                valute_item=valute.get(position);
                convertor();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    public void init()
    {
        listView=findViewById(R.id.listView);
        valute=new ArrayList<>();
        gson=new Gson();
        valute_item=new Valute();

        btn_update=findViewById(R.id.btn_update);
        tV_today=findViewById(R.id.tV_today);
        tV_updated=findViewById(R.id.tV_updated);

        sharedPref=getSharedPreferences(Constants.SharePref.NAME_PREF, Context.MODE_PRIVATE);

        seekbar_autoUpdate=findViewById(R.id.seekBar_autoUpdate);
        switch_autoUpdate=findViewById(R.id.switch_autoUpdate);
        tV_autoUpdate=findViewById(R.id.tV_autoUpdate);
        mTimer = new Timer();
        edittxt_ru=findViewById(R.id.edittxt_ru);
        tV_ru=findViewById(R.id.tV_ru);
        spinner_valute=findViewById(R.id.spinner_valute);
        tV_valute=findViewById(R.id.tV_valute);
    }

    public void list_init()
    {
        for (Iterator<Map.Entry<String, Valute>> it = root.getValute().entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<String, Valute> entry = it.next();
            String t=entry.getKey();
            Valute valuteItem=new Valute();
            valuteItem.setID(entry.getValue().getID());
            valuteItem.setNumCode(entry.getValue().getNumCode());
            valuteItem.setCharCode(entry.getValue().getCharCode());
            valuteItem.setNominal(entry.getValue().getNominal());
            valuteItem.setName(entry.getValue().getName());
            valuteItem.setValue(entry.getValue().getValue());
            valuteItem.setPrevious(entry.getValue().getPrevious());

            valute.add(valuteItem);
        }
    }

    public void adapter_init()
    {
        listAdapter= new ListAdapter(getApplicationContext(), R.layout.list_item, valute);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged ();
    }

    public void adapterSpinner_init()
    {
        spinnerAdapter= new SpinnerAdapter(getApplicationContext(), R.layout.spinner_item, valute);
        spinner_valute.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged ();
    }

    public String date()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        String date = sdf.format(Calendar.getInstance().getTime());
        return date;
    }

    public void update()
    {
        SimpleDateFormat sdf_up = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        date_update = sdf_up.format(Calendar.getInstance().getTime());
        date();
        tV_updated.setText(date_update);
        fromURL();
        list_init();
        adapter_init();
        adapterSpinner_init();
        convertor();
    }

    public void fromURL()
    {
        Thread thread=new Thread(new Runnable()
        {
            public void run()
            {
                data = null;
                try
                {
                    data = getContent(URL);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch(InterruptedException e)
        {

        }
    }

    private String getContent(String path) throws IOException
    {
        BufferedReader reader=null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try
        {
            URL url=new URL(path);
            connection =(HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf=new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null)
            {
                buf.append(line).append("\n");
            }
            return(buf.toString());
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
            if (stream != null)
            {
                stream.close();
            }
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    private void startAlarm()
    {
        mTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                data = null;
                fromURL();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        list_init();
                        adapter_init();
                        convertor();
                        SimpleDateFormat sdf_up = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                        date_update = sdf_up.format(Calendar.getInstance().getTime());date();
                        tV_updated.setText(date_update);
                    }
                });
            }
            }, 0
            , autoUpdate_int*60*1000);
    }

    private void cancelTimer()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void save()
    {
        SharedPreferences.Editor editor=sharedPref.edit();
        if (sharedPref.contains(Constants.SharePref.KEY_PREF + "_" + "data"))
        {
            editor.remove(Constants.SharePref.KEY_PREF + "_" + "data");
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "data", String.valueOf(data));
            editor.remove(Constants.SharePref.KEY_PREF + "_" + "date_update");
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "date_update", String.valueOf(date_update));
            editor.remove(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_bool");
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_bool", String.valueOf(autoUpdate_bool));
            editor.remove(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_int");
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_int", String.valueOf(autoUpdate_int));
            editor.apply();
        }
        else
        {
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "data", String.valueOf(data));
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "date_update", String.valueOf(date_update));
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_bool", String.valueOf(autoUpdate_bool));
            editor.putString(Constants.SharePref.KEY_PREF + "_" + "autoUpdate_int", String.valueOf(autoUpdate_int));
            editor.apply();
        }
    }

    public void convertor()
    {
        Double value=valute_item.getValue();
        Double nominal=Double.valueOf(String.valueOf(valute_item.getNominal()));

        Double temp=(ru/value)/nominal;
        int iValue=(int)(temp*1000);
        double dValue=temp*1000;
        if (dValue-iValue>=0.5)
        {
            iValue+=1;
        }
        dValue=(double) iValue;
        Double val=dValue/1000;
        tV_valute.setText(String.valueOf(val));
    }

    @Override
    protected void onDestroy()
    {
        save();
        super.onDestroy();
    }
}