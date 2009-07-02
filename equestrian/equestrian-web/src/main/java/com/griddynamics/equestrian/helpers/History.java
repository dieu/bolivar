package com.griddynamics.equestrian.helpers;

import com.griddynamics.equestrian.model.HistoryEntity;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: apanasenko aka dieu
 * Date: 18.05.2009
 * Time: 15:52:19
 */
public class History {
    private HistoryEntity historyEntity;
    private String pathChartToCVS;
    private Map<Integer, Float> lineChartDataReal;
    private Map<Integer, Float> lineChartDataIdeal;

    public History() {
        lineChartDataReal = new TreeMap<Integer, Float>();
        lineChartDataIdeal = new TreeMap<Integer, Float>();
        pathChartToCVS = ApplicationPath.APPLICATION_PATH;
        pathChartToCVS += Calendar.getInstance().get(Calendar.DATE) + "_" + Calendar.getInstance().get(Calendar.HOUR)
                + "_" + Calendar.getInstance().get(Calendar.MINUTE) + ".csv";
    }

    public void setHistoryEntity(HistoryEntity historyEntity) {
        this.historyEntity = historyEntity;
    }

    public void add(String date, String workers,
                    String parsing, String returning, String time, String ip, String traf) {
        historyEntity.setDate(date);
        historyEntity.setWorkers(workers);
        historyEntity.setParsing(parsing);
        historyEntity.setReturning(returning);
        historyEntity.setTime(time);
        historyEntity.setIp(ip);
        historyEntity.setTraf(traf);

        int nWorkers = Integer.valueOf(workers);
        if(lineChartDataReal.containsKey(nWorkers)) {
            Float timeOld = lineChartDataReal.get(nWorkers);
            Float timeNew = (Float.valueOf(time) + timeOld) / 2;
            lineChartDataReal.put(nWorkers, timeNew);
            if(nWorkers == lineChartDataIdeal.keySet().iterator().next()) {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataIdeal.keySet());
                lineChartDataIdeal.clear();
                lineChartDataIdeal.put(nWorkers, timeNew);
                for(int i = 1; i < keys.size(); i++) {
                    int key = keys.get(i);
                    lineChartDataIdeal.put(key, (float) (timeNew * (1.0 / key)));
                }
            }
        } else {
            Float timeNew = Float.valueOf(time);
            if(lineChartDataReal.size() == 0) {
                lineChartDataReal.put(nWorkers, timeNew);
                lineChartDataIdeal.put(nWorkers, timeNew);
            } else {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataReal.keySet());
                if(nWorkers < keys.get(0)) {
                    lineChartDataIdeal.clear();
                    lineChartDataIdeal.put(nWorkers, timeNew);
                    for(int key: keys) {
                        lineChartDataIdeal.put(key, (float) (timeNew * (1.0 / key)));
                    }
                } else {
                    timeNew = (float) (lineChartDataIdeal.get(keys.get(0)) * (1.0 / nWorkers));
                    lineChartDataIdeal.put(nWorkers, timeNew);
                }
                timeNew = Float.valueOf(time);
                lineChartDataReal.put(nWorkers, timeNew);
            }
        }
        try {
            FileWriter chartToCVS = new FileWriter(pathChartToCVS, false);
            chartToCVS.write(lineChartDataReal.toString().replace("{", "").replace("}", "")
                    .replace(",", "\n").replace(" ", "").replace("=", ", "));
            chartToCVS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHistory() {
        StringBuilder result = new StringBuilder("");
        for(int i = 0; i < historyEntity.getDate().size(); i++) {
            result.append(historyEntity.getDate().get(i)).append(";")
                    .append(historyEntity.getWorkers().get(i)).append(";")
                    .append(historyEntity.getParsing().get(i)).append(";")
                    .append(historyEntity.getReturning().get(i)).append(";")
                    .append(historyEntity.getTime().get(i)).append(";")
                    .append(historyEntity.getIp().get(i)).append(";")
                    .append(historyEntity.getTraf().get(i)).append(";")
                    .append("|");
        }
        if(result.toString().equals("")) {
            return "";
        } else {
            return result.deleteCharAt(result.length() - 1).toString();
        }
    }

    public String getLineData() {
        StringBuilder result = new StringBuilder("");
        List<Integer> workers = new ArrayList<Integer>(lineChartDataReal.keySet());
        List<Float> valueReal = new ArrayList<Float>(lineChartDataReal.values());
        List<Float> valueIdeal = new ArrayList<Float>(lineChartDataIdeal.values());
        for(int i = 0; i < workers.size(); i++) {
            result.append(workers.get(i)).append(";")
                    .append(valueIdeal.get(i)).append(";")
                    .append(valueReal.get(i)).append(";")
                    .append("|");
        }
        if(result.toString().equals("")) {
            return "";
        } else {
            return result.deleteCharAt(result.length() - 1).toString();
        }
    }
}

