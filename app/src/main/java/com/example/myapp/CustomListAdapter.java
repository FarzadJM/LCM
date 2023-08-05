package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomListAdapter extends BaseAdapter  implements Filterable {
    private final ArrayList<HashMap<String, String>> dataList;
    private ArrayList<HashMap<String, String>>  filteredItemList;
    private final LayoutInflater inflater;

    public CustomListAdapter(Context context, ArrayList<HashMap<String, String>> dataList) {
        this.dataList = dataList;
        this.filteredItemList = dataList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewHaircutCount = convertView.findViewById(R.id.textViewHaircutCount);
        TextView textViewLoyaltyNumber = convertView.findViewById(R.id.textViewLoyaltyNumber);

        HashMap<String, String> data = filteredItemList.get(position);
        textViewName.setText(data.get("name"));
        textViewHaircutCount.setText(data.get("haircut_count"));
        textViewLoyaltyNumber.setText(data.get("loyalty_number"));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList = dataList;
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (HashMap<String, String> row : dataList) {
                        for (String item : row.values()) {
                            if (item.toLowerCase().contains(filterPattern)) {
                                filteredList.add(row);
                                break;
                            }
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemList = (ArrayList<HashMap<String, String>>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}