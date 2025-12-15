package com.example.thuchanhtuan15;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        }

        TextView tvPoints = convertView.findViewById(R.id.tvTransPoints);
        TextView tvNote = convertView.findViewById(R.id.tvTransNote);
        TextView tvDate = convertView.findViewById(R.id.tvTransDate);

        String pointsText = transaction.getType().equals("INPUT") ?
                "+" + transaction.getPoints() : "-" + transaction.getPoints();
        tvPoints.setText(pointsText);

        if (transaction.getType().equals("INPUT")) {
            tvPoints.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tvPoints.setTextColor(Color.parseColor("#F44336"));
        }

        tvNote.setText(transaction.getNote().isEmpty() ? "-" : transaction.getNote());
        tvDate.setText(transaction.getDate());

        return convertView;
    }
}