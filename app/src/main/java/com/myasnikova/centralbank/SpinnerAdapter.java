package com.myasnikova.centralbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Valute>
{
    private List<Valute> mainList;//доп. список чтобы можно было обратиться к appList, кот. передается через конструктор
    private List<ViewHolder>listViewHolder;//список с пом. кот. м.б. сохранять чекбоксы
    private SharedPreferences sharedPrefNotification;
    List<String> listNotification;

    public SpinnerAdapter(@NonNull Context context, int resource, List<Valute> appList)
    {
        super(context, resource, appList);
        mainList=appList;
        listViewHolder=new ArrayList<>();
        listNotification=new ArrayList<>();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) //если элемент создается в первый раз
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, null, false);//надувание
            viewHolder.tV_itemSpinner = convertView.findViewById(R.id.tV_itemSpinner); //сохранение в ViewHolder
            convertView.setTag(viewHolder);//сохранение в convertView в виде tag(ViewHolder не может сохранять в себе)
            listViewHolder.add(viewHolder);//появился доступ к viewHolder
            Log.d("MyLog1", "**** getView");
            Log.d("MyLog3", mainList.get(position).getName());
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d("MyLog1", "viewHolder *");
        }
        viewHolder.tV_itemSpinner.setText(String.valueOf(mainList.get(position).getName()));
        return convertView;
    }

    @NonNull
    @Override //переопределяется чтобы не использовать стандартный шаблон
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view;
        view =  View.inflate(parent.getContext(), R.layout.spinner_item, null);
        TextView tV_itemSpinner = (TextView) view.findViewById(R.id.tV_itemSpinner);
        tV_itemSpinner.setText(String.valueOf(mainList.get(position).getName()));

        return view;
    }

    static class ViewHolder //класс для хранения элементов списка, когда те скрываются при прокручивании
    {
        TextView tV_itemSpinner;
    }

}
