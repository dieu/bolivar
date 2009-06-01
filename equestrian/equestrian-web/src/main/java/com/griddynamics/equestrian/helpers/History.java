package com.griddynamics.equestrian.helpers;

import com.griddynamics.equestrian.model.HistoryEntity;

import java.util.*;

/**
 * @author: apanasenko aka dieu
 * Date: 18.05.2009
 * Time: 15:52:19
 */
public class History {
    private HistoryEntity historyEntity;
    private Map<Integer, Float> lineChartDataReal;
    private Map<Integer, Float> lineChartDataIdeal;

    public static void main(String[] arg) {
        History s = new History();
        s.add("1", "10", "12", "12", "24348", "12", "23");
        s.add("1", "1", "12", "12", "124452", "12", "232");
        s.add("1", "2", "12", "12", "72308", "12", "232");
        s.add("1", "3", "12", "12", "50728", "12", "232");
        s.add("1", "5", "12", "12", "34649", "12", "232");
        s.add("1", "6", "12", "12", "29109", "12", "232");
        s.add("1", "15", "12", "12", "23206", "12", "232");
        s.add("1", "20", "12", "12", "60403", "12", "232");
        s.add("1", "22", "12", "12", "20919", "12", "232");
        s.add("1", "1", "12", "12", "131531", "12", "232");
        return;
    }

    public History() {
        lineChartDataReal = new TreeMap<Integer, Float>();
        lineChartDataIdeal = new TreeMap<Integer, Float>();
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
            Float timeNew = (Integer.valueOf(time) + timeOld) / 2;
            lineChartDataReal.put(nWorkers, timeNew);
            if(nWorkers == lineChartDataIdeal.keySet().iterator().next()) {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataIdeal.keySet());
                lineChartDataIdeal.clear();
                lineChartDataIdeal.put(nWorkers, timeNew);
                for(int i = 1; i < keys.size(); i++) {
                    Integer key = keys.get(i);
                    timeNew /= (float) Math.pow(2, ((key - 1) - (keys.get(i-1) - 1)));
                    lineChartDataIdeal.put(key, timeNew);
                }
            }
        } else {
            Float timeNew = Float.valueOf(time);
            if(lineChartDataReal.size() == 0) {
                lineChartDataReal.put(nWorkers, timeNew);
                lineChartDataIdeal.put(nWorkers, timeNew);
            } else {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataReal.keySet());
                List<Float> values = new ArrayList<Float>(lineChartDataIdeal.values());
                if(nWorkers < keys.get(0)) {
                    lineChartDataIdeal.clear();
                    lineChartDataIdeal.put(nWorkers, timeNew);
                    timeNew /= (float) Math.pow(2, ((keys.get(0) - 1) - (nWorkers - 1)));
                    lineChartDataIdeal.put(keys.get(0), timeNew);
                    for(int i = 1; i < keys.size(); i++) {
                        Integer key = keys.get(i);
                        timeNew /= (float) Math.pow(2, ((key - 1) -
                                (keys.get(i-1) - 1)));
                        lineChartDataIdeal.put(key, timeNew);
                    }
                } else {
                    timeNew = lineChartDataIdeal.get(keys.get(0)) / (int) Math.pow(2, (nWorkers - 1));
                    lineChartDataIdeal.put(nWorkers, timeNew);                    
                }
                timeNew = Float.valueOf(time);
                lineChartDataReal.put(nWorkers, timeNew);
            }
        }
    }

    public String getHistory() {
        StringBuilder result = new StringBuilder("");
        for(int i = 0; i < historyEntity.getDate().size(); i++) {
            result.append(historyEntity.getDate().get(i) + ";")
                    .append(historyEntity.getWorkers().get(i) + ";")
                    .append(historyEntity.getParsing().get(i) + ";")
                    .append(historyEntity.getReturning().get(i) + ";")
                    .append(historyEntity.getTime().get(i) + ";")
                    .append(historyEntity.getIp().get(i) + ";")
                    .append(historyEntity.getTraf().get(i) + ";")
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
            result.append(workers.get(i) + ";")
                    .append(valueIdeal.get(i) + ";")
                    .append(valueReal.get(i) + ";")
                    .append("|");
        }
        if(result.toString().equals("")) {
            return "";
        } else {
            return result.deleteCharAt(result.length() - 1).toString();
        }
    }
}

