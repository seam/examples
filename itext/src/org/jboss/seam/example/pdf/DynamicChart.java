package org.jboss.seam.example.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jboss.seam.annotations.*;
import org.jboss.seam.*;

@Name("chart")
@Scope(ScopeType.SESSION)
public class DynamicChart {
    private static final int CHART_RANGE = 50;
    private List<String> categories = new ArrayList<String>();
    private int nextSeries = 1;
    private int nextCategory = 1;
    
    Random random = new Random();
    
    List<Data> data = new ArrayList<Data>();
    
    boolean is3d = false;
    boolean legend = true;
    
    String  title = "Dynamic Chart";
    String  domainAxisLabel = "Domain Label";
    String  domainAxisPaint;
    boolean domainGridlinesVisible = false;
    String  domainGridlinePaint;
    String  domainGridlineStroke;
    String  rangeAxisLabel = "Range Label";
    String  rangeAxisPaint;
    boolean rangeGridlinesVisible = true;
    String  rangeGridlinePaint;
    String  rangeGridlineStroke;
    
    String orientation           = "vertical";
    String plotBackgroundPaint   = "white";
    String plotOutlinePaint      = "black";
    String plotOutlineStroke     = "solid-thin";
    String borderPaint           = "black";
    String borderBackgroundPaint = "white";
    String borderStroke          = "solid-thick";
    
    String titlePaint;
    String titleBackgroundPaint;
    String legendBackgroundPaint;
    String legendItemPaint;
    
    Float plotBackgroundAlpha = .7f;
    Float plotForegroundAlpha = 1f;
    
    float height = 300;
    float width  = 400;    
    
    boolean borderVisible = true; 
    
    public boolean getIs3d() {
        return is3d;
    }

    public void setIs3d(boolean is3d) {
        this.is3d = is3d;
    }

    public String getDomainAxisLabel() {
        return domainAxisLabel;
    }

    public void setDomainAxisLabel(String categoryAxisLabel) {
        this.domainAxisLabel = categoryAxisLabel;
    }

    public String getRangeAxisLabel() {
        return rangeAxisLabel;
    }

    public void setRangeAxisLabel(String valueAxisLabel) {
        this.rangeAxisLabel = valueAxisLabel;
    }

    public boolean isBorderVisible() {
        return borderVisible;
    }

