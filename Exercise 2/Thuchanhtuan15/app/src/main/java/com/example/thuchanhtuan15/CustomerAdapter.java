package com.example.thuchanhtuan15;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomerAdapter extends ArrayAdapter<Customer> {
    private Context context;
    private ArrayList<Customer> customers;

    public CustomerAdapter(Context context, ArrayList<Customer> customers) {
        super(context, 0, customers);
        this.context = context;
        this.customers = customers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Customer customer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        }

        TextView tvPhone = convertView.findViewById(R.id.tvCustomerPhone);
        TextView tvPoints = convertView.findViewById(R.id.tvCustomerPoints);
        TextView tvCreatedDate = convertView.findViewById(R.id.tvCustomerCreatedDate);
        TextView tvLastUpdated = convertView.findViewById(R.id.tvCustomerDate);

        tvPhone.setText(customer.getPhone());
        tvPoints.setText(String.valueOf(customer.getPoints()));

        // Hiển thị ngày khởi tạo
        String createdDate = customer.getCreatedDate();
        if (createdDate != null && !createdDate.isEmpty()) {
            tvCreatedDate.setText("Tạo: " + createdDate);
            tvCreatedDate.setVisibility(View.VISIBLE);
        } else {
            tvCreatedDate.setVisibility(View.GONE);
        }

        // Hiển thị ngày chỉnh sửa
        String lastUpdated = customer.getLastUpdated();
        if (lastUpdated != null && !lastUpdated.isEmpty()) {
            tvLastUpdated.setText("Sửa: " + lastUpdated);
            tvLastUpdated.setVisibility(View.VISIBLE);
        } else {
            tvLastUpdated.setVisibility(View.GONE);
        }

        return convertView;
    }
}