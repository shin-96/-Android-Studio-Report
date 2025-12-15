package com.example.thuchanhtuan15;


import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

public class CustomerDetailActivity extends AppCompatActivity {
    private TextView tvPhone, tvPoints;
    private ListView lvTransactions;
    private DatabaseHelper dbHelper;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        customer = (Customer) getIntent().getSerializableExtra("customer");

        tvPhone = findViewById(R.id.tvPhone);
        tvPoints = findViewById(R.id.tvPoints);
        lvTransactions = findViewById(R.id.lvTransactions);

        if (customer != null) {
            tvPhone.setText("SĐT: " + customer.getPhone());
            tvPoints.setText("Điểm hiện tại: " + customer.getPoints());
            getSupportActionBar().setTitle(customer.getPhone());

            loadTransactions();
        }
    }

    private void loadTransactions() {
        ArrayList<Transaction> transactions = dbHelper.getTransactionsByPhone(customer.getPhone());
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);
        lvTransactions.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}