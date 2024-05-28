package com.codeWithSrb.BookYourSlot.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Data {

    private int year;
    private double price;

    @SerializedName("CPU model")
    private String cpuModel;

    @SerializedName("Hard disk size")
    private String hardDiskSize;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getHardDiskSize() {
        return hardDiskSize;
    }

    public void setHardDiskSize(String hardDiskSize) {
        this.hardDiskSize = hardDiskSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        Data data = (Data) o;
        return year == data.year && Double.compare(price, data.price) == 0 && Objects.equals(cpuModel, data.cpuModel) && Objects.equals(hardDiskSize, data.hardDiskSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, price, cpuModel, hardDiskSize);
    }

    @Override
    public String toString() {
        return "Data{" +
                "year=" + year +
                ", price=" + price +
                ", cpuModel='" + cpuModel + '\'' +
                ", hardDiskSize='" + hardDiskSize + '\'' +
                '}';
    }
}
