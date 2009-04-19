package org.jboss.seam.example.pdf;

import java.util.HashMap;
import java.util.Map;

public class Data {
    String id = this.toString();

    boolean visible = true;
    boolean visibleInLegend = true;
    String seriesPaint;
    String seriesFillPaint;
    String seriesOutlinePaint;
    String seriesOutlineStroke;
    String seriesStroke;
    
    Map<String, Number> values = new HashMap<String, Number>();
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String,Number> getValues() {
        return values;
    }

    public void addValue(String key, Number value) {
        values.put(key, value);
    }

    public boolean isVisibleInLegend() {
        return visibleInLegend;
    }

    public void setVisibleInLegend(boolean visibleInLegend) {
        this.visibleInLegend = visibleInLegend;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getSeriesFillPaint() {
        return seriesFillPaint;
    }

    public void setSeriesFillPaint(String seriesFillPaint) {
        this.seriesFillPaint = seriesFillPaint;
    }

    public String getSeriesOutlinePaint() {
        return seriesOutlinePaint;
    }

    public void setSeriesOutlinePaint(String seriesOutlinePaint) {
        this.seriesOutlinePaint = seriesOutlinePaint;
    }

    public String getSeriesOutlineStroke() {
        return seriesOutlineStroke;
    }

    public void setSeriesOutlineStroke(String seriesOutlineStroke) {
        this.seriesOutlineStroke = seriesOutlineStroke;
    }

    public String getSeriesPaint() {
        return seriesPaint;
    }

    public void setSeriesPaint(String seriesPaint) {
        this.seriesPaint = seriesPaint;
    }

    public String getSeriesStroke() {
        return seriesStroke;
    }

    public void setSeriesStroke(String seriesStroke) {
        this.seriesStroke = seriesStroke;
    }
}
