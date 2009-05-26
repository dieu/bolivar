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
    private Map<Integer, Integer> lineChartDataReal;
    private Map<Integer, Integer> lineChartDataIdeal;

    public static void main(String[] arg) {
        History s = new History();
        s.add("1", "2", "12", "12", "55382", "12", "23");
        s.add("1", "1", "12", "12", "99160", "12", "232");
    s.add("1", "3", "12", "12", "33300", "12", "232");
        return;
    }

    public History() {
        lineChartDataReal = new HashMap<Integer, Integer>();
        lineChartDataIdeal = new HashMap<Integer, Integer>();
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
            int timeOld = lineChartDataReal.get(nWorkers);
            int timeNew = (Integer.valueOf(time) + timeOld) / 2;
            lineChartDataReal.put(nWorkers, timeNew);
            if(nWorkers == lineChartDataIdeal.keySet().iterator().next()) {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataIdeal.keySet());
                lineChartDataIdeal.clear();
                lineChartDataIdeal.put(nWorkers, timeNew);
                keys.remove(0);
                for(Integer key: keys) {
                    timeNew /= 2;
                    lineChartDataIdeal.put(key, timeNew);
                }
            }
        } else {
            int timeNew = Integer.valueOf(time);
            lineChartDataReal.put(nWorkers, timeNew);
            if(lineChartDataReal.size() == 1) {
                lineChartDataIdeal.put(nWorkers, timeNew);
            } else {
                List<Integer> keys = new ArrayList<Integer>(lineChartDataIdeal.keySet());
                if(nWorkers < keys.get(0)) {
                    lineChartDataIdeal.clear();
                    lineChartDataIdeal.put(nWorkers, timeNew);
                    for(Integer key: keys) {
                        timeNew /= 2;
                        lineChartDataIdeal.put(key, timeNew);
                    }
                } else {
                    timeNew = lineChartDataIdeal.get(keys.get(keys.size() - 1)) / 2;
                    lineChartDataIdeal.put(nWorkers, timeNew);
                }
            }
        }
        return;
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
        List<Integer> valueReal = new ArrayList<Integer>(lineChartDataReal.values());
        List<Integer> valueIdeal = new ArrayList<Integer>(lineChartDataIdeal.values());
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

