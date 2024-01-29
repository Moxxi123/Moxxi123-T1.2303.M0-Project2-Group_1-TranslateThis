
package Control;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;


public class ListView {
    //khởi tạo đối tượng function trong translateThis_function
    TranslateThis_Function function = new TranslateThis_Function(); // function để tạo các hàm
    
    private CheckBox check; // biến tạo checkBox
    String word, pronounce, type, definition;
    private String tablechoice; // chuỗi để ;ưu giá trị từ bộ dieudf khiển TransaltaThis_Controller

    // Hàm dựng
    public ListView() {
    }

     public ListView(int checkFlag, String word, String pronounce, String type, String definition, String tablechoice) {
        this.check = new CheckBox();
        this.check.setSelected(checkFlag == 1); // CheckFlag đại diện cho "check trong database" nếu Checkflag giá trị là 1 tì checkBox sẽ được chọn
        this.word = word;
        this.pronounce = pronounce;
        this.type = type;
        this.definition = definition; // Dùng để truyền giá trị biến tablechoice từ TranslateThis_DanhSachTuPageController qua
        this.tablechoice = tablechoice;

        // Đặt xử lý sự kiện khi click vào checkbox
        this.check.setOnAction(this::Select);
    }

    private void Select(ActionEvent event) {
        if (check.isSelected()) // Nếu checkbox được chọn(trạng thái là true)
        {
            String selectedword = getWord(); // Lấy giá trị từ ô từ vựng được chọn
            String tablechoice = getTablechoice(); // Lấy giá trị từ bảng được chọn
            function.UpdateSelectBox(tablechoice, 1, selectedword); //gọi Pt updateselectbox của function với tham số tablechois và tráng thái là 1 và selectedword được chọn
            
        } else // nếu checkBox không chọn và trạng thái là false
        {
            String unselectedword = getWord(); // Lấy giá trị của từ ở ô bị bỏ select
            String tablechoice = getTablechoice(); // Lấy giá trị của bảng ở ô bị bỏ select
            function.UpdateSelectBox(tablechoice, 0, unselectedword);//gọi Pt updateselectbox của function với tham số tablechois và tráng thái là 1 và selectedword được chọn
        }
    }
    
       public CheckBox getCheck() { //get để lấy giá trị
        return check;
    }

    public String getWord() {
        return word;
    }

    public String getPronounce() {
        return pronounce;
    }

    public String getType() {
        return type;
    }

    public String getDefinition() {
        return definition;
    }

    public String getTablechoice() {
        return tablechoice;
    }
    

    public void setCheck(CheckBox check) { //set để thiết lập giá trị
        this.check = check;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setTableChoice(String tablechoice) {
        this.tablechoice = tablechoice;
    }

    
}
