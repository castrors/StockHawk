package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_HISTORY;
import static com.udacity.stockhawk.ui.MainActivity.SYMBOL_SELECTED;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int QUOTE_LOADER = 1;
    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };
    private String symbolSelected;

    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        symbolSelected = getIntent().getStringExtra(SYMBOL_SELECTED);
        getSupportActionBar().setTitle(symbolSelected);

        barChart = (BarChart) findViewById(R.id.chart);

    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().initLoader(QUOTE_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                this,
                Contract.Quote.makeUriForStock(symbolSelected),
                QUOTE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String history = data.getString(POSITION_HISTORY);
            setupHistoryBarChart(history);
        }
    }

    private void setupHistoryBarChart(String history) {
        String[] historyArray = history.split("\n");

        float count = 0;
        List<Date> historyDates = new ArrayList<>();
        for (String historyItem : historyArray) {
            String[] historyItemArray = historyItem.split(",");
            Date historyDate = new Date(Long.parseLong(historyItemArray[0]));
            historyDates.add(historyDate);
            float historyValue = Float.parseFloat(historyItemArray[1]);

            entries.add(new BarEntry(count, historyValue));
            count++;
        }

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart, historyDates);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart

        BarDataSet barDataSet = new BarDataSet(entries, symbolSelected+ " stocks");

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
