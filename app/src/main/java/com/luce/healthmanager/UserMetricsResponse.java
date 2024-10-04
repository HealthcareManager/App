package com.luce.healthmanager;

import java.util.List;

public class UserMetricsResponse {
    private String userId;
    private List<Metric> metrics;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public static class Metric {
        private Long id;
        private Integer heartRate;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(Integer heartRate) {
            this.heartRate = heartRate;
        }

        public String getBloodPressure() {
            return bloodPressure;
        }

        public void setBloodPressure(String bloodPressure) {
            this.bloodPressure = bloodPressure;
        }

        public Double getBloodSugar() {
            return bloodSugar;
        }

        public void setBloodSugar(Double bloodSugar) {
            this.bloodSugar = bloodSugar;
        }

        public Double getBloodOxygen() {
            return bloodOxygen;
        }

        public void setBloodOxygen(Double bloodOxygen) {
            this.bloodOxygen = bloodOxygen;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        private String bloodPressure;
        private Double bloodSugar;
        private Double bloodOxygen;
        private String timestamp;

        // Getters and Setters

    }

}
