package Control;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TranslateThis_DanhSachTuPageController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TableView<ListView> table;

    @FXML
    private TableColumn<ListView, Integer> col_check;

    @FXML
    private TableColumn<ListView, String> col_word;

    @FXML
    private TableColumn<ListView, String> col_pronounce;

    @FXML
    private TableColumn<ListView, String> col_type;

    @FXML
    private TableColumn<ListView, String> col_definition;

    @FXML
    private CheckBox SelectAllButton;

    @FXML
    private ComboBox<String> tablelist;

    @FXML
    private TextField wordsearch;

    @FXML
    private ComboBox<String> typelist;

    // tạo luồng định kì, tự động cập nhật bảng
    private ScheduledExecutorService auto;

    //Tạo CountDownLach để thực hiện toàn bộ task
    private CountDownLatch latch = new CountDownLatch(1);

    // Đặt giá trị mặc định của table là bảng AnhViet
    private String tablechoice = "AnhViet";

    // Đặt giá trị mặc định của wordsearch là % - tìm kiếm mọi từ
    private String searchword = "%";

    // Đặt giá trị mặc định cho type là % - tìm kiếm mọi từ
    private String typechoice = "%";

    // Khởi tạo đối tượng TranslateThis_Function để gọi hàm
    TranslateThis_Function function = new TranslateThis_Function();

    // Tạo ObservableList để lưu trữ thông tin từ hàm GetWord 
    ObservableList<ListView> wordlist;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Trước khi chạy thì reset selectBox [check] về bằng 0
        function.ResetSelectBox("AnhViet");
        function.ResetSelectBox("AnhAnh");

        // Bảng chọn table
        ObservableList<String> tableoption = FXCollections.observableArrayList("Anh-Việt", "Anh-Anh");
        tablelist.setItems(tableoption);
        tablelist.setValue("Anh-Việt");

        // Bảng chọn type
        ObservableList<String> typeoption = FXCollections.observableArrayList("Toàn Bộ", "Danh Từ", "Động Từ", "Tính Từ", "Trạng Từ", "Chưa phân loại");
        typelist.setItems(typeoption);
        typelist.setValue("Toàn Bộ");

        //Luồng Tự động cập nhập bảng
        AutoUpdateList();

        // Lấy dữ liệu từ table đặt vào từng cột
        col_check.setCellValueFactory(new PropertyValueFactory<>("check"));
        col_word.setCellValueFactory(new PropertyValueFactory<>("word"));
        col_pronounce.setCellValueFactory(new PropertyValueFactory<>("pronounce"));
        col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        col_definition.setCellValueFactory(new PropertyValueFactory<>("definition"));

    }

    // Sự kiện nhấn vào nút SelectAllButton
    @FXML
    private void SelectAll(ActionEvent event) {
        if (SelectAllButton.isSelected())
        { // Trường hợp checkbox được chọn (giá trị là true)
            // Đặt giá trị chọn cho tất cả các đối tượng trong wordlist
            for (ListView Word : wordlist)
            {
                Word.getCheck().setSelected(true);
            }
            // Gọi hàm cập nhập nhiều checkbox
            function.UpdateAllSelectBox(tablechoice, 1, searchword, typechoice);
        } else
        {
            for (ListView Word : wordlist)
            { // Trường hợp checkbox không được chọn (giá trị là false)
                // Đặt giá trị không chọn cho tất cả các đối tượng trong wordlist
                Word.getCheck().setSelected(false); 
            }
            // Gọi hàm cập nhập nhiều checkbox
            function.UpdateAllSelectBox(tablechoice, 0, searchword, typechoice);
        }
    }

    // Sự kiện nhấn vào ComboBox tablelist  
    @FXML
    private void TableChoice(ActionEvent event) {
        // Lưu giá trị hiện tại của ComboBox vào biến selectedmode
        String selectmode = tablelist.getValue();

        if (selectmode.equals("Anh-Việt")) // Nếu selectedmode có giá trị = Anh-Việt 
        {
            tablechoice = "AnhViet";
            // Reset tất cả check về bằng 0
            function.ResetSelectBox(tablechoice);

        } else if (selectmode.equals("Anh-Anh")) // Nếu selectedmode có giá trị = Anh-Anh 
        {
            tablechoice = "AnhAnh"; // Đặt lại biến tablechoice để truyền qua tới câu lệnh SQL
            // Reset tất cả check về bằng 0
            function.ResetSelectBox(tablechoice);
        }
    }

    // Sự kiện nhập vào TextField wordsearch
    @FXML
    private void WordSearch(KeyEvent event) {
        // Trước khi chạy thì reset tất cả check về bằng 0
        function.ResetSelectBox(tablechoice);

        searchword = wordsearch.getText().trim() + "%"; // Lưu giá trị nhập vào TextField vào biến searchword + cắt space ở đầu cuối + thêm % để tìm kiếm từ gần đúng

        // Nếu giá trị nhập vào trống
        if (searchword.isEmpty())
        {
            searchword = "%"; // Tìm kiếm mọi từ
        }
    }

    // Sự kiện nhấn vào ComboBox typelist
    @FXML
    private void TypeChoice(ActionEvent event) {
        // Trước khi chạy thì reset tất cả check về bằng 0
        function.ResetSelectBox(tablechoice);

        String selecttype = typelist.getValue(); //lấy giá trị ở 'type' được chọn từ comboBox 'typelist'
        switch (selecttype)
        {
            case "Toàn Bộ" ->
                typechoice = "%";
            case "Danh Từ" ->
                typechoice = "noun";
            case "Động Từ" ->
                typechoice = "verb";
            case "Tính Từ" ->
                typechoice = "adjective";
            case "Trạng Từ" ->
                typechoice = "adverb";
            case "Chưa phân loại" ->
                typechoice = "null";
            default ->
            {
            }
        }
    }

    // Sự kiện nhấn vào nút Sửa
    @FXML
    private void Edit(ActionEvent event) {
        // Gọi hàm CountSelectBox để lấy số lượng cột đang được chọn
        Integer count = function.CountSelectBox(tablechoice);

        if (count == 0) // Nếu không có cột nào đang được chọn - count = 0 
        {
            AlertNoSelect();
        } else if (count > 1)
        {
            AlertManySelect(); // Nếu có nhiều hơn 1 cột đang được chọn - count > 1
        } else
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/TranslateThis_DanhSachTuEditPage.fxml"));
                Parent root = loader.load();

                // Truyền giá trị tablechoice cho EditPage controller
                TranslateThis_DanhSachTuEditPageController editpagecontroller = loader.getController();
                editpagecontroller.SetTableChoice(tablechoice);

                // Tạo cửa sổ mới
                Stage currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Stage editstage = new Stage();
                editstage.initModality(Modality.APPLICATION_MODAL); // chặn người dùng tương tác với trang chính
                editstage.initOwner(currentstage);
                editstage.setScene(new Scene(root));
                editstage.setTitle("Chỉnh Sửa");
                editstage.showAndWait();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Sự kiện nhấn vào nút Thêm
    @FXML
    private void Add(ActionEvent event) {
        try
        {
            // dối tượng FXMLLoader để tại giao diện UI của trang Adđ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/TranslateThis_DanhSachTuAddPage.fxml"));
            Parent root = loader.load();

            // Truyền giá trị tablechoice cho EditPage controller
            TranslateThis_DanhSachTuAddPageController addpagecontroller = loader.getController();
            addpagecontroller.SetTableChoice(tablechoice);

            // Tạo cửa sổ mới
            Stage currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage editstage = new Stage();
            editstage.initModality(Modality.APPLICATION_MODAL); // chặn người dùng tương tác với trang chính
            editstage.initOwner(currentstage);
            editstage.setScene(new Scene(root));
            editstage.setTitle("Thêm");
            editstage.showAndWait();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    // sự kiện nhấn vào nút xóa
    @FXML
    private void Delete(ActionEvent event) {
        // Gọi hàm CountSelectBox để lấy số lượng cột đang được chọn
        Integer count = function.CountSelectBox(tablechoice);

        if (count == 0) // Nếu không có cột nào đang được chọn - count = 0 
        {
            AlertNoSelect();
        } else
        {
            ConfirmDeleteSelect();
        }

    }
    
    @FXML
    private void AddSystemData(ActionEvent event) {
        // Gọi hàm CountAddSysTemData để lấy số lượng từ vựng sẽ được thêm vào
        Integer count = function.CountAddSysTemData(tablechoice);
        
        Alert confirminsertsystemdata = new Alert(Alert.AlertType.CONFIRMATION);
        confirminsertsystemdata.setTitle("LƯU Ý");
        confirminsertsystemdata.setHeaderText("Bạn có muốn thêm " + count + " từ vựng mặc định từ hệ thống vào danh sách từ vựng");
        ImageView icon = new ImageView("/image/warning.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        confirminsertsystemdata.getDialogPane().setGraphic(icon);

        Optional<ButtonType> choiceinsert = confirminsertsystemdata.showAndWait();

        if (choiceinsert.isPresent() && choiceinsert.get() == ButtonType.OK)
        {
            boolean insertsuccessful = function.AddSystemData(tablechoice);

            // Hiển thị thông báo trạng thái xóa thông tin
            if (insertsuccessful)
            {
                AlertInsertSystemDataSuccess();
            } else if (insertsuccessful)
            {
                AlertInsertSystemDataFail();
            }
        }
    }

    // Hàm cập nhật luồng 
    private void AutoUpdateList() { 
        auto = Executors.newScheduledThreadPool(1);

        auto.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try
                {
                    wordlist = function.GetWordForTable(tablechoice, searchword, typechoice);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (wordlist != null && !wordlist.isEmpty()) // Trường hợp wordlist có thông tin
                            {
                                // Lấy dữ liệu bỏ vào trong TableView
                                table.setItems(wordlist);
                            } else
                            {
                                table.getItems().clear();
                            }
                        }
                    });

                    // Countdown latch
                    latch.countDown();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
    
    // hàm thông báo 
    private void AlertNoSelect() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LỖI");
        alert.setHeaderText("Không có từ vựng nào đang được chọn");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // hàm thông báo
    private void AlertManySelect() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LỖI");
        alert.setHeaderText("Hạn chế chỉnh sửa nhiều từ vựng cùng 1 lúc");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    private void ConfirmDeleteSelect() {
        // Gọi hàm CountSelectBox để lấy số lượng cột đang được chọn
        Integer count = function.CountSelectBox(tablechoice);

        Alert confirmdelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmdelete.setTitle("LƯU Ý");
        confirmdelete.setHeaderText("Bạn có muốn xóa " + count + " từ khỏi danh sách từ vựng");
        ImageView icon = new ImageView("/image/warning.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        confirmdelete.getDialogPane().setGraphic(icon);

        Optional<ButtonType> choicedelete = confirmdelete.showAndWait();

        if (choicedelete.isPresent() && choicedelete.get() == ButtonType.OK)
        {
            boolean deletesuccessful = function.DeleteWordInformation(tablechoice);

            // Hiển thị thông báo trạng thái xóa thông tin
            if (deletesuccessful)
            {
                AlertDeleteSuccess();
            } else if (deletesuccessful)
            {
                AlertDeleteFail();
            }
        }
    }

    // hàm thông báo
    private void AlertDeleteSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Xóa thông tin thành công");
        ImageView icon = new ImageView("/image/success.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // hàm thông báo
    private void AlertDeleteFail() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Xóa thông tin thất bại");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
    
     private void AlertInsertSystemDataSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Thêm thông tin mặc định từ hệ thống thành công");
        ImageView icon = new ImageView("/image/success.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
    
    private void AlertInsertSystemDataFail() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Thêm thông tin mặc định từ hệ thống thất bại");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

}
