package com.codeWithSrb.BookYourSlot.Model;

import java.sql.Timestamp;
import java.util.Objects;

public class Gadgets {

    private String id;
    private String name;
    private Data data;
    private Timestamp createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gadgets)) return false;
        Gadgets gadgets = (Gadgets) o;
        return Objects.equals(id, gadgets.id) && Objects.equals(name, gadgets.name) && Objects.equals(data, gadgets.data) && Objects.equals(createdAt, gadgets.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, data, createdAt);
    }

    @Override
    public String toString() {
        return "Gadgets{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", createdAt=" + createdAt +
                '}';
    }
}
