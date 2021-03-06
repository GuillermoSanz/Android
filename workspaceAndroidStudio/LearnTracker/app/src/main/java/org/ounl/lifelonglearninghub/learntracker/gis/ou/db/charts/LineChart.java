 /*******************************************************************************
 * Copyright (C) 2014 Open University of The Netherlands
 * Author: Bernardo Tabuenca Archilla
 * LearnTracker project 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.ounl.lifelonglearninghub.learntracker.gis.ou.db.charts;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class LineChart extends AbstractChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "LearnTracker";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "Distribution of learning moments along the day (line chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Blogging", "Read scientific papers", "Learn iOS programming", "Get B2 level in German" };
    List<double[]> x = new ArrayList<double[]>();
    for (int i = 0; i < titles.length; i++) {
      //x.add(new double[] { Mo, Tu, We, Th, Fr, Sa, Su});
    	x.add(new double[] { 1, 2, 3, 4, 5, 6, 7});
    }
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] { 8.3, 8.5, 8.8, 9.8, 8.4, 9.4, 7.4});
    values.add(new double[] { 10, 10, 12, 15, 11, 12, 10});
    values.add(new double[] { 17, 17.5, 20, 16.50, 20, 17, 18.10 });
    values.add(new double[] { 20, 21, 17, 20.5, 19, 19.30, 19.40});
    int[] colors = new int[] { Color.parseColor("#E05904"), Color.parseColor("#D97E45"),
    		Color.parseColor("#DBA481"), Color.parseColor("#CCC8C6") };
    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
        PointStyle.TRIANGLE, PointStyle.SQUARE };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
    }
    setChartSettings(renderer, "Distribution of learning moments along the day", "Activity in the last 7 days", "Time of the day", -0.5, 7.5, 0, 24,
        Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { 0, 12, 0, 24 });
    renderer.setZoomLimits(new double[] { 0, 12, 0, 24 });
    
    renderer.setApplyBackgroundColor(true);
    renderer.setBackgroundColor(Color.WHITE);
    renderer.setMarginsColor(Color.WHITE);    

    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
    XYSeries series = dataset.getSeriesAt(0);
    series.addAnnotation("Today", 74, 21);
    Intent intent = ChartFactory.getLineChartIntent(context, dataset, renderer,
        "LearnTracker");
    return intent;
  }

}
