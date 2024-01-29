package Control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TongKetResult {

    private IntegerProperty stt;
    private StringProperty cauhoi;
    private StringProperty dapan;
    private IntegerProperty diem;

    public TongKetResult() {
    }

    public TongKetResult(int stt, int TongDiem) {
        this.stt = new SimpleIntegerProperty(stt);
        this.diem = new SimpleIntegerProperty(TongDiem);
    }

    public TongKetResult(int stt, String cauhoi, String dapan, int diem) {
        this.stt = new SimpleIntegerProperty(stt);
        this.cauhoi = new SimpleStringProperty(cauhoi);
        this.dapan = new SimpleStringProperty(dapan);
        this.diem = new SimpleIntegerProperty(diem);
    }

    public int getStt() {
        return stt.get();
    }

    public String getCauhoi() {
        return cauhoi.get();
    }

    public String getDapan() {
        return dapan.get();
    }

    public int getDiem() {
        return diem.get();
    }

    public void setStt(int stt) {
        this.stt.set(stt);
    }

    public void setCauhoi(String cauhoi) {
        this.cauhoi.set(cauhoi);
    }

    public void setDapan(String dapan) {
        this.dapan.set(dapan);
    }

    public void setDiem(int diem) {
        this.diem.set(diem);
    }

    public IntegerProperty sttProperty() {
        return stt;
    }

    public StringProperty cauhoiProperty() {
        return cauhoi;
    }

    public StringProperty dapanProperty() {
        return dapan;
    }

    public IntegerProperty diemProperty() {
        return diem;
    }

}
