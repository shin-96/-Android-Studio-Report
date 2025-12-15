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

public class UsePointActivity extends AppCompatActivity {
    private EditText etPhone, etUsePoint, etNote;
    private TextView tvCurrentPoint;
    private Button btnSaveNext, btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_point);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("USE POINT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        etPhone = findViewById(R.id.etPhone);
        etUsePoint = findViewById(R.id.etUsePoint);
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
                        Toast.makeText(this, "Không tìm thấy khách hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSaveNext.setOnClickListener(v -> usePoint());
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
            // Chuyển sang màn hình INPUT
            startActivity(new Intent(this, InputPointActivity.class));
            return true;
        } else if (id == R.id.action_use) {
            // Đang ở màn hình USE rồi, không làm gì
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

    private void usePoint() {
        String phone = etPhone.getText().toString().trim();
        String usePointStr = etUsePoint.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (phone.isEmpty() || usePointStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại và điểm", Toast.LENGTH_SHORT).show();
            return;
        }

        int usePoint = Integer.parseInt(usePointStr);
        Customer customer = dbHelper.getCustomerByPhone(phone);

        if (customer == null) {
            Toast.makeText(this, "Khách hàng không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customer.getPoints() < usePoint) {
            Toast.makeText(this, "Điểm không đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        customer.setPoints(customer.getPoints() - usePoint);
        dbHelper.updateCustomer(customer);
        dbHelper.addTransaction(phone, usePoint, note, "USE");

        Toast.makeText(this, "Đã sử dụng điểm thành công", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        etPhone.setText("");
        etUsePoint.setText("");
        etNote.setText("");
        tvCurrentPoint.setText("0");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}