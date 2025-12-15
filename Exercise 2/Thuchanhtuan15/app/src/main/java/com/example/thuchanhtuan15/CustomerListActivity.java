package com.example.thuchanhtuan15;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.ArrayList;

public class CustomerListActivity extends AppCompatActivity {
    private ListView listView;
    private CustomerAdapter adapter;
    private ArrayList<Customer> customers;
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<String> importLauncher;
    private ActivityResultLauncher<String> importExcelLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách khách hàng");

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);

        loadCustomers();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Customer customer = customers.get(position);
            Intent intent = new Intent(CustomerListActivity.this, CustomerDetailActivity.class);
            intent.putExtra("customer", customer);
            startActivity(intent);
        });

        // Initialize import launcher
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        importFromXML(uri);
                    }
                }
        );

        // Initialize Excel import launcher
        importExcelLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        importFromExcel(uri);
                    }
                }
        );
    }

    private void loadCustomers() {
        customers = dbHelper.getAllCustomers();
        adapter = new CustomerAdapter(this, customers);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_input) {
            startActivity(new Intent(this, InputPointActivity.class));
            return true;
        } else if (id == R.id.action_use) {
            startActivity(new Intent(this, UsePointActivity.class));
            return true;
        } else if (id == R.id.action_list) {
            return true;
        } else if (id == R.id.action_export_xml) {
            exportToXML();
            return true;
        } else if (id == R.id.action_export_pdf) {
            exportToPDF();
            return true;
        } else if (id == R.id.action_export_excel) {
            exportToExcel();
            return true;
        } else if (id == R.id.action_import_xml) {
            importLauncher.launch("*/*");
            return true;
        } else if (id == R.id.action_import_excel) {
            importExcelLauncher.launch("*/*");
            return true;
        } else if (id == R.id.action_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportToXML() {
        if (customers.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        String xml = XMLHelper.exportCustomersToXML(customers);
        if (xml == null) {
            Toast.makeText(this, "Xuất file thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LoyalCustomer");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "customers_" + System.currentTimeMillis() + ".xml");

        if (XMLHelper.saveXMLToFile(xml, file)) {
            new AlertDialog.Builder(this)
                    .setTitle("Xuất file thành công")
                    .setMessage("File đã được lưu tại:\n" + file.getAbsolutePath() + "\n\nBạn có muốn gửi qua email?")
                    .setPositiveButton("Gửi Email", (dialog, which) -> sendEmail(file))
                    .setNegativeButton("Đóng", null)
                    .show();
        } else {
            Toast.makeText(this, "Lưu file thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(File file) {
        Uri fileUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);

        String fileName = file.getName();
        String fileType;
        String subject;

        if (fileName.endsWith(".pdf")) {
            fileType = "application/pdf";
            subject = "Danh sách khách hàng thân thiết (PDF)";
        } else if (fileName.endsWith(".xlsx")) {
            fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            subject = "Danh sách khách hàng thân thiết (Excel)";
        } else {
            fileType = "application/xml";
            subject = "Danh sách khách hàng thân thiết (XML)";
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(fileType);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Danh sách khách hàng được xuất từ ứng dụng Khách Hàng Thân Thiết");
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(emailIntent, "Gửi email qua..."));
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở ứng dụng email", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromXML(Uri uri) {
        try {
            File tempFile = new File(getCacheDir(), "temp_import.xml");
            getContentResolver().openInputStream(uri).transferTo(new java.io.FileOutputStream(tempFile));

            ArrayList<Customer> importedCustomers = XMLHelper.importCustomersFromXML(tempFile);

            if (importedCustomers.isEmpty()) {
                Toast.makeText(this, "File XML không hợp lệ hoặc rỗng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.importCustomersFromXML(importedCustomers)) {
                Toast.makeText(this, "Nhập " + importedCustomers.size() + " khách hàng thành công", Toast.LENGTH_SHORT).show();
                loadCustomers();
            } else {
                Toast.makeText(this, "Nhập file thất bại", Toast.LENGTH_SHORT).show();
            }

            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportToPDF() {
        if (customers.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LoyalCustomer");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "customers_" + System.currentTimeMillis() + ".pdf");

        if (PDFHelper.exportCustomersToPDF(this, customers, file)) {
            new AlertDialog.Builder(this)
                    .setTitle("Xuất PDF thành công")
                    .setMessage("File đã được lưu tại:\n" + file.getAbsolutePath() + "\n\nBạn có muốn gửi qua email?")
                    .setPositiveButton("Gửi Email", (dialog, which) -> sendEmail(file))
                    .setNegativeButton("Mở PDF", (dialog, which) -> openPDF(file))
                    .setNeutralButton("Đóng", null)
                    .show();
        } else {
            Toast.makeText(this, "Xuất PDF thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDF(File file) {
        Uri fileUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng đọc PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportToExcel() {
        if (customers.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LoyalCustomer");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "customers_" + System.currentTimeMillis() + ".xlsx");

        if (ExcelHelper.exportCustomersToExcel(this, customers, file)) {
            new AlertDialog.Builder(this)
                    .setTitle("Xuất Excel thành công")
                    .setMessage("File đã được lưu tại:\n" + file.getAbsolutePath() + "\n\nBạn có muốn gửi qua email?")
                    .setPositiveButton("Gửi Email", (dialog, which) -> sendEmail(file))
                    .setNegativeButton("Mở Excel", (dialog, which) -> openExcel(file))
                    .setNeutralButton("Đóng", null)
                    .show();
        } else {
            Toast.makeText(this, "Xuất Excel thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void openExcel(File file) {
        Uri fileUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(intent, "Mở Excel với..."));
        } catch (Exception e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng đọc Excel", Toast.LENGTH_SHORT).show();
        }
    }

    private void importFromExcel(Uri uri) {
        try {
            File tempFile = new File(getCacheDir(), "temp_import.xlsx");
            getContentResolver().openInputStream(uri).transferTo(new java.io.FileOutputStream(tempFile));

            ArrayList<Customer> importedCustomers = ExcelHelper.importCustomersFromExcel(tempFile);

            if (importedCustomers.isEmpty()) {
                Toast.makeText(this, "File Excel không hợp lệ hoặc rỗng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.importCustomersFromXML(importedCustomers)) {
                Toast.makeText(this, "Nhập " + importedCustomers.size() + " khách hàng thành công", Toast.LENGTH_SHORT).show();
                loadCustomers();
            } else {
                Toast.makeText(this, "Nhập file thất bại", Toast.LENGTH_SHORT).show();
            }

            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}