package com.example.barcodescannerapp;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;

public class SheetsActivity {

    public static String google_api_key = "your_api_key";
    public static String spreadsheet_id = "1cDQZuRyDa13grv7FvhgyhIeRrPrcFf2JhqmFBBXjoPc";

    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JacksonFactory factory = JacksonFactory.getDefaultInstance();
    final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
            .setApplicationName("My Awesome App")
            .build();
    final String spreadsheetId = spreadsheet_id;


        String range = "Sheet1!A1:B2";
        ValueRange result;

    {
        try {
            result = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setKey(google_api_key)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int numRows = result.getValues() != null ? result.getValues().size() : 0;






}
