package com.example.thuchanhtuan15;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class XMLHelper {

    public static String exportCustomersToXML(ArrayList<Customer> customers) {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "customers");

            for (Customer customer : customers) {
                serializer.startTag("", "customer");

                serializer.startTag("", "phone");
                serializer.text(customer.getPhone());
                serializer.endTag("", "phone");

                serializer.startTag("", "points");
                serializer.text(String.valueOf(customer.getPoints()));
                serializer.endTag("", "points");

                serializer.startTag("", "created_date");
                serializer.text(customer.getCreatedDate() != null ? customer.getCreatedDate() : "");
                serializer.endTag("", "created_date");

                serializer.startTag("", "last_updated");
                serializer.text(customer.getLastUpdated() != null ? customer.getLastUpdated() : "");
                serializer.endTag("", "last_updated");

                serializer.endTag("", "customer");
            }

            serializer.endTag("", "customers");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveXMLToFile(String xml, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(xml.getBytes("UTF-8"));
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Customer> importCustomersFromXML(File file) {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);

            String phone = null;
            int points = 0;
            String createdDate = null;
            String lastUpdated = null;
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("phone")) {
                            phone = parser.nextText();
                        } else if (tagName.equals("points")) {
                            points = Integer.parseInt(parser.nextText());
                        } else if (tagName.equals("created_date")) {
                            createdDate = parser.nextText();
                        } else if (tagName.equals("last_updated")) {
                            lastUpdated = parser.nextText();
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equals("customer")) {
                            Customer customer = new Customer(0, phone, points, createdDate, lastUpdated);
                            customers.add(customer);
                        }
                        break;
                }
                eventType = parser.next();
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }
}