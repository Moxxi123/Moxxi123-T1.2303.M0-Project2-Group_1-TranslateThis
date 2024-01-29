package Control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class bangxepHang {

    private IntegerProperty so_tu_dong;
    private IntegerProperty tong_diem;
    private StringProperty ngay_luu;
    private StringProperty time_luu;

    public bangxepHang() {
    }

    public bangxepHang(int so_tu_dong, int tong_diem, String ngay_luu, String time_luu) {
        this.so_tu_dong = new SimpleIntegerProperty(so_tu_dong);
        this.tong_diem = new SimpleIntegerProperty(tong_diem);
        this.ngay_luu = new SimpleStringProperty(ngay_luu);
        this.time_luu = new SimpleStringProperty(time_luu);
    }
    
    public int get_so_tu_dong() {
        return so_tu_dong.get();
    }

    public int get_tong_diem() {
        return tong_diem.get();
    }

    public String get_ngay_luu() {
        return ngay_luu.get();
    }

    public String get_time_luu() {
        return time_luu.get();
    }

    public void set_so_tu_dong(int so_tu_dong) {
        this.so_tu_dong.set(so_tu_dong);
    }

    public void set_tong_diem(int tong_diem) {
        this.tong_diem.set(tong_diem);
    }

    public void set_ngay_luu(String ngay_luu) {
        this.ngay_luu.set(ngay_luu);
    }

    public void set_time_luu(String time_luu) {
        this.time_luu.set(time_luu);
    }

//    public IntegerProperty so_tu_dong_Property() {
//        return so_tu_dong;
//    }
//
//    public IntegerProperty tong_diem_Property() {
//        return tong_diem;
//    }
//
//    public StringProperty ngay_luu_Property() {
//        return ngay_luu;
//    }
//
//    public StringProperty time_luu_Property() {
//        return time_luu;
//    }

}
