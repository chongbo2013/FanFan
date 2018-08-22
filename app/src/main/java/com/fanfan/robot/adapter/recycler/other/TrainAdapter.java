package com.fanfan.robot.adapter.recycler.other;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.model.xf.train.Price;
import com.fanfan.robot.model.xf.train.Train;
import com.fanfan.robot.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/7/007.
 */

public class TrainAdapter extends BaseQuickAdapter<Train, BaseViewHolder> {

    private SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat sdfNew = new SimpleDateFormat("HH:mm");

    public TrainAdapter(@Nullable List<Train> data) {
        super(R.layout.item_train, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Train item) {


        try {
            Date date = sdfOriginal.parse(item.getStartTime());
            String startTime = sdfNew.format(date);
            helper.setText(R.id.tv_start_time, startTime);
        } catch (ParseException e) {
            helper.setText(R.id.tv_start_time, "00:00");
            e.printStackTrace();
        }
        helper.setText(R.id.tv_start_time, item.getStarttime_for_voice());
        helper.setText(R.id.tv_origin_station, item.getOriginStation());
        helper.setText(R.id.tv_runtime, item.getRunTime());
        helper.setText(R.id.tv_train_no, item.getTrainNo());

        try {
            Date date = sdfOriginal.parse(item.getArrivalTime());
            String arrivalTime = sdfNew.format(date);
            helper.setText(R.id.tv_end_time, arrivalTime);
        } catch (ParseException e) {
            helper.setText(R.id.tv_end_time, "00:00");
            e.printStackTrace();
        }

        helper.setText(R.id.tv_terminal_station, item.getTerminalStation());

        List<Price> prices = item.getPrice();
        List<Double> doubles = new ArrayList<>();
        if (prices != null && prices.size() > 0) {

            for (Price price : prices) {
                doubles.add(Double.valueOf(price.getValue()));
            }
            Collections.sort(doubles);
            helper.setText(R.id.tv_low_price, String.valueOf(doubles.get(0)));
        } else {
            helper.setText(R.id.tv_low_price, "00");
        }
    }
}