    public void setBorderVisible(boolean borderVisible) {
        this.borderVisible = borderVisible;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isLegend() {
        return legend;
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getBorderBackgroundPaint() {
        return borderBackgroundPaint;
    }

    public void setBorderBackgroundPaint(String borderBackgroundPaint) {
        this.borderBackgroundPaint = borderBackgroundPaint;
    }

    public String getBorderPaint() {
        return borderPaint;
    }

    public void setBorderPaint(String borderPaint) {
        this.borderPaint = borderPaint;
    }

    public String getPlotBackgroundPaint() {
        return plotBackgroundPaint;
    }

    public void setPlotBackgroundPaint(String plotBackgroundPaint) {
        this.plotBackgroundPaint = plotBackgroundPaint;
    }

    public String getPlotOutlinePaint() {
        return plotOutlinePaint;
    }

    public void setPlotOutlinePaint(String plotOutlinePaint) {
        this.plotOutlinePaint = plotOutlinePaint;
    }

    public String getBorderStroke() {
        return borderStroke;
    }

    public void setBorderStroke(String borderStroke) {
        this.borderStroke = borderStroke;
    }

    public String getPlotOutlineStroke() {
        return plotOutlineStroke;
    }

    public void setPlotOutlineStroke(String plotOutlineStroke) {
        this.plotOutlineStroke = plotOutlineStroke;
    }

    public Float getPlotBackgroundAlpha() {
        return plotBackgroundAlpha;
    }

    public void setPlotBackgroundAlpha(Float plotBackgroundAlpha) {
        this.plotBackgroundAlpha = plotBackgroundAlpha;
    }

    public Float getPlotForegroundAlpha() {
        return plotForegroundAlpha;
    }

    public void setPlotForegroundAlpha(Float plotForegroundAlpha) {
        this.plotForegroundAlpha = plotForegroundAlpha;
    }

    public String getTitleBackgroundPaint() {
        return titleBackgroundPaint;
    }

    public void setTitleBackgroundPaint(String titleBackgroundPaint) {
        this.titleBackgroundPaint = titleBackgroundPaint;
    }

    public String getTitlePaint() {
        return titlePaint;
    }

    public void setTitlePaint(String titlePaint) {
        this.titlePaint = titlePaint;
    }

    public String getLegendBackgroundPaint() {
        return legendBackgroundPaint;
    }

    public void setLegendBackgroundPaint(String legendBackgroundPaint) {
        this.legendBackgroundPaint = legendBackgroundPaint;
    }

    public String getLegendItemPaint() {
        return legendItemPaint;
    }

    public void setLegendItemPaint(String legendItemPaint) {
        this.legendItemPaint = legendItemPaint;
    }

    public String getDomainAxisPaint() {
        return domainAxisPaint;
    }

    public void setDomainAxisPaint(String domainAxisPaint) {
        this.domainAxisPaint = domainAxisPaint;
    }

    public String getDomainGridlinePaint() {
        return domainGridlinePaint;
    }

    public void setDomainGridlinePaint(String domainGridlinePaint) {
        this.domainGridlinePaint = domainGridlinePaint;
    }

    public String getDomainGridlineStroke() {
        return domainGridlineStroke;
    }

    public void setDomainGridlineStroke(String domainGridlineStroke) {
        this.domainGridlineStroke = domainGridlineStroke;
    }

    public boolean isDomainGridlinesVisible() {
        return domainGridlinesVisible;
    }

    public void setDomainGridlinesVisible(boolean domainGridlineVisible) {
        this.domainGridlinesVisible = domainGridlineVisible;
    }

    public String getRangeAxisPaint() {
        return rangeAxisPaint;
    }

    public void setRangeAxisPaint(String rangeAxisPaint) {
        this.rangeAxisPaint = rangeAxisPaint;
    }

    public String getRangeGridlinePaint() {
        return rangeGridlinePaint;
    }

    public void setRangeGridlinePaint(String rangeGridlinePaint) {
        this.rangeGridlinePaint = rangeGridlinePaint;
    }

    public String getRangeGridlineStroke() {
        return rangeGridlineStroke;
    }

    public void setRangeGridlineStroke(String rangeGridlineStroke) {
        this.rangeGridlineStroke = rangeGridlineStroke;
    }

    public boolean isRangeGridlinesVisible() {
        return rangeGridlinesVisible;
    }

    public void setRangeGridlinesVisible(boolean rangeGridlineVisible) {
        this.rangeGridlinesVisible = rangeGridlineVisible;
    }

    
    @Create
    public void initData() {
        newCategory();
        newCategory();
        newCategory();

        newSeries();
        newSeries();
    }
        
    public List<Data> getData() {
        return data;
    }

    public List<String> getCategories() {
        return categories;
    }
    
    public void removeSeries(String id) {
        // System.out.println("REMOVE: " + id);
    }
    
    public void newSeries() {
        String newId = findUniqueSeriesId();
        Data set = new Data();
        set.setId(newId);
        
        for (String category: categories) {
            set.addValue(category,  random.nextInt(CHART_RANGE));
        }        
        
        data.add(set);
    }
    
    public void newCategory() {
        String newId = findUniqueCategoryId();
        categories.add(newId);

        for (Data set: data) {
            set.addValue(newId,  random.nextInt(CHART_RANGE));
        }        
    }

    private String findUniqueSeriesId() {
        while (true) {
            String id = "Series " + nextSeries++;
            if (isUniqueSeriesId(id)) {
                return id;
            }
        }
    }

    private boolean isUniqueSeriesId(String id) {
        for (Data item: data) {
            if (item.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }  
    

    private String findUniqueCategoryId() {
        while (true) {
            String id = "Category " + nextCategory++;
            if (!categories.contains(id)) {
                return id;
            }
        }
    }
    
}
