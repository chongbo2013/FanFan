package com.fanfan.robot.model.xf.stock;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/12/6.
 */

public class Stock {

    private String closingPrice;
    private String currentPrice;//目前的价格
    private List<Detail> detail;
    private String highPrice;//高价
    private String lowPrice;//低价
    private String mbmChartUrl;
    private String name;
    private String openingPrice;//openingPrice
    private String riseRate;//上升率
    private String riseValue;//增加价值
    private String source;//来源
    private String stockCode;//股票代码
    private String updateDateTime;
    private String url;

    public String getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(String closingPrice) {
        this.closingPrice = closingPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Detail> detail) {
        this.detail = detail;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getMbmChartUrl() {
        return mbmChartUrl;
    }

    public void setMbmChartUrl(String mbmChartUrl) {
        this.mbmChartUrl = mbmChartUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(String openingPrice) {
        this.openingPrice = openingPrice;
    }

    public String getRiseRate() {
        return riseRate;
    }

    public void setRiseRate(String riseRate) {
        this.riseRate = riseRate;
    }

    public String getRiseValue() {
        return riseValue;
    }

    public void setRiseValue(String riseValue) {
        this.riseValue = riseValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
