package com.example.thuchanhtuan15;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InputPointActivity extends AppCompatActivity {
    private EditText etPhone, etInputPoint, etNote;
    private TextView tvCurrentPoint;
    private Button btnSaveNext, btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_point);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("INPUT POINT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        etPhone = findViewById(R.id.etPhone);
        etInputPoint = findViewById(R.id.etInputPoint);
        etNote = findViewById(R.id.etNote);
        tvCurrentPoint = findViewById(R.id.tvCurrentPoint);
        btnSaveNext = findViewById(R.id.btnSaveNext);
        btnBack = findViewById(R.id.btnBack);

        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = etPhone.getText().toString().trim();
                if (!phone.isEmpty()) {
                    Customer customer = dbHelper.getCustomerByPhone(phone);
                    if (customer != null) {
                        tvCurrentPoint.setText(String.valueOf(customer.getPoints()));
                    } else {
                        tvCurrentPoint.setText("0");
                    }
                }
            }
        });

        btnSaveNext.setOnClickListener(v -> savePoint());
        btnBack.setOnClickListener(v -> finish());
    }

    // ✅ THÊM MỚI: Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ✅ THÊM MỚI: Xử lý click menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_input) {
            // Đang ở màn hình INPUT rồi, không làm gì
            return true;
        } else if (id == R.id.action_use) {
            // Chuyển sang màn hình USE
            startActivity(new Intent(this, UsePointActivity.class));
            return true;
        } else if (id == R.id.action_list) {
            // Chuyển sang màn hình LIST
            startActivity(new Intent(this, CustomerListActivity.class));
            return true;
        } else if (id == R.id.action_export_xml) {
            Toast.makeText(this, "Chức năng này chỉ khả dụng ở màn hình LIST", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_export_pdf) {
            Toast.makeText(this, "Chức năng này chỉ khả dụng ở màn hình LIST", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_export_excel) {
            Toast.makeText(this, "Chức năng này chỉ khả dụng ở màn hình LIST", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_import_xml) {
            Toast.makeText(this, "Chức năng này chỉ khả dụng ở màn hình LIST", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_import_excel) {
            Toast.makeText(this, "Chức năng này chỉ khả dụng ở màn hình LIST", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePoint() {
        String phone = etPhone.getText().toString().trim();
        String inputPointStr = etInputPoint.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (phone.isEmpty() || inputPointStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại và điểm", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputPoint = Integer.parseInt(inputPointStr);
        Customer customer = dbHelper.getCustomerByPhone(phone);

        if (customer == null) {
            customer = new Customer(phone, inputPoint);
            dbHelper.addCustomer(customer);
        } else {
            customer.setPoints(customer.getPoints() + inputPoint);
            dbHelper.updateCustomer(customer);
        }

        dbHelper.addTransaction(phone, inputPoint, note, "INPUT");

        Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        etPhone.setText("");
        etInputPoint.setText("");
        etNote.setText("");
        tvCurrentPoint.setText("0");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}